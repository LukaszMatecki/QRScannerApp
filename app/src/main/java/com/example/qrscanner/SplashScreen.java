package com.example.qrscanner;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SplashScreen extends AppCompatActivity {

    private FirebaseFirestore db;
    private SharedPreferences prefs;
    private LottieAnimationView lottieAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_QRScanner);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        db = FirebaseFirestore.getInstance();
        prefs = getSharedPreferences("AppPrefsHome", MODE_PRIVATE);
        lottieAnimation = findViewById(R.id.lottie_animation);

        fetchRandomPlaces();  // Pobieramy dane już na splashscreenie

        // Czekamy na zakończenie animacji Lottie
        lottieAnimation.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    private void fetchRandomPlaces() {
        CollectionReference placesRef = db.collection("places");

        placesRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful() || task.getResult() == null) return;

            List<Place> allPlaces = new ArrayList<>();
            for (QueryDocumentSnapshot doc : task.getResult()) {
                try {
                    Place place = doc.toObject(Place.class);
                    allPlaces.add(place);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Losujemy 4 miejsca
            List<Place> selectedPlaces = getDailyRandomPlaces(allPlaces);

            // Zapisujemy w SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("selectedPlaces", new Gson().toJson(selectedPlaces));
            editor.apply();
        });
    }

    private List<Place> getDailyRandomPlaces(List<Place> allPlaces) {
        if (allPlaces.size() <= 4) return allPlaces;

        // Sprawdzamy datę ostatniej aktualizacji
        String lastUpdateDate = prefs.getString("lastUpdateDate", "");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getDefault());
        String today = dateFormat.format(new Date());

        if (!lastUpdateDate.equals(today)) {
            Collections.shuffle(allPlaces);
            List<Place> newSelection = allPlaces.subList(0, 4);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("lastUpdateDate", today);
            editor.putString("selectedPlaces", new Gson().toJson(newSelection));
            editor.apply();

            return newSelection;
        }

        // Jeśli już wylosowaliśmy na dziś, zwracamy poprzednie dane
        String json = prefs.getString("selectedPlaces", "");
        if (!json.isEmpty()) {
            Type listType = new TypeToken<List<Place>>() {}.getType();
            return new Gson().fromJson(json, listType);
        }

        return allPlaces.subList(0, 4);
    }
}
