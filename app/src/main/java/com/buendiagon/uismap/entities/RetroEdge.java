package com.buendiagon.uismap.entities;

import com.google.gson.annotations.SerializedName;

public class RetroEdge {
    @SerializedName("id_edge")
    private Integer idEdge;
    @SerializedName("from_node")
    private Integer fromNode;
    @SerializedName("to_node")
    private Integer toNode;
    @SerializedName("weight")
    private Double weight;

    public RetroEdge(Integer idEdge, Integer fromNode, Integer toNode, Double weight) {
        this.idEdge = idEdge;
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.weight = weight;
    }

    public Integer getIdEdge() {
        return idEdge;
    }

    public void setIdEdge(Integer idEdge) {
        this.idEdge = idEdge;
    }

    public Integer getFromNode() {
        return fromNode;
    }

    public void setFromNode(Integer fromNode) {
        this.fromNode = fromNode;
    }

    public Integer getToNode() {
        return toNode;
    }

    public void setToNode(Integer toNode) {
        this.toNode = toNode;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
