package com.example.geolocindoor.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arxit.geolocindoor.common.entities.Event;
import com.example.geolocindoor.R;
import com.example.geolocindoor.utils.DrawableUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private List<EventDisplay> events;
    private Consumer<Long> onButtonClick;
    private final int hoursOffset;

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    public EventsAdapter(@NonNull List<EventDisplay> events, @NonNull Consumer<Long> onButtonClick) {
        super();
        this.events = events;
        this.onButtonClick = onButtonClick;
        this.hoursOffset = new GregorianCalendar().getTimeZone().getRawOffset()/3600000;
    }

    public void setEvents(List<EventDisplay> events) {
        this.events = events;
    }

    public List<EventDisplay> getEvents() {
        return events;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final Context context;
        private final ImageView ivType;
        private final LinearLayout infoLayout;
        private final TextView tvTitle;
        private final TextView tvTime;
        private final TextView tvPlace;
        private final TextView tvDistance;
        private final TextView tvFloor;
        private final Button buttonGoTo;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            this.ivType = itemView.findViewById(R.id.ivType);
            this.infoLayout = itemView.findViewById(R.id.infoLayout);
            this.tvTitle = itemView.findViewById(R.id.tvTitle);
            this.tvTime = itemView.findViewById(R.id.tvTime);
            this.tvPlace = itemView.findViewById(R.id.tvPlace);
            this.tvDistance = itemView.findViewById(R.id.tvDistance);
            this.tvFloor = itemView.findViewById(R.id.tvFloor);
            this.buttonGoTo = itemView.findViewById(R.id.buttonGoTo);
        }

        private void update(EventDisplay eventDisplay) {
            Event event = eventDisplay.getEvent();
            this.ivType.setImageDrawable(DrawableUtils.getDrawableForPoiType(this.context, event.getLocation().getType()));
            this.infoLayout.setOnClickListener(view -> this.displayEventPopup(eventDisplay));
            this.tvTitle.setText(event.getTitle());
            this.tvTime.setText(String.format(this.context.getString(R.string.adapter_time_format), dtf.format(event.getStart().plusHours(hoursOffset)), eventDisplay.computeDuration(this.context)));
            this.tvPlace.setText(event.getLocation().getTitle());
            this.tvDistance.setText(String.format(this.context.getString(R.string.adapter_distance_format), eventDisplay.getDistance()));
            this.tvFloor.setText(event.getLocation().getFloor().getName());
            this.buttonGoTo.setOnClickListener(view -> onButtonClick.accept(event.getLocation().getId()));
        }

        private void displayEventPopup(EventDisplay eventDisplay){
            Event event = eventDisplay.getEvent();
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context)
                    .setTitle(event.getTitle())
                    .setMessage(this.createPopupMessage(eventDisplay))
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_add_event_to_calendar, ((dialog, which) -> {
                        Calendar beginTime = Calendar.getInstance();
                        Calendar endTime = Calendar.getInstance();
                        ZonedDateTime start = event.getStart().plusHours(hoursOffset);
                        ZonedDateTime end = event.getEnd().plusHours(hoursOffset);
                        beginTime.set(start.getYear(), start.getMonthValue()-1, start.getDayOfMonth(), start.getHour(), start.getMinute());
                        endTime.set(end.getYear(), end.getMonthValue()-1, end.getDayOfMonth(), end.getHour(), end.getMinute());
                        Intent intent = new Intent(Intent.ACTION_INSERT)
                                .setData(CalendarContract.Events.CONTENT_URI)
                                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                                .putExtra(CalendarContract.Events.TITLE, event.getTitle())
                                .putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription())
                                .putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation().getTitle())
                                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                        this.context.startActivity(intent);
                    }));
            builder.show();
        }

        private String createPopupMessage(EventDisplay eventDisplay){
            Event event = eventDisplay.getEvent();
            return "\n" + event.getDescription() + "\n\n" +
                    this.context.getString(R.string.dialog_event_location) + ": " + event.getLocation().getTitle() + "\n\n" +
                    String.format(this.context.getString(R.string.adapter_time_format), dtf.format(event.getStart().plusHours(hoursOffset)), eventDisplay.computeDuration(this.context));
        }
    }

    @NonNull
    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new EventsAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_search_results_item_event, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.update(this.events.get(i));
    }

    @Override
    public int getItemCount() {
        return this.events.size();
    }
}