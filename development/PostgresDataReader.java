package com.example.polyway;

import com.yandex.mapkit.geometry.Point;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostgresDataReader {
    private final DataStore dataStore;

    public PostgresDataReader(String url, String user, String password) throws SQLException {
        Map<String, Object> params = new HashMap<>();
        params.put("dbtype", "postgis");
        params.put("host", "localhost");
        params.put("port", 5432);
        params.put("schema", "public");
        params.put("database", "PolyWay"); // Исправлено на правильное имя базы
        params.put("user", user); // Исправлено на "user"
        params.put("passwd", password); // Исправлено на "passwd"

        try {
            dataStore = DataStoreFinder.getDataStore(params);
            if (dataStore == null) {
                throw new SQLException("Не удалось подключиться к базе данных");
            }
        } catch (Exception e) {
            throw new SQLException("Ошибка подключения к базе данных: " + e.getMessage());
        }
    }

    public List<Point> loadNodes() throws SQLException {
        List<Point> nodes = new ArrayList<>();
        try {
            SimpleFeatureSource featureSource = dataStore.getFeatureSource("nodes");
            SimpleFeatureCollection collection = featureSource.getFeatures();
            try (SimpleFeatureIterator iterator = collection.features()) {
                while (iterator.hasNext()) {
                    SimpleFeature feature = iterator.next();
                    org.locationtech.jts.geom.Point geom = (org.locationtech.jts.geom.Point) feature.getDefaultGeometry();
                    double lat = geom.getY();
                    double lon = geom.getX();
                    nodes.add(new Point(lat, lon));
                }
            }
        } catch (Exception e) {
            throw new SQLException("Ошибка загрузки узлов: " + e.getMessage());
        }
        return nodes;
    }

    public List<Edge> loadEdges() throws SQLException {
        List<Edge> edges = new ArrayList<>();
        try {
            SimpleFeatureSource featureSource = dataStore.getFeatureSource("edges");
            SimpleFeatureCollection collection = featureSource.getFeatures();
            try (SimpleFeatureIterator iterator = collection.features()) {
                while (iterator.hasNext()) {
                    SimpleFeature feature = iterator.next();
                    int fromNode = ((Number) feature.getAttribute("start_node_id")).intValue();
                    int toNode = ((Number) feature.getAttribute("end_node_id")).intValue();
                    double distance = ((Number) feature.getAttribute("distance")).doubleValue();
                    edges.add(new Edge(fromNode, toNode, distance));
                }
            }
        } catch (Exception e) {
            throw new SQLException("Ошибка загрузки рёбер: " + e.getMessage());
        }
        return edges;
    }

    public void close() {
        if (dataStore != null) {
            dataStore.dispose();
        }
    }
}