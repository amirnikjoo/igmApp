package com.igm.product;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.igm.product.dbhelper.DatabaseHelper;
import com.igm.product.entity.Part;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * User: Amir Nikjoo,  02/27/2016,  10:14 AM
 */
public class SplashScreen extends Activity {
    private static String url = "http://iraniangm.ir/app/products.json";
    private static long SPLASH_TIME_OUT = 2000;

    // JSON Node names
    private static final String TAG_PRODUCTS = "products";

    private static final String TAG_ID = "id";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_TYPE = "type";
    private static final String TAG_STATUS = "status";

    DatabaseHelper db = new DatabaseHelper(this);

//    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_layout);

        if (isNetworkAvailable())
            new RefreshProducts().execute();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }

    private class RefreshProducts extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
            //httpPost
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/json");

            InputStream inputStream = null;
            String jsonStr = null;
            try {
                long t1 = System.currentTimeMillis();
                HttpResponse response = httpClient.execute(httpPost);
                Log.d("response time : ", System.currentTimeMillis() - t1 + "");

                HttpEntity entity = response.getEntity();

                inputStream = entity.getContent();
                // json is UTF-8 by default
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                jsonStr = sb.toString();
            } catch (Exception e) {
                // Oops
            } finally {
                try {
                    if (inputStream != null) inputStream.close();
                } catch (Exception squish) {
                }
            }

//            ServiceHandler sh = new ServiceHandler();
//            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

/*
            String jsonStr = "{" +
                    "    \"products\": [" +
                    "        {" +
                    "                \"id\": \"321\"," +
                    "                \"description\": \"شادی\"," +
                    "                \"type\": \"3\"," +
                    "                \"status\": \"1\"" +
                    "        }," +
                    "        {" +
                    "                \"id\": \"322\"," +
                    "                \"description\": \"سارینا\"," +
                    "                \"type\": \"3\"," +
                    "                \"status\": \"1\"" +
                    "        }" +
                    "    ]" +
                    "}"
                    ;
*/

            Log.d("Response: ", "> " + jsonStr);


            if (jsonStr != null) {
                if (jsonStr.contains("404 Not Found"))
                    db.updateLastModified();
                else {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // Getting JSON Array node
                        JSONArray parts = jsonObj.getJSONArray(TAG_PRODUCTS);
                        for (int i = 0; i < parts.length(); i++) {
                            JSONObject c = parts.getJSONObject(i);

                            int id = c.getInt(TAG_ID);
                            String desc = c.getString(TAG_DESCRIPTION);
                            int carType = c.getInt(TAG_TYPE);
                            int status = c.getInt(TAG_STATUS);

                            if (db.getPartById(id) != null)
                                db.updatePart(new Part(id, desc, carType, status));
                            else
                                db.insertPart(new Part(id, desc, carType, status));

                        }
                        db.updateLastModified();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
