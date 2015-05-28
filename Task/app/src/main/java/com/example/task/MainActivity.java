package com.example.task;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    ListView myList;

    ArrayList<String> listContent;
    ArrayAdapter<String> adapter;
    String []id1;
    String []name;
    String [] latitude;
    String [] longitude;
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    // Google Map
    private GoogleMap googleMap;
    int length;
    static final LatLng INDIA = new LatLng(13.0827, 80.2707);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

        myList = (ListView) findViewById(R.id.list);

        listContent = new ArrayList<String>();
        // listContent.add("Heloo");
        // listContent.add("Hii");
        adapter

                = new ArrayAdapter<String>(this,

                android.R.layout.simple_list_item_1,

                listContent);
        new JsonAsyncTask()
                .execute("http://54.254.240.217:8080/app-task/projects/");


        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(MainActivity.this,ProjectDetails.class);
                intent.putExtra(KEY_ID,id1[position]);
                intent.putExtra(KEY_NAME,name[position]);
                startActivity(intent);


            }
        });
    }

    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();


            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INDIA, 12));

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

    class JsonAsyncTask extends AsyncTask<String, Void, Void>

    {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub

            try {
                HttpGet httppost = new HttpGet(params[0]);
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(httppost);

                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);

                try {
                    JSONArray jsonArray = new JSONArray(data);
                        length = jsonArray.length();
                    id1 = new String[jsonArray.length()];
                    name = new String[jsonArray.length()];
                    latitude = new String[jsonArray.length()];
                    longitude = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        id1[i] = jsonObject.getString("id");
                        name[i] = jsonObject.getString("projectName");
                        longitude[i] = jsonObject.getString("lon");
                        latitude[i] = jsonObject.getString("lat");


                    listContent.add(jsonObject.getString("projectName"));


                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            addMarker();
            pDialog.cancel();

            myList.setAdapter(adapter);

        }
    }

    private void addMarker() {

        for(int i=0;i<length;i++) {
            MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.parseDouble(latitude[i]), Double.parseDouble(longitude[i]))).title("Hello Maps ");

// adding marker
            googleMap.addMarker(marker);

        }
        }
}

