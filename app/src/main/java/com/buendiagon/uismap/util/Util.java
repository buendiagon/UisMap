package com.buendiagon.uismap.util;

import android.location.Location;

import com.buendiagon.uismap.classes.Node;
import com.google.android.gms.maps.model.LatLng;

import java.util.Collection;
import java.util.List;

public class Util{

    public static float calculateDistance(Node fromNode, Node toNode) {
        LatLng from = new LatLng(fromNode.getLat(), fromNode.getLng());
        LatLng to = new LatLng(toNode.getLat(), toNode.getLng());
        return calculateDistance(from, to);
    }

    public static Node getNearestNode (Collection<Node> nodes, LatLng currentLocation) {
        Node winner = null;
        float minDistance = Float.MAX_VALUE;
        for (Node node : nodes) {
            LatLng nodeLocation = new LatLng(node.getLat(), node.getLng());
            float distance = calculateDistance(nodeLocation, currentLocation);
            if(distance < minDistance) {
                minDistance = distance;
                winner = node;
            }
        }
        return winner;
    }

    public static float calculateDistance(LatLng from, LatLng to) {
        float[] results = new float[1];
        Location.distanceBetween(from.latitude, from.longitude, to.latitude, to.longitude, results);
        return results[0];
    }
}
