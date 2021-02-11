package com.example.geolocindoor.ui;

import android.content.Context;
import android.icu.util.GregorianCalendar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arxit.geolocindoor.common.entities.Event;
import com.example.geolocindoor.R;
import com.example.geolocindoor.utils.DrawableUtils;
import com.example.geolocindoor.utils.DurationCalculator;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class BottomSheetEventsAdapter extends RecyclerView.Adapter<BottomSheetEventsAdapter.ViewHolder> {

    private List<Event> events;
    private final int hoursOffset;

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    BottomSheetEventsAdapter(@NonNull List<Event> events) {
        super();
        this.events = events;
        this.hoursOffset = new GregorianCalendar().getTimeZone().getRawOffset()/3600000;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final Context context;
        private final ImageView ivType;
        private final TextView tvTitle;
        private final TextView tvTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            this.ivType = itemView.findViewById(R.id.ivType);
            this.tvTitle = itemView.findViewById(R.id.tvTitle);
            this.tvTime = itemView.findViewById(R.id.tvTime);
        }

        private void update(Event event) {
            this.ivType.setImageDrawable(DrawableUtils.getDrawableForPoiType(this.context, event.getLocation().getType()));
            this.tvTitle.setText(event.getTitle());
            this.tvTime.setText(String.format(this.context.getString(R.string.adapter_time_format),
                            dtf.format(event.getStart().plusHours(hoursOffset)), DurationCalculator.computeDuration(context, event.getStart(), event.getEnd())));
        }
    }

    @NonNull
    @Override
    public BottomSheetEventsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BottomSheetEventsAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bottom_sheet_item_event, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetEventsAdapter.ViewHolder viewHolder, int i) {
        viewHolder.update(this.events.get(i));
    }

    @Override
    public int getItemCount() {
        return this.events.size();
    }

}
