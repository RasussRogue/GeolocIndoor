package com.example.geolocindoor;

import android.app.SearchManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.geolocindoor.ui.CustomSearchView;
import com.google.android.material.navigation.NavigationView;
import com.mapbox.mapboxsdk.Mapbox;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Handler handler;
    private BluetoothAdapter btAdapter;
    private LocationManager locManager;
    private CustomSearchView cSearchView;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, this.getString(R.string.mapbox_token));

        this.handler = new Handler();
        this.btAdapter = BluetoothAdapter.getDefaultAdapter();
        this.locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!Objects.requireNonNull(this.locManager).isLocationEnabled()){
            this.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 666);
        }
        if (!this.btAdapter.isEnabled()){
            this.btAdapter.enable();
        }

        this.setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.app_bar_main);
        this.setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder
                (R.id.nav_map , R.id.nav_events, R.id.nav_settings, R.id.nav_credits)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // This method will trigger on item Click of navigation menu
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    if(menuItem.isChecked())
                        menuItem.setChecked(false);
                    else
                        menuItem.setChecked(true);
                    drawer.closeDrawers();
                    if (menuItem.getItemId() == R.id.nav_map) {
                        this.setSearchViewForMapFragment();
                    } else {
                        this.setSearchViewForOtherFragments();
                    }
                    navController.navigate(menuItem.getItemId());
                    return true;
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        this.cSearchView = (CustomSearchView) menu.findItem(R.id.search).getActionView();
        this.cSearchView.setSearchableInfo(Objects.requireNonNull(searchManager).getSearchableInfo(getComponentName()));

        this.initSearchView();
        this.setSearchViewForMapFragment();
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setSearchViewForMapFragment(){
        // Set search input field as displayed by default
        this.cSearchView.setIconifiedByDefault(false);
        this.cSearchView.setVisibility(View.VISIBLE);
    }

    private void setSearchViewForOtherFragments(){
        this.cSearchView.setIconifiedByDefault(true);
        this.cSearchView.setVisibility(View.INVISIBLE);
    }

    private void initSearchView() {

        // Set unlimited max width of the search view
        this.cSearchView.setMaxWidth(Integer.MAX_VALUE);
        this.cSearchView.setSubmitButtonEnabled(true);

        ImageView searchViewIcon = this.cSearchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        ViewGroup linearLayoutSearchView = (ViewGroup) searchViewIcon.getParent();
        linearLayoutSearchView.removeView(searchViewIcon);

        View searchViewIconPlate = this.cSearchView.findViewById(androidx.appcompat.R.id.search_plate);
        searchViewIconPlate.setBackgroundColor(Color.TRANSPARENT);

        View searchViewIconSubmit = this.cSearchView.findViewById(androidx.appcompat.R.id.submit_area);
        searchViewIconSubmit.setBackgroundColor(Color.TRANSPARENT);
    }

    private boolean clickedBack = false;

    private void setClickedBack(boolean cb){
        this.clickedBack = cb;
    }

    @Override
    public void onBackPressed() {
        if (this.clickedBack){
            this.finish();
            return;
        }
        this.clickedBack = true;
        Toast.makeText(this, R.string.close_app_warning, Toast.LENGTH_LONG).show();
        this.handler.postDelayed(() -> this.setClickedBack(false), 2000);
    }
}
