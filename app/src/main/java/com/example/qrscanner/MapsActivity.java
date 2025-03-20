package com.example.qrscanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.qrscanner.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Location currentLocation;
    private Marker marker;
    private FusedLocationProviderClient fusedClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private static final int REQUEST_CODE = 101;
    private AutocompleteSupportFragment autocompleteFragment;
    private boolean isMapReady = false;
    private boolean isLocationAvailable = false;
    private double pendingLat = 0, pendingLng = 0;
    private boolean pendingIsRoute = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String apiKey = getString(R.string.google_maps_key);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        PlacesClient placesClient = Places.createClient(this);

        autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG));
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    LatLng latLng = place.getLatLng();
                    if (latLng != null) {
                        updateMapLocation(latLng, place.getAddress());
                    }
                }

                @Override
                public void onError(@NonNull com.google.android.gms.common.api.Status status) {
                    Log.e("PlacesError", "Błąd wyszukiwania miejsca: " + status.getStatusMessage(), new NullPointerException());
                }
            });
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        setupLocationUpdates();

        Intent intent = getIntent();
        if (intent != null) {
            double placeLat = intent.getDoubleExtra("place_lat", 0);
            double placeLng = intent.getDoubleExtra("place_lng", 0);
            boolean isRoute = intent.getBooleanExtra("is_route", false); // Domyślnie false

            if (placeLat != 0 && placeLng != 0) {
                if (isMapReady && isLocationAvailable) {
                    if (isRoute) {
                        setRouteToDestination(placeLat, placeLng);
                    } else {
                        setWaypoint(placeLat, placeLng);
                    }
                } else {
                    pendingLat = placeLat;
                    pendingLng = placeLng;
                    pendingIsRoute = isRoute;
                }
            }
        }
    }

    private void updateMapLocation(LatLng latLng, String title) {
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
            if (marker != null) {
                marker.remove();
            }
            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
    }


    private void setupLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //locationRequest.setInterval(5000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    currentLocation = location;
                    isLocationAvailable = true;

                    if (isMapReady && pendingLat != 0 && pendingLng != 0) {
                        if (pendingIsRoute) {
                            setRouteToDestination(pendingLat, pendingLng);
                        } else {
                            setWaypoint(pendingLat, pendingLng);
                        }
                        pendingLat = 0;
                        pendingLng = 0;
                    }

                    if (mMap != null) {
                        updateMapLocation();
                    }
                }
            }
        };

        fusedClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void updateMapLocation() {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        isMapReady = true;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        if (isLocationAvailable && pendingLat != 0 && pendingLng != 0) {
            if (pendingIsRoute) {
                setRouteToDestination(pendingLat, pendingLng);
            } else {
                setWaypoint(pendingLat, pendingLng);
            }
            pendingLat = 0;
            pendingLng = 0;
        }
        // Pobranie ustawień UI Mapy
        UiSettings uiSettings = mMap.getUiSettings();
        // Przesunięcie przycisku centrowania mapy (padding: lewa, góra, prawa, dół)
        mMap.setPadding(0, 10, 40, 0); // Przesunięcie w dół o 200dp

        // Wyłączenie kompasu, jeśli nie jest potrzebny
        uiSettings.setCompassEnabled(false);

        LatLng Lodz = new LatLng(51.759248, 19.455983);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Lodz, 12));

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title("Custom Marker")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mMap.addMarker(markerOptions);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupLocationUpdates();
        }
    }

    private void setWaypoint(double lat, double lng)
    {
        if (mMap == null && currentLocation == null) return;
        Toast.makeText(this, "Robie pinezke", Toast.LENGTH_SHORT).show();
        LatLng waypoint = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions()
                .position(waypoint)
                .title("Punkt pośredni")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(waypoint, 12));
    }

    private void setRouteToDestination(double destLat, double destLng) {
        if (mMap == null || currentLocation == null) return;

        Toast.makeText(this, "Rysowanie trasy...", Toast.LENGTH_SHORT).show();

        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        LatLng destination = new LatLng(destLat, destLng);

        // Dodanie markera dla celu podróży
        mMap.addMarker(new MarkerOptions()
                .position(destination)
                .title("Cel podróży")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 12));

        // Pobranie trasy z Google Directions API
        String url = getDirectionsUrl(currentLatLng, destination);
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray routes = response.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                            String points = overviewPolyline.getString("points");

                            List<LatLng> decodedPath = PolyUtil.decode(points);
                            mMap.addPolyline(new PolylineOptions().addAll(decodedPath).width(10).color(Color.RED));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Błąd parsowania trasy!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Błąd pobierania trasy!", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }

    /**
     * Generuje URL do pobrania trasy z Google Directions API
     */
    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String apiKey = getString(R.string.google_maps_key); // Upewnij się, że masz poprawny klucz API w `strings.xml`
        return "https://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + origin.latitude + "," + origin.longitude
                + "&destination=" + dest.latitude + "," + dest.longitude
                + "&mode=walking"
                + "&key=" + apiKey;
    }


}