package com.hfad.faceclassifier.LoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hfad.faceclassifier.HomeActivity;
import com.hfad.faceclassifier.R;

import java.util.HashMap;
import java.util.Map;

public class Signup3rd_page extends AppCompatActivity {

    public static final String TAG = "Signup3rd Page";

    ImageView backBtn;
    TextInputLayout phoneNumber;

    String fullNameStr, emailStr, passwordStr, selectedGenderStr, birthday,
            phoneNumberStr;

    ProgressBar progressBar;

    // FirebaseAuth to register user
    FirebaseAuth firebaseAuth;

    // Firebase Cloud Firestore
    FirebaseFirestore firebaseFirestore;

    // Unique user ID
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup3rd_page);

        backBtn = findViewById(R.id.signup3_back_button);
        phoneNumber = findViewById(R.id.signup_phonenumber);
        progressBar = findViewById(R.id.progress_bar_signup3rdpage);
        fullNameStr = getIntent().getExtras().getString("Full Name");
        emailStr = getIntent().getExtras().getString("Email");
        passwordStr = getIntent().getExtras().getString("Password");
        selectedGenderStr = getIntent().getExtras().getString("Gender");
        birthday = getIntent().getExtras().getString("Birthday");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), StartUpScreen.class));
            }
        });



    }

    private boolean validatePhoneNumber(){
        phoneNumberStr = phoneNumber.getEditText().getText().toString().trim();

        if(phoneNumberStr.isEmpty()) {
            phoneNumber.setError("Field can not be empty");
            return false;
        }
        else
            return true;
    }


    public void SignupButtonClicked(View view) {

        if(!validatePhoneNumber())
            return;

        storeNewUserData();
    }

    private void storeNewUserData() {

        // Show progress bar as the signup starts
        progressBar.setVisibility(View.VISIBLE);

        // Register the user in the firebase
        firebaseAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // User successfully registered
                        if(task.isSuccessful()) {

                            Toast.makeText(Signup3rd_page.this, "SignUp Successful", Toast.LENGTH_SHORT).show();

                            // Retrieves the user id of the currently registered or logged in user
                            userID = firebaseAuth.getCurrentUser().getUid();

                            // Creates new document with that user ID on firestore
                            DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);

                            // Put user data using HashMap
                            Map<String, Object> user = new HashMap<>();
                            user.put("fullname", fullNameStr);
                            user.put("email", emailStr);
                            user.put("gender", selectedGenderStr);
                            user.put("birthday", birthday);
                            user.put("phonenumber", phoneNumberStr);
                            // Initially set the user face shape to none
                            user.put("faceshape", "none");

                            // Insert into Firestore database
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });

                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        }


                        // Fails to register user
                        else {
                            Toast.makeText(Signup3rd_page.this, "Sign Up Failed\n! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }


                    }
                });
    }
}