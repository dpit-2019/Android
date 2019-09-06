package com.example.cosmin.mapsssss;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements   GoogleMap.OnMarkerClickListener,
                                                                OnMapReadyCallback,
                                                                GoogleMap.OnCameraIdleListener,
                                                                GoogleMap.OnCameraMoveListener{
    public static final String EXTRA_MESSAGE = "com.example.cosmin.mapsssss.MESSAGE";
    private GoogleMap mMap;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_REQUEST_CODE =101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fetchLastLocation();
    }
    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, LOCATION_REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = location;
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude()+""+currentLocation.getLongitude(),Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnCameraMoveListener(this);
        googleMap.setOnCameraIdleListener(this);
        LatLng current = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(current).title("HERE");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(current));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current,5));
        googleMap.addMarker(markerOptions);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();
                }
                break;
        }
    }

    @Override
    public void onCameraIdle() {

    }
    @Override
    public void onCameraMove()
    {
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url("http://10.0.2.2:8080/efind-0.0.1/getPointeri?lats="+bounds.northeast.latitude+"&lngs="+bounds.northeast.longitude+"&latj="+bounds.southwest.latitude+"&lngj="+bounds.southwest.longitude).build();

        Response resp = null;
        try {
            resp = client.newCall(request).execute();
            ////JSONObject priza = new JSONObject(resp.body().string());
            JSONArray jsonArr = new JSONArray(resp.body().string());
            for (int i = 0; i < jsonArr.length(); i++) {

                LatLng point = new LatLng(jsonArr.getJSONObject(i).getDouble("lat"), jsonArr.getJSONObject(i).getDouble("lng"));
                mMap.addMarker(new MarkerOptions().position(point).title("" + jsonArr.getJSONObject(i).getInt("id")));
            }
            System.out.println(jsonArr.length());
        } catch (
                IOException e) {
            System.out.println("DEEEEE AICI IN JOS");
            e.printStackTrace();
        } catch (
                JSONException e) {
            System.out.println("NUUUUUUMA");
            e.printStackTrace();
        }
    }
    @Override
    public boolean onMarkerClick(final Marker marker) {
        Intent intent = new Intent(this, DetaliiPriza.class);

        String message = marker.getTitle().toString();
        intent.putExtra(EXTRA_MESSAGE,message);
        startActivity(intent);
        return true;
    }

}
