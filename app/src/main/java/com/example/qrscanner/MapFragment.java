package com.example.qrscanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class MapFragment extends Fragment {

    Button showmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Zwracamy widok fragmentu
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Inicjalizujemy przycisk
        showmap = view.findViewById(R.id.showmap);

        // Ustawiamy listener na kliknięcie
        showmap.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapsActivity.class);
            startActivity(intent);
        });

        return view;
    }
}