package com.example.geolocindoor.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geolocindoor.R;
import com.example.geolocindoor.itinerary.ItineraryManager;
import com.example.geolocindoor.location.LocationManager;
import com.example.geolocindoor.managers.BuildingSimple;
import com.example.geolocindoor.managers.RetrieveBuildingsListTask;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {

    private EditTextPreference apiHostPref;
    private ListPreference dayCountPref;
    private ListPreference selectedBuildingPref;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        this.setPreferencesFromResource(R.xml.settings_prefs, rootKey);

        PreferenceManager prefManager = this.getPreferenceManager();

        this.apiHostPref = prefManager.findPreference(this.getString(R.string.prefs_key_api_host));
        assert this.apiHostPref != null;
        this.apiHostPref.setOnPreferenceChangeListener((preference, newValue) -> {
            this.apiHostPref.setSummary(newValue.toString());
            return true;
        });

        SwitchPreference positionningMode = prefManager.findPreference(this.getString(R.string.prefs_key_trilat));
        assert positionningMode != null;
        positionningMode.setOnPreferenceChangeListener((preference, newValue) -> {
            LocationManager locationManager = LocationManager.create(Objects.requireNonNull(this.getContext()));
            locationManager.setPositioningMode((boolean)newValue);
            return true;
        });

        this.dayCountPref = prefManager.findPreference(this.getString(R.string.prefs_key_daycount));
        assert this.dayCountPref != null;
        this.dayCountPref.setOnPreferenceChangeListener((preference, newValue) -> {
            this.dayCountPref.setSummary(this.dayCountPref.getEntries()[this.dayCountPref.findIndexOfValue(newValue.toString())]);
            return true;
        });

        this.selectedBuildingPref = prefManager.findPreference(this.getString(R.string.prefs_key_selected_building));
        assert this.selectedBuildingPref != null;
        this.selectedBuildingPref.setOnPreferenceChangeListener((preference, newValue) -> {
            this.selectedBuildingPref.setSummary(this.selectedBuildingPref.getEntries()[this.selectedBuildingPref.findIndexOfValue(newValue.toString())]);
            return true;
        });

        SwitchPreference accessibilityPref = prefManager.findPreference(this.getString(R.string.prefs_key_accessibility));
        assert accessibilityPref != null;
        accessibilityPref.setOnPreferenceChangeListener(((preference, newValue) -> {
            ItineraryManager.getInstance().setAccessibility((boolean) newValue);
            return true;
        }));

        RetrieveBuildingsListTask task = new RetrieveBuildingsListTask(Objects.requireNonNull(this.getContext()), buildings -> {
            String[] entries = buildings.stream().map(BuildingSimple::getName).toArray(String[]::new);
            String[] entryValues = buildings.stream().map(b -> String.valueOf(b.getId())).toArray(String[]::new);
            this.selectedBuildingPref.setEntries(entries);
            this.selectedBuildingPref.setEntryValues(entryValues);
            this.selectedBuildingPref.setSummary(this.selectedBuildingPref.getEntry());
        }, () -> Toast.makeText(this.getContext(), this.getString(R.string.nonetwork_toast_message), Toast.LENGTH_LONG).show());
        task.execute();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // This holds the PreferenceScreen's items
        RecyclerView rv = this.getListView();
        float marginTop = this.getResources().getDimension(R.dimen.toolbar_total_height);
        rv.setPadding(0, (int) marginTop, 0, 0);

        this.apiHostPref.setSummary(this.apiHostPref.getText());
        this.dayCountPref.setSummary(this.dayCountPref.getEntry());
    }

}