package com.example.bryantyrrell.vdiapp.Database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.bryantyrrell.vdiapp.Chat.ChatMessage;
import com.example.bryantyrrell.vdiapp.Users;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static android.support.constraint.Constraints.TAG;

public class DatabaseService {
    private FirebaseFirestore db;
    private String userID, userName, RouteName;
    private ArrayList<LatLng> preProcessedPoints, postProcessedPoints, cachedProcessedPoints;
    private ArrayList<String> LocationID;
    private DocumentReference userDocument;
    private int count, count1, count2, chatCount = 0;

    //constructor
    public DatabaseService(String uid, String userName) {

        userID = uid;
        this.userName = userName;
        preProcessedPoints = new ArrayList<>();
        postProcessedPoints = new ArrayList<>();
        LocationID = new ArrayList<>();
        cachedProcessedPoints = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        setUserDocument();

    }

    public DatabaseService(String uid, String email, String RouteName) {
        userID = uid;
        this.userName = email;
        this.RouteName = RouteName;
        db = FirebaseFirestore.getInstance();
        setUserDocument();
    }


    private void setUserDocument() {
        userDocument = db.collection("users").document(userName + userID);
    }


    public DocumentReference getUserDocument() {
        return userDocument;
    }

    public void addPreGpsPoint(LatLng GPSPoint) {
        // adds gps update to arraylist
        preProcessedPoints.add(GPSPoint);


        if (preProcessedPoints.size() >= 3) {
            for (int i = 0; i < preProcessedPoints.size(); i++) {
                // remove from queue
                LatLng gpsPoint = preProcessedPoints.remove(i);
                GeoPoint geopoint = new GeoPoint(gpsPoint.latitude, gpsPoint.longitude);

                //upload gps point to db
                userDocument.collection("GPS_Location").document(RouteName).collection("GPS_Pings").document("Raw_GPS_Pings").update("ping" + count++, geopoint)

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding pre processed gps points", e);
                            }
                        });
            }
            preProcessedPoints.clear();
        }
    }

    public void addPostGpsPoint(LatLng GPSPoint) {
        postProcessedPoints.add(GPSPoint);
        cachedProcessedPoints.add(GPSPoint);
        if (cachedProcessedPoints.size() == 2) {
            //send two points to gps class
            //clear array
        }

        if (postProcessedPoints.size() >= 3) {
            for (int i = 0; i < postProcessedPoints.size(); i++) {
                // remove from queue
                LatLng gpsPoint = postProcessedPoints.remove(i);
                //conversion from latLng to GeoPoint
                GeoPoint geopoint = new GeoPoint(gpsPoint.latitude, gpsPoint.longitude);
                userDocument.collection("GPS_Location").document(RouteName).collection("GPS_Pings").document("Processed_GPS_Pings").update("ping" + count1++, geopoint)

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding pre processed gps points", e);
                            }
                        });

            }
            postProcessedPoints.clear();
        }
    }

    public void createRouteGpsStorage(String route) {
        RouteName = route;
        GeoPoint gps = new GeoPoint(0, 0);
        Map<String, GeoPoint> FirstPing = new HashMap<>();
        FirstPing.put("TestPing", gps);
        Map<String, String> FirstLocationPing = new HashMap<>();
        FirstLocationPing.put("TestPing", "FirstPoint");
        //set up raw and processed gps documents
        userDocument.collection("GPS_Location").document(RouteName).collection("GPS_Pings").document("Raw_GPS_Pings").set(FirstPing);//"Raw_GPS_Pings"
        userDocument.collection("GPS_Location").document(RouteName).collection("GPS_Pings").document("Processed_GPS_Pings").set(FirstPing);//Processed_GPS_Pings
        userDocument.collection("GPS_Location").document(RouteName).collection("GPS_Pings").document("location_Pings").set(FirstLocationPing);//Processed_location_strings
        addRouteName(route);
    }

    private void addRouteName(String RouteName) {
        userDocument.collection("GPS_Location").document("RouteNames").update("Routes", FieldValue.arrayUnion(RouteName));//"add route names to array"
    }

    public void SendChatMessages(ChatMessage chatMessage) {


        //+new Date().getTime()
        userDocument.collection("ChatMessages").document(RouteName).collection("Messages").add(chatMessage);


    }


    public void addLocationPoint(String locationString) {
        LocationID.add(locationString);
        if (LocationID.size() >= 3) {
            for (int i = 0; i < LocationID.size(); i++) {
                // remove from queue
                String LocationPoint = LocationID.remove(i);

                userDocument.collection("GPS_Location").document(RouteName).collection("GPS_Pings").document("location_Pings").update("ping" + count2++, LocationPoint)

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding pre processed gps points", e);
                            }
                        });

            }
            postProcessedPoints.clear();
        }
    }

    public void AcceptFriendRequest(Users user) {
        FirebaseAuth Auth = FirebaseAuth.getInstance();
        db.collection("users").document(user.getUserName() + user.getUserID()).collection("FriendsList").add(new Users(userName, userID, Auth.getCurrentUser().getDisplayName(), false));
        userDocument.collection("FriendsList").add(user);
    }

    public void removePendingFriend(Users user) {
        FirebaseAuth Auth = FirebaseAuth.getInstance();
        db.collection("users").document(Auth.getCurrentUser().getEmail()+Auth.getCurrentUser().getUid()).collection("PendingFriendRequest").document(user.getUserID()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
        db.collection("users").document(user.getUserName()+user.getUserID()).collection("PendingFriendRequest").document(Auth.getCurrentUser().getUid()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }
}

