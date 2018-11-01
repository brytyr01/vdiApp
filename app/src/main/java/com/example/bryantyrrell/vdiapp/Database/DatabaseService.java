package com.example.bryantyrrell.vdiapp.Database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class DatabaseService {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String users="users";
    private String userID,userName;
    DocumentReference userDocument;

    public DatabaseService(String uid, String userName){

        userID=uid;
        this.userName=userName;
        setUserDocument();

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



    public DocumentReference getGPSDocument(DatabaseService collectionName){


        return userDocument;
    }
}
