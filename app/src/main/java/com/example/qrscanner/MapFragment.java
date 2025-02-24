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

public class MapFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), MapsActivity.class);
            startActivity(intent);
        }

        returnToHomeFragment();
        return null;
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