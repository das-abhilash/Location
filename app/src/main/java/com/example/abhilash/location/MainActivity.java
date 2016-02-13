package com.example.abhilash.location;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.location.LocationManager.GPS_PROVIDER;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    boolean isGPSEnabled = false;

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    double lt1;
    double ln1;

    LocationManager lm;
    Location lc;

    String Ljson = "  {\"locations\" :[ {\"name\":\"The Orange Bicycle\",\"latitude\":\"12.969534\",\"longitude\":\"77.638589\"},{\"name\":\"Soch\", \"latitude\":\"13.009485\", \"longitude\":\"77.563375\"},{\"name\":\"Nauchandi\", \"latitude\":\"12.970925\", \"longitude\":\"77.648028\"},{\"name\":\"Smart Gardens\", \"latitude\":\"12.976902\", \"longitude\":\"77.654095\"},{\"name\":\"Kudoz Studio\", \"latitude\":\"12.931713\", \"longitude\":\"77.62835\"},{\"name\":\"United Colors of Benetton\",\"latitude\":\"12.986126\", \"longitude\":\"77.703706\"},{\"name\":\"Bohome\",\"latitude\":\"12.969144\",\"longitude\":\"77.638837\"},{\"name\":\"Studio by Untold Homes\",\"latitude\":\"12.964383\",\"longitude\":\"77.639478\" }, {\"name\":\"ABCD\", \"latitude\":\"12.97965\",\"longitude\":\"77.649147\"},  {\"name\":\"Bata\",   \"latitude\":\"13.009485\",     \"longitude\":\"77.563375\"  }]}";

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();

        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Button find = (Button) findViewById(R.id.find);


        find.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        isGPSEnabled = lm
                                                .isProviderEnabled(GPS_PROVIDER);

                                        if (isGPSEnabled) {
                                            RunTask(Ljson);
                                        } else {
                                            showAlert();
                                        }
                                    }
                                }
        );
    }


    protected void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        stopLocationUpdates();


    }

    @Override
    public void onConnected(Bundle bundle) {

        startLocationUpdates();
        getLocation();


    }


    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    public void showAlert() {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }


    public ArrayList<Loc> getData(String locationStr) {

        ArrayList<Loc> Locs = new ArrayList<Loc>();

        try {
            JSONObject jsonRootObject = new JSONObject(locationStr);

            JSONArray jsonArray = jsonRootObject.getJSONArray("locations");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String longitude = jsonObject.getString("longitude");
                String latitude = jsonObject.getString("latitude");

                Loc loc = new Loc();

                loc.setName(name);
                loc.setLatitude(Double.parseDouble(latitude));
                loc.setLongitude(Double.parseDouble(longitude));
                double lt2 = Double.parseDouble(String.valueOf(loc.getLatitude()));
                double ln2 = Double.parseDouble(String.valueOf(loc.getLongitude()));

                loc.setUrl(getDirectionsUrl(lt2, ln2));
                Locs.add(loc);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Locs;
    }

    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }


    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lc = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        if (lc != null) {
            lt1 = lc.getLatitude();
            ln1 = lc.getLongitude();

        }


    }

    private String getDirectionsUrl(double lat, double lon) {

        String str_origin = "origin=" + lt1 + "," + ln1;

        String str_dest = "destination=" + lat + "," + lon;

        String sensor = "sensor=false";
        String metric = "units=metric";

        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + metric;

        String output = "json";

        String url = "http://maps.google.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    public void displayInList(ArrayList<Loc> Locs) {

        ListAdapter myAdapter = new CustomAdapter(this, Locs);
        ListView myList = (ListView) findViewById(R.id.location_list);

        myList.setAdapter(myAdapter);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);

                Loc loc = (Loc) parent.getItemAtPosition(position);

                i.putExtra("latitude", loc.getLatitude());
                i.putExtra("longitude", loc.getLongitude());
                i.putExtra("name", loc.getName());
                i.putExtra("path", loc.getPath());

                startActivity(i);

            }
        });

    }

    public void RunTask(String ljson) {
        disableButton();
        new DistanceTask(this).execute(ljson);

    }

    public void disableButton() {
        Button find = (Button) findViewById(R.id.find);
        find.setEnabled(false);
    }

    public void enableButton() {
        Button find = (Button) findViewById(R.id.find);
        find.setEnabled(true);
    }
// calculate distacne between two points with mathematical formula

    /* public double CalculationByDistance(LatLng StartP, LatLng EndP) {
int Radius = 6371;// radius of earth in Km
double lat1 = StartP.latitude;
double lat2 = EndP.latitude;
double lon1 = StartP.longitude;
double lon2 = EndP.longitude;
double dLat = Math.toRadians(lat2 ‐ lat1);
double dLon = Math.toRadians(lon2 ‐ lon1);
double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
+ Math.cos(Math.toRadians(lat1))
* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
* Math.sin(dLon / 2);
double c = 2 * Math.asin(Math.sqrt(a));
double valueResult = Radius * c;
double km = valueResult / 1;
DecimalFormat newFormat = new DecimalFormat("####");
int kmInDec = Integer.valueOf(newFormat.format(km));
double meter = valueResult % 1000;
int meterInDec = Integer.valueOf(newFormat.format(meter));
Log.i("Radius Value", "" + valueResult + " KM " + kmInDec
+ " Meter " + meterInDec);
return Radius * c;
} */

}
