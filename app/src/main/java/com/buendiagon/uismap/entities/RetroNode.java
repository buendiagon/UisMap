package com.buendiagon.uismap.entities;

import com.google.gson.annotations.SerializedName;

public class RetroNode {
    @SerializedName("id_node")
    private Integer idNode;
    @SerializedName("node_info")
    private String nodeInfo;
    @SerializedName("lat")
    private Double lat;
    @SerializedName("lng")
    private Double lng;

    public RetroNode(Integer idNode, String nodeInfo, Double lat, Double lng) {
        this.idNode = idNode;
        this.nodeInfo = nodeInfo;
        this.lat = lat;
        this.lng = lng;
    }

    public Integer getIdNode() {
        return idNode;
    }

    public void setIdNode(Integer idNode) {
        this.idNode = idNode;
    }

    public String getNodeInfo() {
        return nodeInfo;
    }

    public void setNodeInfo(String nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
