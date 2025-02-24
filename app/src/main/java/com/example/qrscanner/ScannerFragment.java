package com.example.qrscanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

public class ScannerFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getActivity() != null) {
            initiateQRScanner();
        }

        returnToHomeFragment();
        return null;
    }
    private void initiateQRScanner() {
        IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(ScannerFragment.this);

        // Wymuszamy orientację pionową
        ScanOptions options = new ScanOptions();
        options.setOrientationLocked(false);

        // Konfiguracja skanera
        intentIntegrator.setPrompt("Zeskanuj kod QR");
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        intentIntegrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Obsługa wyniku skanowania
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() != null) {
                // Wynik skanowania (np. wyświetlenie w logach)
                System.out.println("QR Code Result: " + intentResult.getContents());
            }
        }

        returnToHomeFragment();
    }

    private void returnToHomeFragment() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new HomeFragment()) // Wstawienie HomeFragment
                .addToBackStack(null)
                .commit();

                // Zmiana "wciśnięcia po kliknięciu powrotu"
                BottomNavigationView bottomNavigation = requireActivity().findViewById(R.id.bottomNavigationView);
                bottomNavigation.setSelectedItemId(R.id.main);

                NavigationView navigationView = requireActivity().findViewById(R.id.nav_view);
                Menu menu = navigationView.getMenu();
                MenuItem item = menu.findItem(R.id.boczna_strona_gl);
    }
}