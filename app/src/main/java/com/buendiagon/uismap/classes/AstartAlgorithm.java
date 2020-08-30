package com.buendiagon.uismap.classes;

import android.graphics.Color;
import android.util.Log;

import com.buendiagon.uismap.util.Util;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class AstartAlgorithm {

    private static final String TAG = "A start Algorithm";
    private static ArrayList<Polyline> polylineGreen = new ArrayList<>();
    private static List<Node> exploredNodes = new ArrayList<>();

    public static List<Node> calculateShortestPath(GoogleMap googleMap, Node start, Node goal) {
        removePolyline();
        Queue<Node> openSet = new PriorityQueue<>();
        Map<Node, Node> cameFrom = new HashMap<>();

        start.setG(0);
        start.setF(Util.calculateDistance(start, goal));

        openSet.add(start);
        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            Log.e(TAG, "Done!!!");
            assert current != null;
            if (current == goal) {
                Log.e(TAG, "Done!!!");
                current.resetNode();
                resetNodes(openSet.iterator());
                return reconstructPath(cameFrom, current);
            }
            current.setVisit(true);
            exploredNodes.add(current);
            for (Node neighbor : current.getAdjacentNodes().keySet()) {
                float tentative_gScore = current.getG() + current.getAdjacentNodes().get(neighbor);
                if (tentative_gScore < neighbor.getG() && !neighbor.isVisit()) {
                    polylineGreen.add(googleMap
                            .addPolyline(new PolylineOptions().clickable(true)
                                    .color(Color.rgb(0, 255, 0))
                                    .add(new LatLng(current.getLat(), current.getLng()), new LatLng(neighbor.getLat(), neighbor.getLng()))
                            ));
                    cameFrom.put(neighbor, current);
                    neighbor.setG(tentative_gScore);
                    neighbor.setF(neighbor.getG() + Util.calculateDistance(neighbor, goal));
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }
        resetNodes(null);
        return null;
    }

    private static void resetNodes(Iterator<Node> openNode) {
        for (Node node : exploredNodes) {
            node.resetNode();
        }
        if (openNode != null) {
            while (openNode.hasNext()) {
                openNode.next().resetNode();
            }
        }
        exploredNodes.clear();
    }

    private static List<Node> reconstructPath(Map<Node, Node> cameFrom, Node current) {
        List<Node> totalPath = new ArrayList<>();
        if (cameFrom.get(current) != null)
            current = cameFrom.get(current);
        while (cameFrom.containsKey(current)) {
            totalPath.add(current);
            current = cameFrom.get(current);
        }
        return totalPath;
    }

    public static void removePolyline(){
        for (Polyline polyline : polylineGreen) {
            polyline.remove();
        }
        polylineGreen.clear();
    }

}
