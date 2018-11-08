package com.example.bryantyrrell.vdiapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import android.widget.Toast;

import com.example.bryantyrrell.vdiapp.Database.FirstUseDBCreation;
import com.example.bryantyrrell.vdiapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterPageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

    }

    public void extractcreds(View view) {

        EditText usernameBox = findViewById(R.id.Email);
        EditText passwordBox = findViewById(R.id.password);
        String username = usernameBox.getText().toString();
        String password = passwordBox.getText().toString();

        createAccount(username, password);

    }


    public void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            setUpUser(user.getUid(),user.getEmail());
                            updateToHomeScreen(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterPageActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            reportError();
                        }

                        // ...
                    }
                });

    }

    private void reportError() {

    }


    // brings you to a new page
    private void updateToHomeScreen(Object o) {
        // send info of user to retrive their data
        // change to home page
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
    }
    private void setUpUser(String uid, String UserName) {
        // set up users instance on db
         new FirstUseDBCreation(uid,UserName);


        // change to home page
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
    }

    public void updateToLogInScreen(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
