package com.buendiagon.uismap.entities;

public class Node {
    private int id_node;
    private String node_info;
    private float lat;
    private float lng;

    public Node(int id_node, String node_info, float lat, float lng) {
        this.id_node = id_node;
        this.node_info = node_info;
        this.lat = lat;
        this.lng = lng;
    }

    public int getId_node() {
        return id_node;
    }

    public void setId_node(int id_node) {
        this.id_node = id_node;
    }

    public String getNode_info() {
        return node_info;
    }

    public void setNode_info(String node_info) {
        this.node_info = node_info;
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
}
