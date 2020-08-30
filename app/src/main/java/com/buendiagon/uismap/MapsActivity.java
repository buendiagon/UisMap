package com.buendiagon.uismap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.buendiagon.uismap.classes.AstartAlgorithm;
import com.buendiagon.uismap.classes.Node;
import com.buendiagon.uismap.data_base.UisMapSqliteHelper;
import com.buendiagon.uismap.util.Util;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private String TAG = MapsActivity.class.getSimpleName();

    // datos del algoritmo
    private UisMapSqliteHelper db;
    private Map<Integer, Node> nodes;
    private List<Node> totalPath;

    // Obtener ubicación en tiempo real
    private LatLng currentLocation;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private final int REQUEST_CODE = 22;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        ImageButton myLocation = findViewById(R.id.btn_location);
        myLocation.setOnClickListener(getMyLocation());
        db = new UisMapSqliteHelper(this);
        nodes = new HashMap<>();
        totalPath = new ArrayList<>();

        // Sirve para obtener la ubicación del usuario en tiempo real
        createLocationRequest();
        fusedLocationProviderClient = new FusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                Location location = locationResult.getLastLocation();
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                updatePath();
            }
        };
        setupDB();
    }

    private View.OnClickListener getMyLocation() {
        return v -> {
            stopLocationUpdates();
            startLocationUpdates();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));
                if (currentMarkerPosition != null)
                    currentMarkerPosition.remove();
                currentMarkerPosition = null;
            });
        };
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        // llamado a la base de datos que devuelve el grafo
        nodes = db.getGraph(this);

        setupMap();
        setupAutocomplete();
    }


    private void setupMap() {
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        // elimina todos los elementos visuales del mapa
        try {
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "can't find style. Error: " + e);
        }

        // Mueve la vista del mapa al centro de la UIS
        LatLng start = new LatLng(7.138630, -73.123401);
        LatLng end = new LatLng(7.143180, -73.115532);

        final LatLngBounds uisArea = new LatLngBounds(start, end);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uisArea.getCenter(), 0));
        mMap.setLatLngBoundsForCameraTarget(uisArea);
        mMap.setMinZoomPreference(16.5f);

        // dibuja el mapa de la UIS sobre google maps
        BitmapDescriptor uis_map = BitmapDescriptorFactory.fromResource(R.drawable.uis_map);
        mMap.addGroundOverlay(new GroundOverlayOptions().image(uis_map).positionFromBounds(uisArea).transparency(0.1f));

        stopLocationUpdates();
        startLocationUpdates();

    }

    private void setupDB() {
        // Crea la base de datos local si no existe
        db = new UisMapSqliteHelper(this);
        try {
            db.createDataBase();
            db.openDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupAutocomplete() {
        ArrayList<Node> items = new ArrayList<>(nodes.values());
        ArrayAdapter<Node> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.dropdown_menu_popup_item,
                        items);

        AutoCompleteTextView end_dropdown =
                findViewById(R.id.end_dropdown);
        end_dropdown.setAdapter(adapter);

        end_dropdown.setOnItemClickListener((parent, view, position, id) -> {
            if (currentLocation != null) {
                // busca el nodo más cercano a la posición actual del usuario
                Node nearestNode = Util.getNearestNode(nodes.values(), currentLocation);
                // llama al método para calcular la ruta y la dibuja
                loadPath(nearestNode.getId(), ((Node) parent.getAdapter().getItem(position)).getId(), true);
            } else
                Toast.makeText(this, "No se pudo detectar tu ubicación", Toast.LENGTH_SHORT).show();
            // esconde el teclado
            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert in != null;
            in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        });


    }

    private Polyline polyline;
    private boolean isRoute = false;

    private void loadPath(int start, int end, boolean withZoom) {
        totalPath.clear();
        Node startNode = nodes.get(start);
        Node endNode = nodes.get(end);

        // Llama al algoritmo A* para calcular la ruta más corta entre los dos puntos dados
        totalPath = AstartAlgorithm.calculateShortestPath(mMap, startNode, endNode);
        if (!withZoom && totalPath != null) {
            totalPath.add(0, endNode);
        }

        // limpia las rutas dibujadas en el mapa si las hay
        if (polyline != null) {
            polyline.remove();
            polyline = null;
        }
        if (totalPath == null) {
            Toast.makeText(this, "No hay camino", Toast.LENGTH_SHORT).show();
        } else {
            // dibuja la ruta en el mapa

            isRoute = true;
            List<LatLng> path = new ArrayList<>();
            for (Node node : totalPath) {
                path.add(new LatLng(node.getLat(), node.getLng()));
            }
            path.add(currentLocation);
            polyline = mMap.addPolyline(new PolylineOptions().clickable(false).addAll(path).zIndex(10));
            LatLng startLatLng = currentLocation;
            LatLng endLatLng = new LatLng(endNode.getLat(), endNode.getLng());
            if (startLatLng.longitude > endLatLng.longitude) {
                startLatLng = new LatLng(currentLocation.latitude, path.get(0).longitude);
                endLatLng = new LatLng(path.get(0).latitude, currentLocation.longitude);
            }
            if (startLatLng.latitude > endLatLng.latitude) {
                LatLng tempStart = startLatLng;
                LatLng tempEnd = endLatLng;
                startLatLng = new LatLng(tempEnd.latitude, tempStart.longitude);
                endLatLng = new LatLng(tempStart.latitude, tempEnd.longitude);
            }
            if (withZoom)
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(startLatLng, endLatLng), 100));
        }
    }

    private void updatePath() {
        if (totalPath.size() > 1) {
            // producto punto para medir distancias y si se encuentra en la ruta
            Node originNode = totalPath.get(totalPath.size() - 1);
            Node pathNode = totalPath.get(totalPath.size() - 2);

            LatLng pathVector = new LatLng(pathNode.getLat() - originNode.getLat(), pathNode.getLng() - originNode.getLng());
            LatLng locationVector = new LatLng(currentLocation.latitude - originNode.getLat(), currentLocation.longitude - originNode.getLng());
            double pathSquare = Math.pow(pathVector.latitude, 2) + Math.pow(pathVector.longitude, 2);
            double c = ((pathVector.latitude * locationVector.latitude) + (pathVector.longitude * locationVector.longitude)) / pathSquare;
            LatLng product = new LatLng((c * pathVector.latitude), (c * pathVector.longitude));
            double distanceNormal = Util.calculateDistance(product, locationVector);
            if ((c > 0 && c < 1 && distanceNormal < 15)) {
                List<LatLng> newPath = new ArrayList<>();
                totalPath.remove(totalPath.size() - 1);
                for (Node node : totalPath) {
                    newPath.add(new LatLng(node.getLat(), node.getLng()));
                }
                newPath.add(currentLocation);
                polyline.remove();
                polyline = mMap.addPolyline(new PolylineOptions().addAll(newPath));
            } else {
                loadPath(Util.getNearestNode(nodes.values(), currentLocation).getId(), totalPath.get(0).getId(), false);
            }
        } else if (isRoute) {
            isRoute = false;
            Toast.makeText(this, "Ha llegado a su destino", Toast.LENGTH_SHORT).show();
            totalPath.clear();
            if (polyline != null)
                polyline.remove();
            polyline = null;
            AstartAlgorithm.removePolyline();
        }
    }


    private Marker currentMarkerPosition;

    @Override
    public void onMapLongClick(LatLng latLng) {
        stopLocationUpdates();
        currentLocation = latLng;
        if (currentMarkerPosition != null) {
            currentMarkerPosition.remove();
            currentMarkerPosition = null;
        }
        currentMarkerPosition = mMap.addMarker(new MarkerOptions().title("Yo").position(latLng));
        updatePath();
    }


    // Todos los métodos de aquí para abajo son para obtener la ubicación del usuario

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, REQUEST_CODE);
            return;
        }
        if (mMap != null)
            mMap.setMyLocationEnabled(true);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}