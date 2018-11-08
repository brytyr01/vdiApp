package com.example.bryantyrrell.vdiapp.Activities;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.bryantyrrell.vdiapp.Database.DatabaseService;
import com.example.bryantyrrell.vdiapp.GEOPoint;
import com.example.bryantyrrell.vdiapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PastMapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private FirebaseAuth mAuth;
    private ArrayList<GeoPoint> GPSPoints;
    private DatabaseService DatabaseAccoser;
    private GoogleMap mMap;
    private String routeName;
    GeoPoint geoPoint;
    GeoPoint point;
    private ArrayList<GeoPoint> postProcessedPoints = new ArrayList<GeoPoint>();
    ;
    GEOPoint use;
    LatLng LtLngPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        routeName = getIntent().getStringExtra("RouteName");
        System.out.println("The route got to the past map activity: " + routeName);
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

        // Add a marker in Sydney and move the camera


        getGPSPoints();
    }

    private void getGPSPoints() {

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseAccoser = new DatabaseService(user.getUid(), user.getEmail());

        DocumentReference userDocument = DatabaseAccoser.getUserDocument();
        System.out.println(routeName);
        userDocument.collection("GPS_Location").document(routeName).collection("GPS_Pings").document("Processed_GPS_Pings").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    for (int i = 1; document.contains("ping" + i); i++) {
                        String name = "ping" + i;
                        point = (GeoPoint) document.getGeoPoint(name);
                        if (point != null) {
                            try {
                                geoPoint = new GeoPoint(point.getLatitude(), point.getLongitude());
                                System.out.println(point.toString() + point.getLatitude());
                                postProcessedPoints.add(geoPoint);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            RedrawLine();
                        }

                    }
                    RedrawLine();


                    // ArrayList<GeoPoint> GPSPoints = (ArrayList<GeoPoint>) document.getData();//"Array of GeoPoints"ArrayList<LatLng>
                    // ArrayList<Object> map =(ArrayList<Object>) (document.getData());

                    //  use = document.toObject(GEOPoint.class);
                    // HashMap<String, GeoPoint> userPrivacy = use.getGEOPoint();


                    // System.out.println(userPrivacy.toString());
//                        for (String name: map.keySet()){
//
//                            System.out.println(name);
//                            System.out.println(map);
//                        }
//                         System.out.println(GPSPoints.toString());
//                        //String json = GPSPoints.toString();
//                        Gson gson = new Gson();
//                        JsonElement jsonElement = gson.toJsonTree(GPSPoints.toString());
//                       // System.out.println("JSON Element"+jsonElement.toString());
//                        System.out.println(jsonElement.toString());
//                        GeoPoint point = gson.fromJson(jsonElement, GeoPoint.class);

                    //System.out.println(point.toString());
// if(array.contains("GeoPoints")){}
                    //                       try {
//                            JSONObject obj = new JSONObject(GPSPoints.toString());
//                            JSONArray geodata = jsonElement.getJSONArray("Array of GeoPoints");
//                            final int n = geodata.length();
//                            for (int i = 0; i < n; ++i) {
//                                JSONObject geopoint = geodata.getJSONObject(i);
//                                System.out.println(geopoint.toString());
//                                if (geopoint.has("GeoPoint")) {
//                                    System.out.println("Double processed:" + geopoint.getJSONObject("location"));
//                                    JSONObject obje = geopoint.getJSONObject("location");
//                                    System.out.println("Double processed:" + obje.getDouble("longitude"));
//                                    LatLng point = new LatLng(obje.getDouble("latitude"), obje.getDouble("longitude"));
//                                    postProcessedPoints.add(point);
//                                }
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }


                }
            }


        });
    }


    private void ConvertGPSPoints() {

        for (int i = 0; i < GPSPoints.size(); i++) {
            System.out.println(GPSPoints.get(i).toString());

            //System.out.println();

        }

    }

    private void RedrawLine() {
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);

        for (int i = 0; i < postProcessedPoints.size(); i++) {
            //System.out.println(postProcessedPoints.size());
            GeoPoint point = postProcessedPoints.get(i);
            LtLngPoint = new LatLng(point.getLatitude(), point.getLongitude());
            options.add(LtLngPoint);

        }
        //add Marker in current position
        mMap.addPolyline(options); //add Polyline


        mMap.addMarker(new MarkerOptions().position(LtLngPoint).title("Marker for route"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(LtLngPoint));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(LtLngPoint.latitude, LtLngPoint.longitude))
                .zoom(16)
                .build();

        if (mMap != null)
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    }

