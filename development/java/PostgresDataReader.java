package com.example.polyway;

import com.yandex.mapkit.geometry.Point;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgresDataReader {
    private Connection connection;
    private final String url;
    private final String user;
    private final String password;

    public PostgresDataReader(String url, String user, String password) throws SQLException {
        this.url = url;
        this.user = user;
        this.password = password;
        connect();
    }

    private void connect() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            throw new SQLException("Failed to connect to database: " + e.getMessage());
        }
    }

    public List<Point> loadNodes() throws SQLException {
        List<Point> nodes = new ArrayList<>();
        String query = "SELECT ST_X(geom) as lon, ST_Y(geom) as lat FROM nodes";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                double lat = rs.getDouble("lat");
                double lon = rs.getDouble("lon");
                nodes.add(new Point(lat, lon));
            }
        } catch (SQLException e) {
            throw new SQLException("Error loading nodes: " + e.getMessage());
        }
        return nodes;
    }

    public List<Edge> loadEdges() throws SQLException {
        List<Edge> edges = new ArrayList<>();
        String query = "SELECT start_node_id, end_node_id, distance FROM edges";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int fromNode = rs.getInt("start_node_id");
                int toNode = rs.getInt("end_node_id");
                double distance = rs.getDouble("distance");
                edges.add(new Edge(fromNode, toNode, distance));
            }
        } catch (SQLException e) {
            throw new SQLException("Error loading edges: " + e.getMessage());
        }
        return edges;
    }

    public List<Landmark> loadLandmarks() throws SQLException {
        List<Landmark> landmarks = new ArrayList<>();
        String query = "SELECT l.node_id, l.short_description, l.long_description, ST_X(n.geom) as lon, ST_Y(n.geom) as lat " +
                "FROM landmarks l JOIN nodes n ON l.node_id = n.node_id";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int nodeId = rs.getInt("node_id");
                String shortDescription = rs.getString("short_description");
                String longDescription = rs.getString("long_description");
                double lat = rs.getDouble("lat");
                double lon = rs.getDouble("lon");
                Point point = new Point(lat, lon);
                landmarks.add(new Landmark(nodeId, point, shortDescription, longDescription));
            }
        } catch (SQLException e) {
            throw new SQLException("Error loading landmarks: " + e.getMessage());
        }
        return landmarks;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static class Landmark {
        public int nodeId;
        public Point point;
        public String shortDescription;
        public String longDescription;

        public Landmark(int nodeId, Point point, String shortDescription, String longDescription) {
            this.nodeId = nodeId;
            this.point = point;
            this.shortDescription = shortDescription;
            this.longDescription = longDescription;
        }
    }
}