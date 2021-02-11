package com.example.geolocindoor.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geolocindoor.R;
import com.example.geolocindoor.utils.DrawableUtils;

import java.util.List;
import java.util.function.Consumer;

public class VisiblePOIAdapter extends RecyclerView.Adapter<VisiblePOIAdapter.ViewHolder> {

    private List<VisiblePOIDisplay> places;
    private Consumer<Long> onButtonClick;

    public VisiblePOIAdapter(@NonNull List<VisiblePOIDisplay> places, @NonNull Consumer<Long> onButtonClick) {
        super();
        this.places = places;
        this.onButtonClick = onButtonClick;
    }

    public List<VisiblePOIDisplay> getPlaces() {
        return places;
    }

    public void setPlaces(List<VisiblePOIDisplay> places) {
        this.places = places;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final Context context;
        private final ImageView ivType;
        private final TextView tvTitle;
        private final TextView tvFloor;
        private final TextView tvDistance;
        private final Button buttonGoTo;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            this.ivType = itemView.findViewById(R.id.ivType);
            this.tvTitle = itemView.findViewById(R.id.tvTitle);
            this.tvFloor = itemView.findViewById(R.id.tvFloor);
            this.tvDistance = itemView.findViewById(R.id.tvDistance);
            this.buttonGoTo = itemView.findViewById(R.id.buttonGoTo);
        }

        private void update(VisiblePOIDisplay place) {
            this.ivType.setImageDrawable(DrawableUtils.getDrawableForPoiType(this.context, place.getPoi().getType()));
            this.tvTitle.setText(place.getPoi().getTitle());
            this.tvFloor.setText(place.getPoi().getFloor().getName());
            this.tvDistance.setText(String.format(this.context.getString(R.string.adapter_distance_format), place.getDistance()));
            this.buttonGoTo.setOnClickListener(view -> onButtonClick.accept(place.getPoi().getId()));
        }
    }

    @NonNull
    @Override
    public VisiblePOIAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VisiblePOIAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_search_results_item_place, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VisiblePOIAdapter.ViewHolder viewHolder, int i) {
        viewHolder.update(this.places.get(i));
    }

    @Override
    public int getItemCount() {
        return this.places.size();
    }
}
