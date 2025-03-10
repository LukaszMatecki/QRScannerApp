package com.example.qrscanner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;

public class ThemeFragment extends Fragment {

    private RadioButton radioButtonLIGHT;
    private RadioButton radioButtonDARK;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theme, container, false);

        // Inicjalizacja komponentów
        radioButtonLIGHT = view.findViewById(R.id.light_theme_radiobutton);
        radioButtonDARK = view.findViewById(R.id.dark_theme_radiobutton);

        // Inicjalizacja SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE);

        // Odczytanie zapisanej wartości (domyślnie Polski)
        String selectedTheme = sharedPreferences.getString("theme", "light");
        if (selectedTheme.equals("light"))
        {
            radioButtonLIGHT.setChecked(true);
            radioButtonDARK.setChecked(false);
        }
        else
        {
            radioButtonDARK.setChecked(true);
            radioButtonLIGHT.setChecked(false);
        }

        // Obsługa zmian wyboru języka
        radioButtonLIGHT.setOnClickListener(v -> {
            radioButtonDARK.setChecked(false);
            saveTheme("light");
        });

        radioButtonDARK.setOnClickListener(v -> {
            radioButtonLIGHT.setChecked(false);
            saveTheme("dark");
        });

        return view;
    }

    private void saveTheme(String theme) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("theme", theme);
        editor.apply();
    }
}
