package com.example.qrscanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

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

    private static final int SPLASH_TIMER = 2000;  // 2 sekundy
    private FirebaseFirestore db;
    private SharedPreferences prefs;
    private ProgressBar progressBar;
    private int progressStatus = 0; // Dodane zmienne
    private Handler handler = new Handler(); // Dodane zmienne

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        progressBar = findViewById(R.id.progressBar);
        db = FirebaseFirestore.getInstance();
        prefs = getSharedPreferences("AppPrefsHome", MODE_PRIVATE);

        startProgressAnimation();
        fetchRandomPlaces();  // Pobieramy dane już na splashscreenie

        new Handler().postDelayed(() ->
        {
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
            finish();
        }, SPLASH_TIMER);
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

            // Losujemy 2 miejsca
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

    private void startProgressAnimation() {
        new Thread(() -> {
            while (progressStatus < 100) {
                progressStatus += 2;  // Zwiększamy co 40 ms (100% w ~2 sekundy)
                handler.post(() -> progressBar.setProgress(progressStatus));
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
