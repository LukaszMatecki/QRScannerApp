package com.example.qrscanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
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
import androidx.core.content.ContextCompat;
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

            if (!categoryList.contains("Ulubione")){categoryList.add("Ulubione");}
            if (!categoryList.contains("Zwiedzone")){categoryList.add("Zwiedzone");}

            for (QueryDocumentSnapshot doc : value) {
                String category = doc.getString("kategoria");
                if (category != null && !categoryList.contains(category)) {
                    categoryList.add(category);
                }
            }


        });
    }
    private void showFilterPopup(View anchorView) {
        Context context = getContext();
        if (context == null) return;

        // Tworzenie głównego layoutu popupu
        LinearLayout popupLayout = new LinearLayout(context);
        popupLayout.setOrientation(LinearLayout.VERTICAL);
        popupLayout.setPadding(20, 20, 20, 20);

        // Ustawienie zaokrąglonych rogów i ramki
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE);
        background.setCornerRadius(30f); // Zaokrąglone rogi
        popupLayout.setBackground(background);

        Typeface customFont = ResourcesCompat.getFont(context, R.font.lato_regular);

        // 🔹 Zmniejszenie szerokości popupu do 250dp
        int popupWidth = (int) (200 * context.getResources().getDisplayMetrics().density);

        PopupWindow popupWindow = new PopupWindow(
                popupLayout,
                popupWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.setElevation(10f);
        popupWindow.setOutsideTouchable(true);

        // 🔹 Opcja "Wszystkie"
        TextView filterAll = new TextView(context);
        filterAll.setText("Wszystkie");
        filterAll.setTextSize(18);
        filterAll.setTypeface(customFont);
        filterAll.setTextColor(Color.BLACK);
        filterAll.setPadding(20, 20, 20, 20);
        filterAll.setGravity(Gravity.CENTER);
        filterAll.setBackgroundResource(R.drawable.custom_selector);
        filterAll.setOnClickListener(v -> {
            filterPlacesByCategory("Wszystkie");
            popupWindow.dismiss();
        });

        popupLayout.addView(filterAll);

        //separator po "Wszystkie"
        View separatorAll = new View(context);
        separatorAll.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 2));
        separatorAll.setBackgroundColor(ContextCompat.getColor(context, R.color.filter_options_gray));
        popupLayout.addView(separatorAll);

        //generowanie kategorii dynamicznie
        for (int i = 0; i < categoryList.size(); i++) {
            String category = categoryList.get(i);

            TextView categoryView = new TextView(context);
            categoryView.setText(category);
            categoryView.setTextSize(18);
            categoryView.setTypeface(customFont);
            categoryView.setTextColor(Color.BLACK);
            categoryView.setPadding(20, 20, 20, 20);
            categoryView.setGravity(Gravity.CENTER);
            categoryView.setBackgroundResource(R.drawable.custom_selector);
            categoryView.setOnClickListener(v -> {
                filterPlacesByCategory(category);
                popupWindow.dismiss();
            });

            popupLayout.addView(categoryView);

            // 🔹 Separator po każdej kategorii oprócz ostatniej
            if (i < categoryList.size() - 1) {
                View separator = new View(context);
                separator.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 2));
                separator.setBackgroundColor(ContextCompat.getColor(context, R.color.filter_options_gray));
                popupLayout.addView(separator);
            }
        }

        // Ustawienie pozycji popupu
        popupWindow.showAsDropDown(anchorView, -500, 30);
    }



    private void filterPlacesByCategory(String category) {
        if (category.equals("Wszystkie")) {
            fetchPlaces();
            return;
        }

        if (category.equals("Ulubione")) {
            filterFavoriteOrVisited(category);
            return;
        }

        if (category.equals("Zwiedzone")) {
            filterFavoriteOrVisited(category);
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

    private void filterFavoriteOrVisited(String category) {
        if (getContext() == null) return; // Unikamy błędów kontekstowych

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean filterFavorites = category.equals("Ulubione");

        placesRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            placesList.clear(); // Czyścimy listę dopiero po udanym pobraniu
            boolean found = false;

            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Place place = doc.toObject(Place.class);
                String placeId = doc.getId();

                boolean isFavorite = sharedPreferences.getBoolean("favorite_" + placeId, false);
                boolean isVisited = sharedPreferences.getBoolean("visited_" + placeId, false);

                if ((filterFavorites && isFavorite) || (!filterFavorites && isVisited)) {
                    placesList.add(place);
                    found = true;
                }
            }

            if (!found) {
                Toast.makeText(getContext(), "Brak miejsc w tej kategorii!", Toast.LENGTH_SHORT).show();
            }

            adapter.notifyDataSetChanged(); // Odświeżenie listy po przefiltrowaniu
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Błąd filtrowania: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
