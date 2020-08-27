package com.buendiagon.uismap.clases;

import java.util.HashMap;
import java.util.Map;

public class Node {
    private int id;
    private String name;
    private float lat;
    private float lng;

    // for algorithm
    private Node previousNode;
    private float f;
    private float g;

    private Map<Node, Float> adjacentNodes = new HashMap<>();

    public void addDestination(Node destination, float distance){
        adjacentNodes.put(destination, distance);
    }

    public Node(int id, String name, float lat, float lng) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.previousNode = null;
        this.f = Float.MAX_VALUE;
        this.g = Float.MAX_VALUE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public Map<Node, Float> getAdjacentNodes() {
        return adjacentNodes;
    }

    public Node getPreviousNode() {
        return previousNode;
    }

    public void setPreviousNode(Node previousNode) {
        this.previousNode = previousNode;
    }

    public float getF() {
        return f;
    }

    public void setF(float f) {
        this.f = f;
    }

    public float getG() {
        return g;
    }

    public void setG(float g) {
        this.g = g;
    }
}
