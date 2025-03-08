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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private Context context;
    private List<Place> placesList;
    private boolean isGridView; // Przełączanie widoku kafelków

    public PlacesAdapter(Context context, List<Place> placesList, boolean isGridView) {
        this.context = context;
        this.placesList = placesList;
        this.isGridView = isGridView;
    }

    public void setGridView(boolean isGridView) {
        this.isGridView = isGridView;
        notifyDataSetChanged(); // Odśwież adapter po zmianie widoku
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isGridView ? R.layout.place_card_small : R.layout.place_card_big;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place place = placesList.get(position);

        holder.name.setText(place.getNazwa());
        String imageUrl = place.getZdjecie();

        // Resetowanie ImageView przed załadowaniem nowego obrazu
        holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.image.setImageDrawable(null);

        Glide.with(context)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(Target.SIZE_ORIGINAL)
                .placeholder(R.drawable.loading)
                .error(R.drawable.loading_error)
                .into(holder.image);

        // Obsługa kliknięcia w kafelek
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlaceDetails.class);
            intent.putExtra("PLACE_ID", place.getId()); // Przekazanie ID miejsca
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount()
    {
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