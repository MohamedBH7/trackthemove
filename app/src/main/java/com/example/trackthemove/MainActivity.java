package com.example.trackthemove;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView longitudeTextView;
    private TextView latitudeTextView;
    private TextView speedTextView;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private List<Location> locationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        longitudeTextView = findViewById(R.id.longitudeTextView);
        latitudeTextView = findViewById(R.id.latitudeTextView);
        speedTextView = findViewById(R.id.speedTextView);

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            startTracking();
        }

        locationList = new ArrayList<>();
    }

    private void startTracking() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateUI(location);
                locationList.add(location);
                saveLocationData(locationList);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    private void updateUI(Location location) {
        longitudeTextView.setText("Longitude: " + location.getLongitude());
        latitudeTextView.setText("Latitude: " + location.getLatitude());
        speedTextView.setText("Speed: " + location.getSpeed() + " m/s");
    }

    private void saveLocationData(List<Location> locations) {
        JSONArray jsonArray = new JSONArray();
        for (Location location : locations) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("latitude", location.getLatitude());
                jsonObject.put("longitude", location.getLongitude());
                jsonObject.put("speed", location.getSpeed());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            File file = new File(getFilesDir(), "location_data.json");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonArray.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
