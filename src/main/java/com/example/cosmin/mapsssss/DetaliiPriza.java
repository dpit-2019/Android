package com.example.cosmin.mapsssss;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class DetaliiPriza extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalii_priza);

        System.out.println("APA RE");
        Intent intent = getIntent();
        String message = intent.getStringExtra(MapsActivity.EXTRA_MESSAGE);
        final int id = Integer.parseInt(message);
        System.out.println("MERge intent");

        Button button = (Button) findViewById(R.id.repbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                report(id);
            }
        });



        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url("http://10.0.2.2:8080/efind-0.0.1/getPriza?id=" + id).build();
        System.out.println("IDU" + id);

        System.out.println("aJUNS LA TRY");
        try {
            TextView nume = (TextView) findViewById(R.id.nume);
            TextView descriere = (TextView) findViewById(R.id.descriere);
            TextView tip = (TextView) findViewById(R.id.tip);
            TextView reports = (TextView) findViewById(R.id.reports);
            Response resp  = client.newCall(request).execute();
            System.out.println("DAAAAAAAAAAAAAAA");
            JSONObject priza = new JSONObject(resp.body().string());
            nume.setText(priza.getString("name"));
            descriere.setText(priza.getString("descriere"));
            tip.setText(priza.getString("tip"));
            reports.setText(priza.getString("reports"));
        } catch (IOException e) {
            System.out.println("DEEEEE AICI IN JOS");
            e.printStackTrace();
        } catch (
                JSONException e) {
            System.out.println("NUUUUUUMA");
            e.printStackTrace();
        }



        /*
        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                munceste();
            }
        });*/

    }


    public void report (int id) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url("http://10.0.2.2:8080/efind-0.0.1/report?id="+id).build();
        try {

            Response resp  = client.newCall(request).execute();
            System.out.println("am raportat");

        } catch (IOException e) {
            System.out.println("DEEEEE AICI IN JOS");
            e.printStackTrace();
        }

    }




}