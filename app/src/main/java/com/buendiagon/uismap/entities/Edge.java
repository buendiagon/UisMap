package com.buendiagon.uismap.entities;

public class Edge {
    private int id_edge;
    private int from_node;
    private int to_node;
    private float weight;

    public Edge(int id_edge, int from_node, int to_node, float weight) {
        this.id_edge = id_edge;
        this.from_node = from_node;
        this.to_node = to_node;
        this.weight = weight;
    }

    public int getId_edge() {
        return id_edge;
    }

    public void setId_edge(int id_edge) {
        this.id_edge = id_edge;
    }

    public int getFrom_node() {
        return from_node;
    }

    public void setFrom_node(int from_node) {
        this.from_node = from_node;
    }

    public int getTo_node() {
        return to_node;
    }

    public void setTo_node(int to_node) {
        this.to_node = to_node;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
