package com.example.qrscanner;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
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
    private boolean isHomeFragment = false; // Czy Fragment jest Stroną Główną - problem wyświetlania
    private List<String> categoryList = new ArrayList<>();
    private ImageView filterIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        adapter = new PlacesAdapter(getContext(), placesList, isGridView, isHomeFragment);
        recyclerView.setAdapter(adapter);

        setRecyclerViewLayout(); // Ustawienie domyślnego widoku

        ImageView showBig = view.findViewById(R.id.showBig);
        ImageView showSmall = view.findViewById(R.id.showSmall);
        filterIcon = view.findViewById(R.id.filter);

        filterIcon.setOnClickListener(v -> showFilterPopup(v));
        showBig.setOnClickListener(v -> switchToListView());
        showSmall.setOnClickListener(v -> switchToGridView());

        fetchCategories();
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

    private void fetchCategories() {
        placesRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(getContext(), "Błąd pobierania kategorii!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (value == null) return;

            categoryList.clear();
            for (QueryDocumentSnapshot doc : value) {
                String category = doc.getString("kategoria");
                if (category != null && !categoryList.contains(category)) {
                    categoryList.add(category);
                }
            }
        });
    }
    private void showFilterPopup(View anchorView) {
        View popupView = getLayoutInflater().inflate(R.layout.custom_filter_popup, null);
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.setElevation(10f);
        popupWindow.setOutsideTouchable(true);

        // Pobieramy kontenery i opcje
        Typeface customFont = ResourcesCompat.getFont(getContext(), R.font.lato_regular);
        TextView filterAll = popupView.findViewById(R.id.filter_all);
        LinearLayout filterOptionsContainer = popupView.findViewById(R.id.filter_options_container);
        int color = ResourcesCompat.getColor(getResources(), R.color.dark_gray, null);

        // Obsługa "Wszystkie"
        filterAll.setOnClickListener(v -> {
            filterPlacesByCategory("Wszystkie");
            popupWindow.dismiss();
        });

        // Dodajemy kategorie dynamicznie
        filterOptionsContainer.removeAllViews();
        for (String category : categoryList) {
            TextView categoryView = new TextView(getContext());
            categoryView.setText(category);
            categoryView.setTextSize(18);
            categoryView.setTypeface(customFont);
            categoryView.setTextColor(getResources().getColor(android.R.color.black));
            categoryView.setPadding(20, 20, 20, 20);
            categoryView.setGravity(Gravity.CENTER);
            categoryView.setBackgroundResource(R.drawable.custom_selector);

            // Separator
            //View separator = new View(getContext());
            //separator.setLayoutParams(new LinearLayout.LayoutParams(
             //       ViewGroup.LayoutParams.MATCH_PARENT, 1));
            //separator.setBackgroundColor(color);

            // Obsługa kliknięcia
            categoryView.setOnClickListener(v -> {
                filterPlacesByCategory(category);
                popupWindow.dismiss();
            });

            filterOptionsContainer.addView(categoryView);
            //filterOptionsContainer.addView(separator);
        }

        // Ustawienie pozycji pod przyciskiem
        popupWindow.showAsDropDown(anchorView, -550, 30);
    }

    private void filterPlacesByCategory(String category) {
        if (category.equals("Wszystkie")) {
            fetchPlaces();
            return;
        }

        placesRef.whereEqualTo("kategoria", category).addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(getContext(), "Błąd filtrowania!", Toast.LENGTH_SHORT).show();
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
        if (isGridView)
        {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 kolumny dla widoku kafelkowego
            recyclerView.setAdapter(adapter);
        }
        else
        {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }
        adapter.setGridView(isGridView); // Zmieniamy układ w adapterze
    }
}
