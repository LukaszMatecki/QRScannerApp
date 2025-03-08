package com.example.qrscanner;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        View backContainer = findViewById(R.id.back_container);
        backContainer.setOnClickListener(v -> onBackPressed());

        // Inicjalizacja Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Pobranie referencji do widoków
        placeImage = findViewById(R.id.place_image);
        placeTitle = findViewById(R.id.place_title);
        placeDescription = findViewById(R.id.place_description);

        // Pobranie ID miejsca przekazanego z intentu
        String placeId = getIntent().getStringExtra("PLACE_ID");

        if (placeId == null || placeId.trim().isEmpty()) {
            Toast.makeText(this, "Błąd: brak danych miejsca", Toast.LENGTH_SHORT).show();
            finish(); // Zamknięcie aktywności, jeśli brak ID miejsca
            return;
        }

        // Załadowanie szczegółów miejsca
        loadPlaceDetails(placeId);
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
                                .load(imageUrl != null && !imageUrl.isEmpty() ? imageUrl : R.drawable.plac_wolnosci)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
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
}