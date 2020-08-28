package com.buendiagon.uismap.clases;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Node implements Comparable<Node> {
    private int id;
    private String name;
    private float lat;
    private float lng;

    // for algorithm
    private float f;
    private float g;
    private boolean visit;

    private Map<Node, Float> adjacentNodes = new HashMap<>();

    public void addDestination(Node destination, float distance){
        adjacentNodes.put(destination, distance);
    }

    public Node(int id, String name, float lat, float lng) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.f = Float.MAX_VALUE;
        this.g = Float.MAX_VALUE;
        this.visit = false;
    }

    public int getId() {
        return id;
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

    public float getLng() {
        return lng;
    }

    public Map<Node, Float> getAdjacentNodes() {
        return adjacentNodes;
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

    public boolean isVisit() {
        return visit;
    }

    public void setVisit(boolean visit) {
        this.visit = visit;
    }

    public void resetNode(){
        setVisit(false);
        setG(Float.MAX_VALUE);
        setF(Float.MAX_VALUE);
    }

    @Override
    public int compareTo(Node o) {
        return Float.compare(o.getF(), this.getF()) * (-1);
    }
}
