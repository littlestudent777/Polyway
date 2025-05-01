import osmnx as ox
import pandas as pd
from geopy.geocoders import Nominatim
import csv


def get_pois(area_name, tags):
    """
    Получает точки интереса (POIs) в указанном районе.

    Args:
        area_name (str): Название района (например, "Академическое")
        tags (dict): Словарь тегов OSM (например, {"amenity": "pharmacy"})
        save_csv (bool): Сохранять ли данные в CSV. По умолчанию True.

    Returns:
        GeoDataFrame: Точки интереса в формате GeoDataFrame
    """
    # 1. Определяем границы района
    geolocator = Nominatim(user_agent="academic_district")
    location = geolocator.geocode(f"{area_name}, Санкт-Петербург, Россия")
    area = ox.geocode_to_gdf(location.address, which_result=1)

    # 2. Загружаем POIs в границах района
    pois = ox.features_from_polygon(
        area.geometry.iloc[0],
        tags=tags
    )

    # Фильтруем и сохраняем данные
    pois = pois[pois.geometry.type == "Point"]  # Только точки (без полигонов)
    pois = pois[["name", "geometry"]].reset_index(drop=True)

    # Удаляем точки без названия
    pois = pois[pois["name"].notna() & (pois["name"] != "")]

    # Добавляем координаты
    pois["latitude"] = pois.geometry.y
    pois["longitude"] = pois.geometry.x

    # Указываем явные параметры для корректного сохранения кириллицы
    pois[["name", "latitude", "longitude"]].to_csv(
        f"{area_name}_{'_'.join(tags.values())}.csv",
        index=False,
        encoding='utf-8-sig',  # UTF-8 с BOM для совместимости с Excel
        sep=',',  # Разделитель - запятая
        escapechar='\\'  # Экранирование спецсимволов
    )
    print(f"Найдено {len(pois)} точек. Данные сохранены в CSV.")

    return pois


if __name__ == "__main__":
    # Аптеки
    pharmacies = get_pois(
        "Академическое",
        tags={"amenity": "pharmacy"}
    )

    # # Достопримечательности
    # attractions = get_pois(
    #     "Академическое",
    #     tags={"tourism": "attraction"}
    # )

    # # Объединение данных
    # all_pois = pd.concat([pharmacies, attractions])
    # all_pois.to_csv("Академическое_places.csv", index=False, encoding="utf-8")
    # print(f"Общее количество точек: {len(all_pois)}")
