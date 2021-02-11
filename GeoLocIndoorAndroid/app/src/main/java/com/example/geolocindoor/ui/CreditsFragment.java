package com.example.geolocindoor.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geolocindoor.BuildConfig;
import com.example.geolocindoor.R;

import java.util.Objects;

public class CreditsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        this.setPreferencesFromResource(R.xml.credits_prefs, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // This holds the PreferenceScreen's items
        RecyclerView rv = this.getListView();
        float marginTop = this.getResources().getDimension(R.dimen.toolbar_total_height);
        rv.setPadding(0, (int) marginTop, 0, 0);

        PreferenceManager prefManager = this.getPreferenceManager();
        Preference versionPref = prefManager.findPreference("credits_key_version");
        Objects.requireNonNull(versionPref).setSummary(BuildConfig.VERSION_NAME);
    }
}
