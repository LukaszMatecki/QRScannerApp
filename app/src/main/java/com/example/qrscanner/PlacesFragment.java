package com.example.qrscanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class PlacesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_places, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Znajdowanie kafelków
        ImageView piotrkowska = view.findViewById(R.id.piotrkowska_image);
        ImageView ogrod = view.findViewById(R.id.ogrod_b_image);
        ImageView dworzec = view.findViewById(R.id.dworzec_image);
        ImageView manufaktura = view.findViewById(R.id.manufaktura_image);
        ImageView pasaz = view.findViewById(R.id.pasaz_r_image);
        ImageView orientarium = view.findViewById(R.id.orientarium_image);
        ImageView palmiarnia = view.findViewById(R.id.palmiarnia_image);
        ImageView plac = view.findViewById(R.id.plac_wolnosci_image);

        // Ustawienie kliknięć
        piotrkowska.setOnClickListener(v -> openDescriptionFragment("1"));
        ogrod.setOnClickListener(v -> openDescriptionFragment("2"));
        dworzec.setOnClickListener(v -> openDescriptionFragment("3"));
        manufaktura.setOnClickListener(v -> openDescriptionFragment("4"));
        pasaz.setOnClickListener(v -> openDescriptionFragment("5"));
        orientarium.setOnClickListener(v -> openDescriptionFragment("6"));
        palmiarnia.setOnClickListener(v -> openDescriptionFragment("7"));
        plac.setOnClickListener(v -> openDescriptionFragment("8"));
    }

    private void openDescriptionFragment(String placeId) {
        Intent intent = new Intent(requireContext(), PlaceDetails.class);
        intent.putExtra("PLACE_ID", placeId); // Przekazujemy ID miejsca
        startActivity(intent);
    }

}
