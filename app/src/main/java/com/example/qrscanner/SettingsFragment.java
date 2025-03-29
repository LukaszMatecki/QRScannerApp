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
import androidx.appcompat.widget.SwitchCompat;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.Objects;

public class SettingsFragment extends Fragment {

    private Button themeSwitch;
    private Button languageButton;
    private Button clearDataButton;
    private Button aboutButton;
    private SwitchCompat notificationsSwitch;
    private SwitchCompat brightnessSwitch;

    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        LinearLayout languageButton = view.findViewById(R.id.button_language);
        notificationsSwitch = view.findViewById(R.id.switch_notifications);
        brightnessSwitch = view.findViewById(R.id.switch_brightness);
        LinearLayout themeSwitch = view.findViewById(R.id.button_theme);
        LinearLayout clearDataButton = view.findViewById(R.id.button_clear_data);
        LinearLayout aboutButton = view.findViewById(R.id.button_about);


        languageButton.setOnClickListener(v ->
        {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new LanguageFragment())
                    .addToBackStack(null)
                    .commit();
        });


        sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE);

        notificationsSwitch.setChecked(sharedPreferences.getBoolean("notifications_enabled", true));

        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply();
            Toast.makeText(requireContext(), isChecked ? "Powiadomienia włączone" : "Powiadomienia wyłączone", Toast.LENGTH_SHORT).show();
        });


        brightnessSwitch.setChecked(sharedPreferences.getBoolean("brightness_enabled", true));

        brightnessSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("brightness_enabled", isChecked).apply();
            Toast.makeText(requireContext(), isChecked ? "Automatyczne rozjaśnianie ekranu włączona" : "Automatyczne rozjaśnianie ekranu wyłączone", Toast.LENGTH_SHORT).show();
        });


        themeSwitch.setOnClickListener(v ->
        {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new ThemeFragment())
                    .addToBackStack(null)
                    .commit();
        });


        clearDataButton.setOnClickListener(v -> showClearDataDialog());


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

    private void showClearDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_custom, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        TextView positiveButton = dialogView.findViewById(R.id.dialog_positive);
        TextView negativeButton = dialogView.findViewById(R.id.dialog_negative);

        positiveButton.setOnClickListener(v -> {
            clearAppData();
            dialog.dismiss();
        });

        negativeButton.setOnClickListener(v -> dialog.dismiss());
    }

    private void clearAppData()
    {
        SharedPreferences preferences = requireContext().getSharedPreferences("AppPrefs", 0);
        preferences.edit().clear().apply();

        Glide.get(requireContext()).clearMemory();
        new Thread(() -> Glide.get(requireContext()).clearDiskCache()).start();

        Toast.makeText(requireContext(), "Dane zostały usunięte", Toast.LENGTH_SHORT).show();

        // Restart aktywności
        Intent intent = requireActivity().getIntent();
        requireActivity().finish();
        requireActivity().startActivity(intent);

    }

}
