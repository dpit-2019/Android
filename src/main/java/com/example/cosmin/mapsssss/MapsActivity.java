package com.example.cosmin.mapsssss;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnCameraMoveListener(this);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        LatLng po = new LatLng(46.771286, 23.596441);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(po));
        googleMap.setOnCameraIdleListener(this);

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
            for (int i = 1; i < jsonArr.length(); i++) {

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
