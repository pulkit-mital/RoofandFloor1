package com.example.task;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.io.InputStream;

/**
 * Created by User on 27-05-2015.
 */
public class ProjectDetails extends ActionBarActivity {


    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";

    TextView projectName,projectDescription,projectCity,projectAddress1,projectAddress2;
    ImageView img;
    String imageUrl;
    String id1;
    String description;
    String city;
    String address1;
    String address2;
    String name1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        name1 = getIntent().getStringExtra(KEY_NAME).toString();
        id1 = getIntent().getStringExtra(KEY_ID).toString();

        projectName = (TextView)findViewById(R.id.txt_name);
        projectDescription = (TextView)findViewById(R.id.txt_description);
        projectCity = (TextView)findViewById(R.id.txt_city);
        projectAddress1 = (TextView)findViewById(R.id.txt_address1);
        projectAddress2 = (TextView)findViewById(R.id.txt_address2);
        img = (ImageView)findViewById(R.id.imageView);


        new JsonAsyncTask()
                .execute("http://54.254.240.217:8080/app-task/projects/" + id1);




    }


    class JsonAsyncTask extends AsyncTask<String, Void, Void>

    {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog = new ProgressDialog(ProjectDetails.this);
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
                    JSONObject jsonObject = new JSONObject(data);
                    description = jsonObject.getString("description");
                    city = jsonObject.getString("city");
                    address1 = jsonObject.getString("addressLine1");
                    address2 = jsonObject.getString("addressLine2");

                    JSONArray jsonArray = jsonObject.getJSONArray("documents");

                    JSONObject jsonObj = jsonArray.getJSONObject(0);

                    imageUrl = jsonObj.getString("reference");
                    new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                            .execute(imageUrl);

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


            projectName.setText(name1);
            projectDescription.setText(description);
            projectAddress1.setText(address1);
            projectAddress2.setText(address2);
            projectCity.setText(city);

            pDialog.cancel();



        }
    }



    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
