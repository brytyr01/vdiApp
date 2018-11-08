package com.example.bryantyrrell.vdiapp.Database;

import android.content.Context;

import com.example.bryantyrrell.vdiapp.Activities.RegisterPageActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstUseDBCreation {

    private String userID,userName;
    private String users="users";
    DocumentReference userDocument;
    Context context;
            ;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FirstUseDBCreation(String uid, String userName, Context context) {

        userID=uid;
        this.userName=userName;
        createUserDocuments();

        this.context=context;
    }

    private void createUserDocuments() {
        GeoPoint gps = new GeoPoint(0,0);

        Map<String, Object> Pings = new HashMap<>();
        Map<String, GeoPoint> FirstPing = new HashMap<>();
        FirstPing.put("TestPing", gps);
        Pings.put("Array of GeoPoints", FirstPing);

        Map<String, Object> Routes = new HashMap<>();
        Map<String, String> FirstRoute = new HashMap<>();
        FirstRoute.put("Test", "");
        Routes.put("Routes",FirstRoute);

         userDocument = db.collection(users).document((userName+userID));
        // userDocument.collection("GPS_Location").document("GPS_Pings").set(FirstPing);
         //userDocument.collection("GPS_Location").document("GPS_Map_Points").set(FirstPing);
        userDocument.collection("GPS_Location").document("RouteNames").set(Routes);
    }

    public void CreateUserFiles(){

        File file = new File(context.getFilesDir(), "StoredRouteNames");
        System.out.println("The new file location is: "+file.getAbsolutePath());
    }
}
