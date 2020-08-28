package com.buendiagon.uismap;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.buendiagon.uismap.clases.AstartAlgorithm;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private String TAG = MapsActivity.class.getSimpleName();

    private UisMapSqliteHelper db;
    private Map<Integer, Node> nodes;
    private List<Integer> interestPoints;

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
        nodes = new HashMap<>();
        interestPoints = new ArrayList<>();
        setupDB();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        nodes = db.getGraph(this);
        interestPoints = db.getInterestPoints(this);
        setupMap();
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

        for (int index : interestPoints) {
            Node node = nodes.get(index);
            if (node != null) {
                LatLng position = new LatLng(node.getLat(), node.getLng());
                mMap.addMarker(new MarkerOptions().title(node.getName()).position(position)).setTag(node.getId());
            }
        }
    }

    private void setupDB() {
        db = new UisMapSqliteHelper(this);
        try {
            db.createDataBase();
            db.openDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Polyline polyline;

    private void loadPath(int start, int end) {
        Log.e(TAG, "start: " + start + "---- end: " + end);
        List<Node> totalPath = AstartAlgorithm.calculateShortestPath(mMap, nodes.get(start), nodes.get(end));
        if (polyline != null) {
            polyline.remove();
            polyline = null;
        }
        if (totalPath == null) {
            Toast.makeText(this, "No hay camino", Toast.LENGTH_SHORT).show();
        } else {
            List<LatLng> path = new ArrayList<>();
            for (Node node : totalPath) {
                path.add(new LatLng(node.getLat(), node.getLng()));
            }
            polyline = mMap.addPolyline(new PolylineOptions().clickable(false).addAll(path).zIndex(10));
        }
    }

    Marker lastMarker = null;

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (lastMarker == null) {
            lastMarker = marker;
        } else {
            if (lastMarker.getTag() instanceof Integer && marker.getTag() instanceof Integer) {
                loadPath((Integer) lastMarker.getTag(), (Integer) marker.getTag());
            }
            lastMarker = null;
        }

        return false;
    }
}