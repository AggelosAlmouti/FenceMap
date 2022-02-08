package com.example.fencemap;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.example.fencemap.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int MY_LOCATION_PERMISSION_CODE = 555; //todo uuid
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private static final String AUTHORITY = "com.example.geomonitor";
    public static final String CONTENT_URI = "content://" + AUTHORITY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == MY_LOCATION_PERMISSION_CODE) { //use switch for multiple requests
                enableMyLocation();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();

        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = resolver.query(Uri.parse(CONTENT_URI + "/"), null, null, null, null);
        if (cursor == null) {
            Log.i("cursor", "Database is empty!");
        } else {
            if (cursor.moveToFirst()) {
                do {
                    LatLng coords = new LatLng(cursor.getDouble(0), cursor.getDouble(1));
                    googleMap.addMarker(new MarkerOptions()
                            .position(coords)
                            .title(cursor.getString(2)+ " " +cursor.getDouble(0)+ ", " +cursor.getDouble(1)));
                } while (cursor.moveToNext());
            }
        }
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_PERMISSION_CODE);
            return;
        }
        mMap.setMyLocationEnabled(true);
    }
}