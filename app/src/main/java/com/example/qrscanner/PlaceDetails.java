package com.example.qrscanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PlaceDetails extends AppCompatActivity {
    private FirebaseFirestore db;
    private ImageView placeImage;
    private TextView placeTitle, placeDescription;

    private boolean isFavorite, isVisited;
    private String placeId;
    private ImageView iconFavorite, iconVisited, iconWaypoint, iconRoute, iconQR;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        findViewById(R.id.back_container).setOnClickListener(v -> onBackPressed());

        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        placeImage = findViewById(R.id.place_image);
        placeTitle = findViewById(R.id.place_title);
        placeDescription = findViewById(R.id.place_description);
        iconFavorite = findViewById(R.id.icon1);
        iconVisited = findViewById(R.id.icon2);
        iconWaypoint = findViewById(R.id.icon3);
        iconRoute = findViewById(R.id.icon4);
        iconQR = findViewById(R.id.icon5);

        placeId = getIntent().getStringExtra("PLACE_ID");
        placeId = String.format("%03d", Integer.parseInt(placeId));

        if (placeId == null || placeId.trim().isEmpty()) {
            Toast.makeText(this, "Błąd: brak danych miejsca", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("PlaceDetails", "PLACE_ID: " + placeId);

        isFavorite = getPreference("favorite_" + placeId);
        isVisited = getPreference("visited_" + placeId);

        updateIcons();
        loadPlaceDetails(placeId);

        iconFavorite.setOnClickListener(v -> toggleFavorite());
        iconVisited.setOnClickListener(v -> toggleVisited());
        iconWaypoint.setOnClickListener(v -> showPlace(false)); // false = tylko pinezka
        iconRoute.setOnClickListener(v -> showPlace(true)); // true = wyznacz trasę
        iconQR.setOnClickListener(v -> showQR());
    }

    private void loadPlaceDetails(String placeId) {
        DocumentReference docRef = db.collection("places").document(placeId);
        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Pobranie danych z Firestore

                        String title = documentSnapshot.getString("nazwa");
                        String description = documentSnapshot.getString("opis");
                        String imageUrl = documentSnapshot.getString("zdjecie");

                        // Ustawienie wartości w UI
                        placeTitle.setText(title != null ? title : "Brak nazwy");
                        placeDescription.setText(description != null ? description : "Brak opisu");

                        // Wczytanie obrazu lub ustawienie domyślnego
                        Glide.with(this)
                                .load(imageUrl != null && !imageUrl.isEmpty() ? imageUrl : R.drawable.loading_error)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .placeholder(R.drawable.loading) // Tymczasowy obraz
                                .error(R.drawable.loading_error) // W razie błędu
                                .into(placeImage);
                    } else {
                        Toast.makeText(this, "Brak danych dla tego miejsca", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Błąd pobierania danych: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }


    private void toggleFavorite() {
        isFavorite = !isFavorite;
        setPreference("favorite_" + placeId, isFavorite);
        updateIcons();
        Toast.makeText(this, isFavorite ? "Dodano do ulubionych" : "Usunięto z ulubionych", Toast.LENGTH_SHORT).show();
    }

    private void toggleVisited() {
        isVisited = !isVisited;
        setPreference("visited_" + placeId, isVisited);
        updateIcons();
        Toast.makeText(this, isVisited ? "Oznaczono jako zwiedzone" : "Usunięto ze zwiedzonych", Toast.LENGTH_SHORT).show();
    }

    private void showPlace(boolean isRoute) {
        db.collection("places").document(placeId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Double place_lat = documentSnapshot.getDouble("wspolrzedned");
                        Double place_lng = documentSnapshot.getDouble("wspolrzedneg");

                        if (place_lat != null && place_lng != null) {
                            Intent intent = new Intent(this, MapsActivity.class);
                            intent.putExtra("place_lat", place_lat);
                            intent.putExtra("place_lng", place_lng);
                            intent.putExtra("is_route", isRoute); // Przekazujemy tryb działania
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Brak współrzędnych dla tego miejsca", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Błąd pobierania współrzędnych: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void showQR() {
        db.collection("places").document(placeId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String qrUrl = documentSnapshot.getString("kod");
                        if (qrUrl != null && !qrUrl.isEmpty()) {
                            QRDialogFragment qrDialog = new QRDialogFragment(qrUrl);
                            qrDialog.show(getSupportFragmentManager(), "QRDialog");
                        } else {
                            Toast.makeText(this, "Brak kodu QR dla tego miejsca", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Błąd pobierania kodu QR: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }


    private void updateIcons() {
        iconFavorite.setImageResource(isFavorite ? R.drawable.favoritesolid_icon : R.drawable.favorite_icon);
        iconVisited.setImageResource(isVisited ? R.drawable.checkpositivecolor_icon : R.drawable.checkpositive_icon);
    }

    private boolean getPreference(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    private void setPreference(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }
}
