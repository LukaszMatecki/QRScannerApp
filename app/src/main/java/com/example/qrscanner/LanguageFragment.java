package com.example.qrscanner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;

public class LanguageFragment extends Fragment {

    private RadioButton radioButtonPl;
    private RadioButton radioButtonEng;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_language, container, false);

        // Inicjalizacja komponentów
        radioButtonPl = view.findViewById(R.id.language_pl_radiobutton);
        radioButtonEng = view.findViewById(R.id.language_eng_radiobutton);

        // Inicjalizacja SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE);

        // Odczytanie zapisanej wartości (domyślnie Polski)
        String selectedLanguage = sharedPreferences.getString("language", "pl");
        if (selectedLanguage.equals("pl"))
        {
            radioButtonPl.setChecked(true);
            radioButtonEng.setChecked(false);
        }
        else
        {
            radioButtonEng.setChecked(true);
            radioButtonPl.setChecked(false);
        }

        // Obsługa zmian wyboru języka
        radioButtonPl.setOnClickListener(v -> {
            radioButtonEng.setChecked(false);
            saveLanguage("pl");
        });

        radioButtonEng.setOnClickListener(v -> {
            radioButtonPl.setChecked(false);
            saveLanguage("eng");
        });

        return view;
    }

    private void saveLanguage(String language) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("language", language);
        editor.apply();
    }
}
