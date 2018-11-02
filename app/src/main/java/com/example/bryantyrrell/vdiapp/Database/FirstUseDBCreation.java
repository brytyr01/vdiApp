package com.example.bryantyrrell.vdiapp.Database;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstUseDBCreation {

    private String userID,userName;
    private String users="users";
    DocumentReference userDocument;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FirstUseDBCreation(String uid, String userName) {

        userID=uid;
        this.userName=userName;
        createUserDocuments();
    }

    private void createUserDocuments() {
        GeoPoint gps = new GeoPoint(0,0);

        Map<String, Object> Pings = new HashMap<>();
        Map<String, GeoPoint> FirstPing = new HashMap<>();
        FirstPing.put("TestPing", gps);
        Pings.put("Array of GeoPoints", FirstPing);


         userDocument = db.collection(users).document((userName+userID));
         userDocument.collection("GPS_Location").document("GPS_Pings").set(Pings);
        userDocument.collection("GPS_Location").document("GPS_Map_Points").set(Pings);
    }


}
