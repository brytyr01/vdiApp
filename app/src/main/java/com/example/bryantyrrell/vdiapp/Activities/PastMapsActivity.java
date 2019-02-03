package com.example.bryantyrrell.vdiapp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.example.bryantyrrell.vdiapp.Database.DatabaseService;
import com.example.bryantyrrell.vdiapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import java.util.ArrayList;

public class PastMapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private String routeName,UserName,UserID;
    GeoPoint geoPoint;
    GeoPoint point;
    private ArrayList<GeoPoint> postProcessedPoints = new ArrayList<>();
    private ArrayList<LatLng> postProcessedLatLngPoints;
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
        UserName = getIntent().getStringExtra("UserName");
        UserID = getIntent().getStringExtra("UserID");
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
        //gets database reference
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseService DatabaseAccessor = new DatabaseService(UserID, UserName);

        DocumentReference userDocument = DatabaseAccessor.getUserDocument();

        //gets gps points in geoPoint format
        userDocument.collection("GPS_Location").document(routeName).collection("GPS_Pings").document("Processed_GPS_Pings").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // check if count starts at 0 or 1+doest account for missed ping
                        for (int i = 1; document.contains("ping" + i); i++) {
                            String name = "ping" + i;
                            point = document.getGeoPoint(name);
                            if (point != null) {
                                try {
                                    geoPoint = new GeoPoint(point.getLatitude(), point.getLongitude());
                                    postProcessedPoints.add(geoPoint);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    //once for loop has finished else empty route?
                    if(postProcessedPoints!=null&&!postProcessedPoints.isEmpty()) {
                        RedrawLine();
                    }
                }
            }


        });
    }

    private void RedrawLine() {
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        convertToLatLngList(postProcessedPoints);
        for (int i = 0; i < postProcessedLatLngPoints.size(); i++) {

            LtLngPoint = postProcessedLatLngPoints.get(i);

            if (i == 0) {
                mMap.addMarker(new MarkerOptions().position(LtLngPoint).title("Start of route"));
                //zoom to route size
                zoomToRoute();
            }

            options.add(LtLngPoint);

        }
        // adds marker to end of route
        mMap.addMarker(new MarkerOptions().position(LtLngPoint).title("End of route"));

        //add Marker in current position
        mMap.addPolyline(options); //add Polyline




    }

    private void convertToLatLngList(ArrayList<GeoPoint> postProcessedPoints) {
        postProcessedLatLngPoints=new ArrayList<>();

        for (int i = 0; i < postProcessedPoints.size(); i++) {
            GeoPoint point = postProcessedPoints.get(i);
            LtLngPoint = new LatLng(point.getLatitude(), point.getLongitude());
            postProcessedLatLngPoints.add(LtLngPoint);

        }
    }

    private void zoomToRoute() {

        if (mMap == null || postProcessedLatLngPoints == null || postProcessedLatLngPoints.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : postProcessedLatLngPoints) {
            boundsBuilder.include(latLngPoint);
        }

        int routePadding = 100;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));

    }
    // starts map activity with past route
    public void StartChatActivity(View view) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("RouteName",routeName);
        intent.putExtra("UserName",UserName);
        intent.putExtra("UserID",UserID);
        startActivity(intent);
    }
    // starts map activity with past route
    public void StartStaticticsActivity(View view) {
        Intent intent = new Intent(this, PastMapsActivity.class);
        startActivity(intent);
    }
}



