package com.example.geolocindoor;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.geolocindoor.managers.BuildingSimple;
import com.example.geolocindoor.managers.RetrieveBuildingsListTask;
import com.example.geolocindoor.utils.PreferenceUtils;

import java.util.Objects;

import timber.log.Timber;

public class InitActivity extends AppCompatActivity {

    private BluetoothAdapter btAdapter;
    private LocationManager locManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.plant(new Timber.DebugTree());
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(new LinearLayout(this));
        this.btAdapter = BluetoothAdapter.getDefaultAdapter();
        this.locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.askPermissions();
    }

    private static final int PERMISSION_REQUEST_CODE = 666;
    private static final int LOCATION_REQUEST_CODE = 667;
    private static final int BLUETOOTH_REQUEST_CODE = 668;

    private void askPermissions(){
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_PRIVILEGED
        };
        this.requestPermissions(permissions, PERMISSION_REQUEST_CODE);
    }

    private boolean isBuildingSelected(){
        return PreferenceUtils.getSelectedBuildingId(this) != Long.parseLong(this.getString(R.string.prefs_defaultvalue_selected_building));
    }

    private void displaySelectBuildingDialog(){
        RetrieveBuildingsListTask task = new RetrieveBuildingsListTask(Objects.requireNonNull(this), buildings -> {
            String[] names = buildings.stream().map(BuildingSimple::getName).toArray(String[]::new);
            long[] ids = buildings.stream().mapToLong(BuildingSimple::getId).toArray();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(this.getString(R.string.prefs_dialog_title_selected_building));
            builder.setSingleChoiceItems(names, 0, (dialog, which) -> {
                PreferenceUtils.setSelectedBuildingId(this, ids[which]);
                this.processNext();
            });
            builder.setOnCancelListener(dialog -> processNext());
            builder.show();

        }, () -> {
            Toast.makeText(this, this.getString(R.string.nonetwork_toast_message), Toast.LENGTH_LONG).show();
            this.finish();
        });
        task.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                this.processNext();
            } else {
                Toast.makeText(this, this.getString(R.string.init_authorize_location), Toast.LENGTH_LONG).show();
                this.finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_REQUEST_CODE || requestCode == BLUETOOTH_REQUEST_CODE){
            this.processNext();
        }
    }

    private void activateLocation(){
        this.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_REQUEST_CODE);
    }

    private void activateBluetooth(){
        this.startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), BLUETOOTH_REQUEST_CODE);
    }

    private void processNext(){

        if (!this.isBuildingSelected()){
            //Toast.makeText(this, this.getString(R.string.init_choose_building), Toast.LENGTH_LONG).show();
            this.displaySelectBuildingDialog();
            return;
        }
        if (!Objects.requireNonNull(this.locManager).isLocationEnabled()){
            Toast.makeText(this, this.getString(R.string.init_activate_location), Toast.LENGTH_LONG).show();
            this.activateLocation();
            return;
        }
        if (!this.btAdapter.isEnabled()){
            Toast.makeText(this, this.getString(R.string.init_activate_bluetooth), Toast.LENGTH_LONG).show();
            this.activateBluetooth();
            //this.btAdapter.enable();
            return;
        }

        this.startActivity(new Intent(this, MainActivity.class)
                .setData(this.getIntent().getData()));
        this.finish();
    }

    private void onCancel(DialogInterface dialog) {
        this.processNext();
    }
}
