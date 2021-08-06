package com.example.trafficincidentsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    ListView lv;
    ArrayList<Incident> al;
    ArrayAdapter aa;

    private FirebaseFirestore db;
    private CollectionReference colRef;
    private DocumentReference docRef;

    private AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client.addHeader("AccountKey", "tJaGDhToQ/WXHZF8imFCbQ==");

        db = FirebaseFirestore.getInstance();

        colRef = db.collection("incidents");

        lv = findViewById(R.id.lv);
        al = new ArrayList<Incident>();
        aa = new IncidentAdapter(MainActivity.this, R.layout.row, al);

        lv.setAdapter(aa);

        Toast.makeText(MainActivity.this, "" + Calendar.getInstance().get(Calendar.YEAR), Toast.LENGTH_SHORT).show();

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
//                            String day  = (String) DateFormat.format("dd",   date);
                        } catch (ParseException e){
                            e.printStackTrace();
                        }

                        Incident incident = new Incident(type, latitude, longitude, message, date);

                        al.add(incident);
                    }

                    aa.notifyDataSetChanged();

                }
                catch (JSONException e){
                    e.printStackTrace();
                }

            }//end onSuccess

        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Incident selected = al.get(position);

                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                i.putExtra("type", selected.getType());
                i.putExtra("latitude", selected.getLatitude());
                i.putExtra("longitude", selected.getLongitude());
                startActivity(i);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.viewAll){

            Intent i = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(i);

            return true;

        } else if (id == R.id.upload) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Upload to Firestore")
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .setMessage("Proceed to upload to Firestore?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {

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
                                            String [] timeString = dateString[1].split(Pattern.quote(" "));
                                            try{
                                                SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY HH:mm");
                                                date = (format.parse(dateString[0] + " " + timeString[0]));
                                                Log.i("date1223243342","" + date);

                                            } catch (ParseException e){
                                                e.printStackTrace();
                                            }

                                            Incident incident = new Incident(type, latitude, longitude, message, date);

                                            al.add(incident);
                                        }

                                    }
                                    catch (JSONException e){
                                        e.printStackTrace();
                                    }

                                }//end onSuccess

                            });

//                            colRef
//                                    .add(student)
//                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                        @Override
//                                        public void onSuccess(DocumentReference documentReference) {
//                                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
//
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Log.w(TAG, "Error adding document", e);
//                                        }
//                                    });
//
//
                        }

                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            // Create the AlertDialog object and return it
            builder.create();
            builder.show();

            return true;
        } else if (id == R.id.refresh){
            al.clear();
            aa.notifyDataSetChanged();

            client.post("http://datamall2.mytransport.sg/ltaodataservice/TrafficIncidents", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {

                    try {
                        Log.i("Response: ", response.toString());

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
//                            String day  = (String) DateFormat.format("dd",   date);
                            } catch (ParseException e){
                                e.printStackTrace();
                            }

                            Incident incident = new Incident(type, latitude, longitude, message, date);

                            al.add(incident);
                        }

                        aa.notifyDataSetChanged();

                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }

                }//end onSuccess

            });

            Toast.makeText(MainActivity.this, "List Refreshed", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.graph) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}