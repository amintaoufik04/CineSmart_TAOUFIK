package com.example.cinesmart_taoufik;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.cinesmart_taoufik.api.GooglePlacesApi;
import com.example.cinesmart_taoufik.models.PlaceResult;
import com.example.cinesmart_taoufik.models.PlacesApiResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NearbyCinemasActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String[]> locationPermissionLauncher;
    private LinearLayout cinemasListLayout;
    private TextView tvStatus;
    private ProgressBar progressBar;
    private String apiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_cinemas);

        apiKey = getString(R.string.google_maps_key);

        cinemasListLayout = findViewById(R.id.layout_cinemas_list);
        tvStatus = findViewById(R.id.tv_status);
        progressBar = findViewById(R.id.progress_bar);

        findViewById(R.id.btn_back_fav).setOnClickListener(v -> finish());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean fine = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarse = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    if (Boolean.TRUE.equals(fine) || Boolean.TRUE.equals(coarse)) {
                        getLocationAndLoadCinemas();
                    } else {
                        tvStatus.setText("Permission de localisation refusée");
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            getLocationAndLoadCinemas();
        } else {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void getLocationAndLoadCinemas() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        tvStatus.setText("Recherche de votre position...");
        progressBar.setVisibility(View.VISIBLE);

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(2000)
                .setMaxUpdates(1) // On s'arrête dès qu'on a une position
                .build();

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    updateUIWithLocation(locationResult.getLastLocation());
                } else {
                    tvStatus.setText("Localisation impossible. Vérifiez vos paramètres.");
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, getMainLooper());
    }

    private void requestFreshLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        CancellationTokenSource tokenSource = new CancellationTokenSource();
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, tokenSource.getToken())
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        updateUIWithLocation(location);
                    } else {
                        tvStatus.setText("Localisation introuvable. Vérifiez votre GPS.");
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    tvStatus.setText("Erreur de localisation: " + e.getMessage());
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void updateUIWithLocation(android.location.Location location) {
        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.clear(); // Nettoie les anciens marqueurs
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 10f));
        googleMap.addMarker(new MarkerOptions()
                .position(userLatLng)
                .title("Ma position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        
        searchNearbyCinemas(userLatLng);
    }

    private void searchNearbyCinemas(LatLng userLatLng) {
        tvStatus.setText("Recherche des cinémas à proximité...");
        progressBar.setVisibility(View.VISIBLE);
        cinemasListLayout.removeAllViews(); // Vider la liste avant nouvelle recherche

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GooglePlacesApi placesApi = retrofit.create(GooglePlacesApi.class);

        String location = userLatLng.latitude + "," + userLatLng.longitude;

        placesApi.getNearbyCinemas(location, 50000, null, "cinema", apiKey)
                .enqueue(new Callback<PlacesApiResponse>() {
                    @Override
                    public void onResponse(Call<PlacesApiResponse> call, Response<PlacesApiResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            PlacesApiResponse apiResponse = response.body();
                            List<PlaceResult> results = apiResponse.getResults();
                            String status = apiResponse.getStatus();

                            if (results != null && !results.isEmpty()) {
                                int count = Math.min(results.size(), 20);
                                tvStatus.setText(count + " cinéma(s) trouvé(s)");
                                for (int i = 0; i < count; i++) {
                                    addCinemaMarkerAndListItem(results.get(i), i);
                                }
                            } else {
                                if ("ZERO_RESULTS".equals(status)) {
                                    tvStatus.setText("Aucun cinéma trouvé (Rayon: 50km)");
                                } else if ("REQUEST_DENIED".equals(status)) {
                                    String detail = apiResponse.getErrorMessage() != null ? apiResponse.getErrorMessage() : "Vérifiez la clé API dans Google Cloud Console";
                                    tvStatus.setText("Erreur: Accès refusé (" + detail + ")");
                                } else {
                                    tvStatus.setText("Erreur Google: " + status);
                                }
                            }
                        } else {
                            tvStatus.setText("Erreur API: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<PlacesApiResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        tvStatus.setText("Erreur réseau: " + t.getMessage());
                    }
                });
    }

    private void addCinemaMarkerAndListItem(PlaceResult place, int index) {
        if (place.getGeometry() == null || place.getGeometry().getLocation() == null) return;

        double lat = place.getGeometry().getLocation().getLat();
        double lng = place.getGeometry().getLocation().getLng();
        LatLng position = new LatLng(lat, lng);

        String snippet = place.getVicinity() != null ? place.getVicinity() : "";
        if (place.getRating() > 0) {
            snippet += " | Note: " + place.getRating();
        }

        googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title(place.getName())
                .snippet(snippet));

        View itemView = getLayoutInflater().inflate(android.R.layout.simple_list_item_2, cinemasListLayout, false);
        TextView text1 = itemView.findViewById(android.R.id.text1);
        TextView text2 = itemView.findViewById(android.R.id.text2);

        text1.setText((index + 1) + ". " + place.getName());
        text1.setTextColor(Color.BLACK);
        text1.setTextSize(14);

        String detail = place.getVicinity() != null ? place.getVicinity() : "";
        if (place.getRating() > 0) {
            detail += " - ★ " + place.getRating();
        }
        text2.setText(detail);
        text2.setTextSize(12);

        cinemasListLayout.addView(itemView);
    }
}
