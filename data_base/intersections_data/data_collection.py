import osmnx as ox
import pandas as pd
import matplotlib.pyplot as plt
from geopy.geocoders import Nominatim


def get_intersections(save_csv=True):
    """Получает перекрестки (автомобильные и пешеходные) района Академическое и возвращает DataFrame.

    Args:
        save_csv (bool): Сохранять ли данные в CSV. По умолчанию True.

    Returns:
        tuple: (graph_drive, graph_walk, area, intersections_df) -
        графы дорог, границы района, DataFrame с перекрестками
    """
    # 1. Определяем границы района
    geolocator = Nominatim(user_agent="academic_district")
    location = geolocator.geocode("Академическое, Санкт-Петербург, Россия")
    area = ox.geocode_to_gdf(location.address, which_result=1)

    # 2. Скачиваем дорожную сеть (автомобильную и пешеходную) в границах района
    graph_drive = ox.graph_from_polygon(
        area.geometry.iloc[0],
        network_type="drive",
        simplify=True
    )

    graph_walk = ox.graph_from_polygon(
        area.geometry.iloc[0],
        network_type="walk",
        simplify=True
    )

    # 3. Извлекаем перекрестки из автомобильной сети
    intersections = []
    for node, data in graph_drive.nodes(data=True):
        if "street_count" in data and data["street_count"] >= 2:
            intersections.append({
                "id": node,
                "latitude": data["y"],
                "longitude": data["x"],
                "street_count": data["street_count"],
                "type": "car"
            })

    # 4. Извлекаем пешеходные перекрестки
    walk_nodes = set(graph_walk.nodes())
    for node, data in graph_walk.nodes(data=True):
        # Учитываем только узлы, которых нет в автомобильной сети
        if node not in graph_drive.nodes() and "street_count" in data and data["street_count"] >= 2:
            intersections.append({
                "id": node,
                "latitude": data["y"],
                "longitude": data["x"],
                "street_count": data["street_count"],
                "type": "walk"
            })

    intersections_df = pd.DataFrame(intersections)

    if save_csv:
        intersections_df.to_csv("district_intersections.csv", index=False, encoding="utf-8")
        print(f"Найдено {len(intersections)} перекрестков. Данные сохранены в CSV.")

    return graph_drive, graph_walk, area, intersections_df


def visualization(graph_drive=None, graph_walk=None, area=None, intersections_df=None):
    """Визуализирует дорожную сеть и перекрестки (автомобильные и пешеходные).

    Args:
        graph_drive: Граф автомобильной сети (если None, будет загружен заново)
        graph_walk: Граф пешеходной сети (если None, будет загружен заново)
        area: Границы района (если None, будет загружен заново)
        intersections_df: DataFrame с перекрестками (если None, будет загружен заново)
    """
    # Если данные не предоставлены, загружаем их
    if graph_drive is None or graph_walk is None or area is None or intersections_df is None:
        graph_drive, graph_walk, area, intersections_df = get_intersections(save_csv=False)

    # Настройка визуализации
    ox.settings.log_console = True
    ox.settings.default_crs = "EPSG:4326"

    # Визуализация автомобильной сети
    fig, ax = ox.plot_graph(
        graph_drive,
        bgcolor="white",
        node_size=0,
        edge_linewidth=0.5,
        edge_color="gray",
        show=False,
        close=False
    )

    # Визуализация пешеходной сети
    ox.plot_graph(
        graph_walk,
        ax=ax,
        node_size=0,
        edge_linewidth=0.3,
        edge_color="lightblue",
        show=False,
        close=False
    )

    # Добавляем автомобильные перекрестки
    car_intersections = intersections_df[intersections_df["type"] == "car"]
    ax.scatter(
        car_intersections["longitude"],
        car_intersections["latitude"],
        c="red",
        s=10,
        alpha=0.7,
        label="Автомобильные перекрестки"
    )

    # Добавляем пешеходные перекрестки
    walk_intersections = intersections_df[intersections_df["type"] == "walk"]
    ax.scatter(
        walk_intersections["longitude"],
        walk_intersections["latitude"],
        c="green",
        s=10,
        alpha=0.7,
        label="Пешеходные перекрестки"
    )

    # Добавляем границы района
    area.plot(ax=ax, facecolor="none", edgecolor="blue", linewidth=2, label="Район")

    plt.savefig("district_intersections_graph.png", dpi=300, bbox_inches='tight')
    print("График сохранён как 'district_intersections_graph.png'")
    plt.close()


# Получаем данные и визуализируем
graph_drive, graph_walk, area, df = get_intersections()
visualization(graph_drive, graph_walk, area, df)
