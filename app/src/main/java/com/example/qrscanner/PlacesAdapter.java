package com.example.qrscanner;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private Context context;
    private List<Place> placesList;

    public PlacesAdapter(Context context, List<Place> placesList) {
        this.context = context;
        this.placesList = placesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.place_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place place = placesList.get(position);

        holder.name.setText(place.getNazwa());
        String imageUrl = place.getZdjecie();

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.manufaktura) // Tymczasowy obrazek
                .error(R.drawable.piotrkowska) // W razie błędu
                .into(holder.image);

        // Obsługa kliknięcia w kafelek
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlaceDetails.class);
            intent.putExtra("PLACE_ID", place.getId()); // Przekazanie ID miejsca
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.place_image);
            name = itemView.findViewById(R.id.place_name);
        }
    }
}