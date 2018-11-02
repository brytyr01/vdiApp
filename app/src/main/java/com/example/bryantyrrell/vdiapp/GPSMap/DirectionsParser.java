package com.example.bryantyrrell.vdiapp.GPSMap;
import android.graphics.Color;
import android.os.AsyncTask;

import com.example.bryantyrrell.vdiapp.Database.DatabaseService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
// this class takes the map and geo points and draws a road line
//The Roads API identifies the roads a vehicle was traveling along
//and provides additional metadata about those roads, such as speed limits.

public class DirectionsParser {
    private Polyline line;
    private GoogleMap mMap;
    private ArrayList<LatLng> preProcessedPoints;
    private ArrayList<LatLng> postProcessedPoints;
    private BufferedReader reader;
    private String JSONFile;
    private boolean result = false;
    private DatabaseService databaseService;
    private String userID,userName;


    DirectionsParser(GoogleMap mMap, ArrayList<LatLng> storedPoints,DatabaseService databaseUser) {
        this.mMap = mMap;
        this.preProcessedPoints = storedPoints;
        postProcessedPoints=new ArrayList<>();

        databaseService=databaseUser;
    }


    public void URLstringBuilder() {

        StringBuilder path = new StringBuilder();
        path.append("https://roads.googleapis.com/v1/snapToRoads?path=");

        for (int i = 0; i < preProcessedPoints.size(); i++) {
            LatLng gpsPoint = preProcessedPoints.get(i);

            if (i == (preProcessedPoints.size() - 1)) {
                path.append(gpsPoint.latitude + "," + gpsPoint.longitude + "&");
            } else {
                path.append(gpsPoint.latitude + "," + gpsPoint.longitude + "|");
            }
        }
        path.append("interpolate=true&");
        path.append("key=AIzaSyBIXmHTXWs1LfOc7E6ERSGMQRWd4sA6swM");

        System.out.println("This is working: " + path.toString());

        SnapToRoadAPICall SnapToRoad = new SnapToRoadAPICall();
        SnapToRoad.execute(path.toString());

    }

    private void ParseJsonResult() {


        try {
            JSONObject obj = new JSONObject(JSONFile);
            System.out.println(obj.toString());
            JSONArray geodata = obj.getJSONArray("snappedPoints");
            final int n = geodata.length();
            for (int i = 0; i < n; ++i) {
                JSONObject geopoint = geodata.getJSONObject(i);
                System.out.println(geopoint.toString());
                if (geopoint.has("location")) {
                    System.out.println("Double processed:" + geopoint.getJSONObject("location"));
                    JSONObject obje = geopoint.getJSONObject("location");
                    System.out.println("Double processed:" + obje.getDouble("longitude"));
                    LatLng point = new LatLng(obje.getDouble("latitude"), obje.getDouble("longitude"));
                    postProcessedPoints.add(point);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RedrawLine();

    }

    private void RedrawLine() {
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);

        for (int i = 0; i < postProcessedPoints.size(); i++) {
            LatLng point = postProcessedPoints.get(i);
            options.add(point);
//            addMarker(point);
        }
        //add Marker in current position
        line = mMap.addPolyline(options); //add Polyline

        // takes old gps points out of the array and sends them to the database class to be uploaded
        RemoveGPSPoints();
    }
    // takes old gps points out of the array and sends them to the database class to be uploaded
    private void RemoveGPSPoints() {
        // leaves 1 point for accuracy
        for(int i=0;i<(preProcessedPoints.size()-1);i++) {

           databaseService.addPreGpsPoint(preProcessedPoints.remove(i));

       }
        for(int i=0;i<(postProcessedPoints.size());i++) {

            databaseService.addPostGpsPoint(postProcessedPoints.remove(i));
        }


        // clears all the points from the preprocessed and processed arraylist

        postProcessedPoints.clear();

    }


    private class SnapToRoadAPICall extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            //https://stackoverflow.com/questions/2938502/sending-post-data-in-android
            StringBuilder jsonStringBuilder= new StringBuilder();

            // Making HTTP request
            try {
                //String temp = URLEncoder.encode(urlPath, "UTF-8");
                URL url = new URL(strings[0]);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                //urlConnection.connect();
//                urlConnection.setReadTimeout(1 * 1000);
                urlConnection.connect();

                //System.out.println(urlConnection.getResponseCode());
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());


                    //InputStream stream = urlConnection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(in));



                    StringBuffer buffer = new StringBuffer();
                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                        jsonStringBuilder.append(line);
                        jsonStringBuilder.append("\n");
                    }
                    System.out.println(jsonStringBuilder.toString());
                } catch (Exception e) {
                    System.out.println(e.getMessage() + "first error message");
                }
            } catch (MalformedURLException e) {
                // Replace this with your exception handling
                System.out.println(e.getMessage() + "second error message");
                e.printStackTrace();
            } catch (IOException e) {
                // Replace this with your exception handling
                System.out.println(e.getMessage() + "third error message");
                e.printStackTrace();
            }
            System.out.println(jsonStringBuilder.toString());
            JSONFile=jsonStringBuilder.toString();
            result=true;
            return jsonStringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String GeoResult) {

            JSONFile=GeoResult;
            result=true;
            ParseJsonResult();
            //also access database to send new geopoints to users database
        }

    }

}