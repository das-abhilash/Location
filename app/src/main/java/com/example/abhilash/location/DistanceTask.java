package com.example.abhilash.location;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Abhilash on 1/30/2016.
 */
public class DistanceTask extends AsyncTask<String, Integer, ArrayList<Loc>> { // it'll recieve the location json string as input



    private MainActivity mainActivity;

    public DistanceTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }







    @Override
    protected ArrayList<Loc> doInBackground(String... params) {


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String disStr = "";

        ArrayList<Loc> Locs = new ArrayList<>();

        Locs = mainActivity.getData(params[0]);

        InputStream inputStream = null;
        for (int i = 0;i < Locs.size();i++){


            // here i m using http
            try {
                URL url = new URL(Locs.get(i).getUrl());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            disStr = buffer.toString();

            Locs.get(i).setJson(disStr);

        }

        return Locs;
    }



    @Override
    protected void onPostExecute(ArrayList<Loc> Locs) {


        for (int i = 0; i < Locs.size(); i++){

            try {
                JSONObject distJsn = new JSONObject(Locs.get(i).getJson());
                JSONArray routesArray = distJsn.getJSONArray("routes");

                JSONObject c = routesArray.getJSONObject(0);

                JSONArray legsArray = c.getJSONArray("legs");

                JSONObject distanceObj = legsArray.getJSONObject(0);

                JSONObject distance = distanceObj.getJSONObject("distance");

                double dist = Double.parseDouble(String.valueOf(distance.getInt("value")));

                Locs.get(i).setDistance(dist/1000);
                //String text = distance.getString("text");


            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        Collections.sort(Locs, new Comparator<Loc>() {
            public int compare(Loc loc1, Loc loc2) {
                return Double.compare(loc1.getDistance(), (loc2.getDistance()));
            }
        });

        mainActivity.displayInList(Locs);
        mainActivity.enableButton();


    }


}
