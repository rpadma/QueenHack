package com.etuloser.padma.rohit.amap;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.etuloser.padma.rohit.amap.Model.Place;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<LatLng> markerPoints;
    ArrayList<Place> placeList;
    private UiSettings mUiSettings;
    ArrayList<LatLng> modeldata;
    LatLngBounds.Builder b   = new LatLngBounds.Builder();;
    LatLng slat;
    LatLng dlat;
    PolylineOptions  pOptions = new PolylineOptions();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        markerPoints = new ArrayList<LatLng>();
        placeList=new ArrayList<>();

        if(getIntent().getExtras()!=null)
        {
            placeList=(ArrayList<Place>) getIntent().getExtras().getSerializable("Placeobj");

            Intent intent = getIntent();
            Bundle args = intent.getBundleExtra("BUNDLE");
            placeList = (ArrayList<Place>) args.getSerializable("Placeobj");
            modeldata = (ArrayList<LatLng>) args.getSerializable("lllist");

            Log.d("Master data count:",String.valueOf(modeldata.size()));
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        for (int i=0;i<placeList.size();i++) {
            if(i<placeList.size()-1) {
                //placeList.get(i).getLatLng().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                 slat=new LatLng(Double.valueOf(placeList.get(i).getLatitude()),Double.valueOf(placeList.get(i).getLongitude()));
                 dlat=new LatLng(Double.valueOf(placeList.get(i+1).getLatitude()),Double.valueOf(placeList.get(i+1).getLongitude()));

                String url = getDirectionsUrl(slat,dlat);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }
            else{

                 slat=new LatLng(Double.valueOf(placeList.get(i).getLatitude()),Double.valueOf(placeList.get(i).getLongitude()));
                dlat=new LatLng(Double.valueOf(placeList.get(0).getLatitude()),Double.valueOf(placeList.get(0).getLongitude()));

                String url = getDirectionsUrl(slat, dlat);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);

            }
        }


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
        mMap=googleMap;


        if (mMap != null) {

            mMap.addMarker(new MarkerOptions()
                    .position(slat)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .title("Source Marker"));

            mMap.addMarker(new MarkerOptions()
                    .position(dlat)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title("Destination Marker"));

            mUiSettings=mMap.getUiSettings();
            mMap.getUiSettings().setZoomControlsEnabled(true);

          //  pOptions.add(new LatLng(slat.latitude,slat.longitude));
            b.include(new LatLng(slat.latitude,dlat.longitude));
            b.include(new LatLng(dlat.latitude,dlat.longitude));
            LatLngBounds bounds = b.build();

            int width = getResources().getDisplayMetrics().widthPixels;
            int height=getResources().getDisplayMetrics().heightPixels;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width,height,250);
            mMap.animateCamera(cu);


            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);



        }
    }


    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }


    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionJsonParser parser = new DirectionJsonParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);



                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    DecimalFormat df = new DecimalFormat("#.##");
                    double tlat=Double.valueOf(df.format(position.latitude));
                    double tlog=Double.valueOf(df.format(position.longitude));
                    LatLng temppos=new LatLng(tlat,tlog);

                   // points.add(position);


                    if(modeldata.contains(temppos)) {
                        lineOptions.add(position);
                        lineOptions.width(4);
                        lineOptions.color(Color.RED);
                        Log.d("Points i:"+i+"j:"+j,position.toString());

                    }
                    else
                    {
                        lineOptions.add(position);
                        lineOptions.width(4);
                        lineOptions.color(Color.GREEN);
                    }
                    Log.d("Points i:"+i+"j:"+j,String.valueOf(lat)+","+String.valueOf(lng));
                }

                // Adding all the points in the route to LineOptions


                  //  lineOptions.addAll(points);
                   // lineOptions.width(4);
                   // lineOptions.color(Color.RED);


            }

            mMap.addPolyline(lineOptions);

            
        }
    }

}
