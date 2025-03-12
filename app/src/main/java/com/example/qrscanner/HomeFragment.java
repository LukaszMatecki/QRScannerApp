package com.example.qrscanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment
{
    private RecyclerView recyclerView;
    private PlacesAdapter adapter;
    private List<Place> placesList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference placesRef = db.collection("places");
    private boolean doubleBackToExitPressedOnce = false;

    private boolean isGridView = false;
    private boolean isHomeFragment = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_home, container, false); // Layout strony głównej

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view_home);
        adapter = new PlacesAdapter(getContext(), placesList, isGridView, isHomeFragment);
        recyclerView.setAdapter(adapter);

        setRecyclerViewLayout();
        fetchPlaces();

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    requireActivity().finishAffinity(); // Zamknij aplikację do ekranu głównego
                } else {
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(requireContext(), "Stuknij dwukrotnie, aby zamknąć aplikację", Toast.LENGTH_SHORT).show();

                    new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000); // Reset po 2 sek.
                }
            }
        });
    }

    private void fetchPlaces() {
        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefsHome", Context.MODE_PRIVATE);
        String json = prefs.getString("selectedPlaces", "");

        if (!json.isEmpty()) {
            Type listType = new TypeToken<List<Place>>() {}.getType();
            List<Place> savedPlaces = new Gson().fromJson(json, listType);

            placesList.clear();
            placesList.addAll(savedPlaces);
            adapter.notifyDataSetChanged();
        }
    }

    private void setRecyclerViewLayout()
    {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
    }

}