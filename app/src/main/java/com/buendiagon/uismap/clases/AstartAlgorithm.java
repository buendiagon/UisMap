package com.buendiagon.uismap.clases;

import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AstartAlgorithm {

    private static final String TAG = "A start Algorithm";

    private static float calculateDistance(LatLng from, LatLng to) {
        float[] results = new float[1];
        Location.distanceBetween(from.latitude, from.longitude, to.latitude, to.longitude, results);
        return results[0];
    }

    public static Node calculateShortestPath(Node start, Node goal) {
        Set<Node> openSet = new HashSet<>();
        Set<Node> closedSet = new HashSet<>();
        openSet.add(start);
        start.setG(0);

        while (!openSet.isEmpty()) {
            Node current = start;
            for (Node node : openSet) {
                current = (current.getF() < node.getF()) ? current : node;
            }

            if (current == goal) {
                Log.e(TAG, "Done!!!!");
                return goal;
            }

            openSet.remove(current);
            closedSet.add(current);
            for (Node neighbor : current.getAdjacentNodes().keySet()) {
                if (!closedSet.contains(neighbor)) {
                    float tentative_gScore = current.getG() + current.getAdjacentNodes().get(neighbor);
                    Log.e(TAG, "tentative: " + neighbor.getId());

                    if (tentative_gScore < neighbor.getG()) {
                        neighbor.setPreviousNode(current);
                        neighbor.setG(tentative_gScore);
                        neighbor.setF(neighbor.getG() + calculateDistance(new LatLng(neighbor.getLat(), neighbor.getLng()), new LatLng(goal.getLat(), goal.getLng())));
                        openSet.add(neighbor);
                    }
                }
            }
        }
        return null;
    }

}
