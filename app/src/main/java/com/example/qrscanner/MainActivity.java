package com.example.qrscanner;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Ustawienie domyślnego fragmentu
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());  // Ustaw domyślny fragment
        }

        // Obsługa kliknięć w menu bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.main) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.scan) {
                selectedFragment = new ScannerFragment();
            } else if (item.getItemId() == R.id.map) {
                selectedFragment = new MapFragment();
            }

            // Zamiana fragmentu
            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
            }

            return true;
        });
    }

    // Funkcja do zamiany fragmentów
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);  // Opcjonalnie dodajemy do back stack, by móc wrócić do poprzedniego fragmentu
        transaction.commit();
    }
}