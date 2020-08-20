package com.buendiagon.uismap;

import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.android.material.textfield.TextInputEditText;

import java.io.Console;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String TAG = MapsActivity.class.getSimpleName();

    private MaterialButton btnDelete;

    private Marker currentMarker = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        btnDelete = findViewById(R.id.btn_delete);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMarker != null) {
                    currentMarker.remove();
                    currentMarker = null;
                } else {
                    Log.e(TAG, "No hay marcadores seleccionados");
                }
            }
        });
//        setupAutocomplete();
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
        GroundOverlay groundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions().image(uis_map).positionFromBounds(uisArea).transparency(0.1f));

        LatLng uisPosition = new LatLng(7.140366, -73.120573);
//        mMap.addMarker(new MarkerOptions().position(start).title("Marker in start"));
//        mMap.addMarker(new MarkerOptions().position(end).title("Marker in end"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uisPosition, 17));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(MapsActivity.this);
                final View view = View.inflate(MapsActivity.this, R.layout.custom_dialog, null);
                final LatLng position = latLng;
                dialogBuilder.setTitle("Node insert").setView(view).setPositiveButton("Insert", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextInputEditText nodeName = view.findViewById(R.id.node_name);
                        currentMarker = null;
                        mMap.addMarker(new MarkerOptions().position(position).title(nodeName.getText().toString()).draggable(true));
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (currentMarker == marker) {
                    currentMarker = null;
                } else if(currentMarker == null) {
                    currentMarker = marker;
                }else{
                    Polyline polyline = googleMap.addPolyline(new PolylineOptions().clickable(false).add(currentMarker.getPosition(),marker.getPosition()));
                    currentMarker = null;
                }
                return false;
            }
        });


    }

}