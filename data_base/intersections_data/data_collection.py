import osmnx as ox
import pandas as pd
import matplotlib.pyplot as plt
from geopy.geocoders import Nominatim


def get_intersections(save_csv=True):
    """Получает перекрестки района Академическое и возвращает DataFrame.

    Args:
        save_csv (bool): Сохранять ли данные в CSV. По умолчанию True.

    Returns:
        tuple: (graph, area, intersections_df) - граф дорог, границы района, DataFrame с перекрестками
    """
    # 1. Определяем границы района "Академическое"
    geolocator = Nominatim(user_agent="academic_district")
    location = geolocator.geocode("Академическое, Санкт-Петербург, Россия")
    area = ox.geocode_to_gdf(location.address, which_result=1)

    # 2. Скачиваем дорожную сеть в границах района
    graph = ox.graph_from_polygon(
        area.geometry.iloc[0],
        network_type="drive",
        simplify=True
    )

    # 3. Извлекаем перекрестки
    intersections = []
    for node, data in graph.nodes(data=True):
        if "street_count" in data and data["street_count"] >= 2:
            intersections.append({
                "id": node,
                "latitude": data["y"],
                "longitude": data["x"],
                "street_count": data["street_count"]
            })

    intersections_df = pd.DataFrame(intersections)

    if save_csv:
        intersections_df.to_csv("district_intersections.csv", index=False, encoding="utf-8")
        print(f"Найдено {len(intersections)} перекрестков. Данные сохранены в CSV.")

    return graph, area, intersections_df


def visualization(graph=None, area=None, intersections_df=None):
    """Визуализирует дорожную сеть и перекрестки.

    Args:
        graph: Граф дорожной сети (если None, будет загружен заново)
        area: Границы района (если None, будет загружен заново)
        intersections_df: DataFrame с перекрестками (если None, будет загружен заново)
    """
    # Если данные не предоставлены, загружаем их
    if graph is None or area is None or intersections_df is None:
        graph, area, intersections_df = get_intersections(save_csv=False)

    # Настройка визуализации
    ox.settings.log_console = True
    ox.settings.default_crs = "EPSG:4326"

    # Визуализация дорожной сети
    fig, ax = ox.plot_graph(
        graph,
        bgcolor="white",
        node_size=0,
        edge_linewidth=0.5,
        edge_color="gray",
        show=False,
        close=False
    )

    # Добавляем перекрестки
    ax.scatter(
        intersections_df["longitude"],
        intersections_df["latitude"],
        c="red",
        s=10,
        alpha=0.7,
        label="Перекрестки"
    )

    # Добавляем границы района
    area.plot(ax=ax, facecolor="none", edgecolor="blue", linewidth=2, label="Район")

    plt.savefig("district_intersections_graph.png", dpi=300, bbox_inches='tight')
    print("График сохранён как 'district_intersections_graph.png'")
    plt.close()


if __name__ == "__main__":
    # Можно вызвать только получение данных или только визуализацию

    graph, area, df = get_intersections()
    visualization(graph, area, df)