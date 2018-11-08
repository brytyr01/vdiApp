package com.example.bryantyrrell.vdiapp.Database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class DatabaseService {
    private ArrayList<String> RouteArrList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String users="users";
    private String userID,userName;
    private ArrayList<LatLng> preProcessedPoints;
    private ArrayList<LatLng> postProcessedPoints;
    DocumentReference userDocument;
    private String RouteName;
    private int count=0;
    private int count1=0;
    public DatabaseService(String uid, String userName){

        userID=uid;
        this.userName=userName;
        SetArrayLists();
        setUserDocument();

    }

    private void SetArrayLists() {
        preProcessedPoints = new ArrayList<>();
        postProcessedPoints = new ArrayList<>();
    }

    private void setUserDocument() {
        userDocument= db.collection("users").document(userName+userID);
    }

    public void updateGPSLocation(GeoPoint gpsPing){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Map<String, GeoPoint> gpsping = new HashMap<>();
        gpsping.put("ping"+timestamp.toString(),gpsPing);

        userDocument
                .collection("GPS_Location").document("GPS_Pings").update("Array of GeoPoints",FieldValue.arrayUnion(gpsping))

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }



    public DocumentReference getUserDocument(){
        return userDocument;
    }

    public void addPreGpsPoint(LatLng GPSPoint) {
        preProcessedPoints.add(GPSPoint);
        if(preProcessedPoints.size()==3){
            for(int i=0;i<preProcessedPoints.size();i++) {

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                Map<String, GeoPoint> gpsping = new HashMap<>();

                LatLng gpsPoint = preProcessedPoints.remove(i);

                GeoPoint geopint = new GeoPoint(gpsPoint.latitude,gpsPoint.longitude);
                count++;
                gpsping.put("ping" + count, geopint);
                userDocument.collection("GPS_Location").document(RouteName).collection("GPS_Pings").document("Raw_GPS_Pings").update("ping" + count,geopint)

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding pre processed gps points", e);
                            }
                        });
                if(i==(preProcessedPoints.size()-1)){//-1
                    preProcessedPoints.clear();
                }
            }

        }

    }

    public void addPostGpsPoint(LatLng GPSPoint) {
        postProcessedPoints.add(GPSPoint);
        if(postProcessedPoints.size()==3){
            for(int i=0;i<postProcessedPoints.size();i++) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                Map<String, GeoPoint> gpsping = new HashMap<>();

                LatLng gpsPoint = postProcessedPoints.remove(i);

                GeoPoint geopint = new GeoPoint(gpsPoint.latitude,gpsPoint.longitude);
                gpsping.put("ping" + timestamp.toString(), geopint);
                userDocument.collection("GPS_Location").document(RouteName).collection("GPS_Pings").document("Processed_GPS_Pings").update("ping"+count1++,geopint)//.update("Array of GeoPoints", FieldValue.arrayUnion(gpsping))


                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding pre processed gps points", e);
                            }
                        });
                if(i==(postProcessedPoints.size()-1)){
                    postProcessedPoints.clear();
                }

            }
        }


    }

    public void createRouteGpsStorage(String m_Text) {
        RouteName=new String(m_Text);

        GeoPoint gps = new GeoPoint(0,0);
        Map<String, Object> Pings = new HashMap<>();
        Map<String, GeoPoint> FirstPing = new HashMap<>();
        FirstPing.put("TestPing", gps);



        userDocument.collection("GPS_Location").document(RouteName).collection("GPS_Pings").document("Raw_GPS_Pings").set(FirstPing);//"Raw_GPS_Pings"
        userDocument.collection("GPS_Location").document(RouteName).collection("GPS_Pings").document("Processed_GPS_Pings").set(FirstPing);//Processed_GPS_Pings
    }

    public void addRouteName(String RouteName) {
        userDocument.collection("GPS_Location").document("RouteNames").update("Routes", FieldValue.arrayUnion(RouteName));//"add route names to array"
    }



    }

