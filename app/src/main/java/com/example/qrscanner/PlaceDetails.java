package com.example.qrscanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PlaceDetails extends AppCompatActivity {
    private FirebaseFirestore db;
    private ImageView placeImage;
    private TextView placeTitle, placeDescription, placeLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        // Inicjalizacja Firebase
        db = FirebaseFirestore.getInstance();

        // Pobranie referencji do widoków
        placeImage = findViewById(R.id.place_image);
        placeTitle = findViewById(R.id.place_title);
        placeDescription = findViewById(R.id.place_description);
        //placeLocation = findViewById(R.id.place_location);

        // Pobranie ID miejsca przekazanego z intentu
        String placeId = getIntent().getStringExtra("PLACE_ID");
        if (placeId != null) {
            loadPlaceDetails(placeId);
        } else {
            Toast.makeText(this, "Błąd: brak danych miejsca", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPlaceDetails(String placeId) {
        DocumentReference docRef = db.collection("places").document(placeId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String title = documentSnapshot.getString("title");
                String description = documentSnapshot.getString("description");
                String imageUrl = documentSnapshot.getString("imageUrl");
                //String location = documentSnapshot.getString("location");

                // Ustawienie danych w widokach
                placeTitle.setText(title);
                placeDescription.setText(description);
                //placeLocation.setText(location);

                // Wczytanie obrazu z Firestore
                Glide.with(this).load(imageUrl).into(placeImage);
            } else {
                Toast.makeText(this, "Brak danych dla tego miejsca", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Błąd pobierania danych", Toast.LENGTH_SHORT).show());
    }
}