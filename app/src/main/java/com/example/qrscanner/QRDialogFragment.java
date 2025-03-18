package com.example.qrscanner;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
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

    public QRDialogFragment(String qrUrl) {
        this.qrUrl = qrUrl;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
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
        if (context == null) return;

        Window window = getActivity().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();

        if (increase) {
            originalBrightness = layoutParams.screenBrightness;
            layoutParams.screenBrightness = 1.0f; // Maksymalna jasność
        } else {
            layoutParams.screenBrightness = originalBrightness; // Przywrócenie oryginalnej jasności
        }

        window.setAttributes(layoutParams);
    }
}
