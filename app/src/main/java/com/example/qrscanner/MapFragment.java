package com.example.qrscanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class MapFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Zwracamy widok fragmentu, a nie wynik z inflater.inflate
        return inflater.inflate(R.layout.fragment_map, container, false);
    }
}
