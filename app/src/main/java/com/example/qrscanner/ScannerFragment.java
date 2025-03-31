package com.example.qrscanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
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
        options.setOrientationLocked(true);

        // Konfiguracja skanera
        intentIntegrator.setPrompt("Zeskanuj kod QR");
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        intentIntegrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            String scannedData = intentResult.getContents();
            if (scannedData != null) {
                try {
                    int placeId = Integer.parseInt(scannedData); // Pobranie ID miejsca z QR
                    String formattedPlaceId = String.format("%03d", placeId); // Formatowanie zgodnie z PlaceDetails

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("places").document(formattedPlaceId).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    // Miejsce istnieje, otwieramy szczegóły
                                    Intent intent = new Intent(requireContext(), PlaceDetails.class);
                                    intent.putExtra("PLACE_ID", formattedPlaceId);
                                    startActivity(intent);
                                } else {
                                    // Miejsce nie istnieje, wyświetlamy Toast
                                    Toast.makeText(requireContext(), "Nie znaleziono żadnego pasującego miejsca", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(requireContext(), "Błąd połączenia z bazą danych", Toast.LENGTH_SHORT).show()
                            );

                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Niepoprawny kod QR", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Brak danych o kodzie QR", Toast.LENGTH_SHORT).show();
            }
        }
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