package com.example.qrscanner;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class SettingsFragment extends Fragment {

    private Button themeSwitch;
    private Button languageButton;
    private Button clearDataButton;
    private Button aboutButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        LinearLayout themeSwitch = view.findViewById(R.id.button_theme);
        LinearLayout languageButton = view.findViewById(R.id.button_language);
        LinearLayout clearDataButton = view.findViewById(R.id.button_clear_data);
        LinearLayout aboutButton = view.findViewById(R.id.button_about);

        themeSwitch.setOnClickListener(v ->
        {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new ThemeFragment())
                    .addToBackStack(null)
                    .commit();
        });

        languageButton.setOnClickListener(v ->
        {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new LanguageFragment())
                    .addToBackStack(null)
                    .commit();
        });

        //clearDataButton.setOnClickListener(v -> showClearDataDialog());

        aboutButton.setOnClickListener(v ->
        {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new AboutFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

//    private void showClearDataDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//        View dialogView = getLayoutInflater().inflate(R.layout.dialog_custom, null);
//        builder.setView(dialogView);
//
//        AlertDialog dialog = builder.create();
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialog.show();
//
//        TextView title = dialogView.findViewById(R.id.dialog_title);
//        TextView message = dialogView.findViewById(R.id.dialog_message);
//        TextView positiveButton = dialogView.findViewById(R.id.dialog_positive);
//        TextView negativeButton = dialogView.findViewById(R.id.dialog_negative);
//
//        title.setText("Czy chcesz usunąć dane?");
//        message.setText("Ta operacja jest nieodwracalna.");
//
//        positiveButton.setOnClickListener(v -> {
//            clearAppData();
//            dialog.dismiss();
//        });
//
//        negativeButton.setOnClickListener(v -> dialog.dismiss());
//    }
//
//    private void clearAppData() {
//        SharedPreferences preferences = requireContext().getSharedPreferences("app_prefs", 0);
//        preferences.edit().clear().apply();
//        Toast.makeText(requireContext(), "Dane zostały usunięte", Toast.LENGTH_SHORT).show();
//    }

}
