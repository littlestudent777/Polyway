package com.example.polyway;

public class Edge {
    public int fromNode;
    public int toNode;
    public double distance;

    public Edge(int fromNode, int toNode, double distance) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.distance = distance;
    }
}