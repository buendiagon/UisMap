package com.buendiagon.uismap;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.buendiagon.uismap.clases.AstartAlgorithm;
import com.buendiagon.uismap.clases.Graph;
import com.buendiagon.uismap.clases.Node;
import com.buendiagon.uismap.data_base.UisMapSqliteHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String TAG = MapsActivity.class.getSimpleName();
    private static boolean flag = false;

    private UisMapSqliteHelper db;
    private List<Node> nodes;
    private Node finalNode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        db = new UisMapSqliteHelper(this);
        nodes = new ArrayList<>();
        setupDB();
//        setupAutocomplete();
    }

    private void setupDB() {
        db = new UisMapSqliteHelper(this);
        try {
            db.createDataBase();
            db.openDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Graph graph = db.getGraph(this);
        nodes = graph.getNodes();

        Node endNode = AstartAlgorithm.calculateShortestPath(nodes.get(0), nodes.get(200));
        if(endNode == null) {
            Toast.makeText(this, "No hay camino", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "No hay camino");
        }else{
            Log.e(TAG, "si hay camino");
            loadPath(endNode);
        }

    }

    private void loadPath(Node endNode) {
        if(!flag) {
            flag = true;
            finalNode = endNode;
            return;
        }
        while (endNode.getPreviousNode() != null){
            Log.e(TAG, endNode.getId() + "-" + endNode.getPreviousNode().getId());
            mMap.addPolyline(new PolylineOptions().clickable(false).add(new LatLng(endNode.getLat(), endNode.getLng()), new LatLng(endNode.getPreviousNode().getLat(), endNode.getPreviousNode().getLng())));
            endNode = endNode.getPreviousNode();
        }
    }


//    public void setupAutocomplete(){
//        String[] COUNTRIES = new String[] {"Item 1", "Item 2", "Item 3", "Item 4"};
//
//        ArrayAdapter<String> adapter =
//                new ArrayAdapter<>(
//                        this,
//                        R.layout.dropdown_menu_popup_item,
//                        COUNTRIES);
//
//        AutoCompleteTextView start_dropdown =
//                findViewById(R.id.start_dropdown);
//        start_dropdown.setAdapter(adapter);
//
//        AutoCompleteTextView end_dropdown =
//                findViewById(R.id.end_dropdown);
//        end_dropdown.setAdapter(adapter);
//    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        setupMap();
        loadPath(finalNode);
    }


    private void setupMap() {
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        try {
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "can't find style. Error: " + e);
        }
        LatLng start = new LatLng(7.138630, -73.123401);
        LatLng end = new LatLng(7.143180, -73.115532);

        final LatLngBounds uisArea = new LatLngBounds(start, end);
        mMap.setLatLngBoundsForCameraTarget(uisArea);
        mMap.setMinZoomPreference(16.5f);

        BitmapDescriptor uis_map = BitmapDescriptorFactory.fromResource(R.drawable.uis_map);
        mMap.addGroundOverlay(new GroundOverlayOptions().image(uis_map).positionFromBounds(uisArea).transparency(0.1f));

        LatLng uisPosition = new LatLng(7.140366, -73.120573);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uisPosition, 17));



    }

}