package com.example.trafficincidentsapp;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private double latitude, longitude;
    private String type;

    private ArrayList<Incident> al = new ArrayList<Incident>();

    private AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.clear();

        Intent i = getIntent();

        if (i.getStringExtra("type") != null) {
            latitude = i.getDoubleExtra("latitude", 0.0);
            longitude = i.getDoubleExtra("longitude",0.0);
            type = i.getStringExtra("type");

            LatLng incident = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(incident).title(type));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(incident , 12));
        } else {

            al.clear();

            client.addHeader("AccountKey", "tJaGDhToQ/WXHZF8imFCbQ==");
            client.post("http://datamall2.mytransport.sg/ltaodataservice/TrafficIncidents", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {

                    try {
                        Log.i("Response: ", response.toString());

                        al.clear();

                        JSONArray value = response.getJSONArray("value");

                        for (int i = 0; i < value.length(); i++) {

                            JSONObject jsonObj = value.getJSONObject(i);

                            String type = jsonObj.getString("Type");
                            double latitude = jsonObj.getDouble("Latitude");
                            double longitude = jsonObj.getDouble("Longitude");
                            String message = jsonObj.getString("Message");

                            Date date = null;
                            String dateMessage = message.substring(1);
                            String [] dateString = dateMessage.split(Pattern.quote(")"));
                            try{
                                SimpleDateFormat format = new SimpleDateFormat("dd/MM");
                                date = (format.parse(dateString[0]));
                            } catch (ParseException e){
                                e.printStackTrace();
                            }

                            Incident incident = new Incident(type, latitude, longitude, message, date);

                            al.add(incident);
                        }

                        for (int o = 0; o < al.size(); o ++) {
                            LatLng incident = new LatLng(al.get(o).getLatitude(), al.get(o).getLongitude());
                            mMap.addMarker(new MarkerOptions().position(incident).title(al.get(o).getType()));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(1.3593063198643724, 103.86690935033708), 12));
                        }

                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }

                }//end onSuccess

            });

        }
    }
}