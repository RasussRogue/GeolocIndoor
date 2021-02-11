package com.example.geolocindoor.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;

import com.arxit.geolocindoor.common.entities.Building;
import com.arxit.geolocindoor.common.entities.Event;
import com.arxit.geolocindoor.common.entities.VisiblePOI;
import com.example.geolocindoor.R;

import java.util.List;
import java.util.stream.Stream;


public class CustomSearchView extends SearchView {

    private SearchView.SearchAutoComplete autoComplete;

    public CustomSearchView(Context context) {
        super(context);
        this.initialize();
    }

    public CustomSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public CustomSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    private void initialize(){
        this.autoComplete = this.findViewById(androidx.appcompat.R.id.search_src_text);
        this.autoComplete.setThreshold(2);
        this.autoComplete.setMaxLines(10);
    }

    public void setData(Building building, List<Event> events){

        String[] data = Stream.concat(events.stream().map(Event::getTitle),
                building.getFloors().stream().flatMap(floor -> floor.getPois().stream()).filter(VisiblePOI.class::isInstance).map(VisiblePOI.class::cast).map(VisiblePOI::getTitle))
                .sorted().toArray(String[]::new);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), R.layout.autocomplete_item, R.id.tvAutocompleteSuggestion, data);
        this.autoComplete.setAdapter(adapter);

        //Click on suggestion
        this.autoComplete.setOnItemClickListener((adapterView, view, position, id) -> this.setQuery(adapterView.getItemAtPosition(position).toString(), true));

        //Set what happens when query changes or is submitted
        this.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //L'action de recherche est lancée ici, return true pour override le comportement par défaut de la Search
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    public void setAdapter(@NonNull ArrayAdapter<?> adapter){
        this.autoComplete.setAdapter(adapter);
    }

    @Override
    public void setSuggestionsAdapter(CursorAdapter adapter) {
        // don't let anyone touch this
    }

}
