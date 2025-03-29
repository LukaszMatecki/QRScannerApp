package com.example.qrscanner;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class QRDialogFragment extends DialogFragment {

    private String qrUrl;
    private float originalBrightness;
    private SharedPreferences sharedPreferences;

    public QRDialogFragment(String qrUrl) {
        this.qrUrl = qrUrl;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_qr_display);

        // Ustawienie przezroczystego tła
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            // Ustawienie rozmiaru dialogu
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(params);
        }

        ImageView qrImageView = dialog.findViewById(R.id.qr_image_view);
        ImageView closeButton = dialog.findViewById(R.id.close_button);

        if (qrImageView != null && qrUrl != null && !qrUrl.isEmpty()) {
            Glide.with(this)
                    .load(qrUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.loading_error)
                    .into(qrImageView);
        } else {
            Toast.makeText(getContext(), "Brak kodu QR", Toast.LENGTH_SHORT).show();
        }

        if (closeButton != null) {
            closeButton.setOnClickListener(v -> dismiss());
        }

        adjustScreenBrightness(true);

        return dialog;
    }

    @Override
    public void onDismiss(@NonNull android.content.DialogInterface dialog) {
        super.onDismiss(dialog);
        adjustScreenBrightness(false);
    }

    private void adjustScreenBrightness(boolean increase) {
        Context context = getContext();
        if (context == null || getActivity() == null) return;

        Window window = getActivity().getWindow();
        if (window == null) return;

        WindowManager.LayoutParams layoutParams = window.getAttributes();


        boolean isBrightnessEnabled = sharedPreferences.getBoolean("brightness_enabled", true);

        if (increase && isBrightnessEnabled) {

            if (layoutParams.screenBrightness == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) {
                originalBrightness = -1; // Wartość domyślna
            }
            else {
                originalBrightness = layoutParams.screenBrightness;
            }

            layoutParams.screenBrightness = 1.0f; // Maksymalna jasność
            Log.d("QRDialogFragment", "Zwiększono jasność ekranu.");
        }
        else if (!increase && isBrightnessEnabled) {
            Log.d("QRDialogFragment", "Przywrócono oryginalną jasność ekranu.");

            if (originalBrightness == -1) {
                layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE; // Domyślna jasność systemowa
            }
            else {
                layoutParams.screenBrightness = originalBrightness;
            }
        }

        window.setAttributes(layoutParams);
    }
}
