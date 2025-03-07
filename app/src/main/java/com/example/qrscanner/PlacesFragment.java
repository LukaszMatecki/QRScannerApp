package com.example.qrscanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class PlacesFragment extends Fragment {

    private RecyclerView recyclerView;
    private PlacesAdapter adapter;
    private List<Place> placesList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference placesRef = db.collection("places");

    private boolean isGridView = false; // Domyślnie lista

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        adapter = new PlacesAdapter(getContext(), placesList, isGridView);
        recyclerView.setAdapter(adapter);

        setRecyclerViewLayout(); // Ustawienie domyślnego widoku

        ImageView showBig = view.findViewById(R.id.showBig);
        ImageView showSmall = view.findViewById(R.id.showSmall);

        showBig.setOnClickListener(v -> switchToListView());
        showSmall.setOnClickListener(v -> switchToGridView());

        fetchPlaces();

        return view;
    }

    private void fetchPlaces() {
        placesRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                if (error != null) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Błąd pobierania danych!", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                if (value == null) return;

                placesList.clear();
                for (QueryDocumentSnapshot doc : value) {
                    try {
                        Place place = doc.toObject(Place.class);
                        placesList.add(place);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void switchToListView() {
        isGridView = false;
        setRecyclerViewLayout();
    }

    private void switchToGridView() {
        isGridView = true;
        setRecyclerViewLayout();
    }

    private void setRecyclerViewLayout() {
        if (isGridView) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 kolumny dla widoku kafelkowego
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        adapter.setGridView(isGridView); // Zmieniamy układ w adapterze
    }
}
