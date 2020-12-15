package com.hfad.faceclassifier.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.hfad.faceclassifier.R;

public class SignUp extends AppCompatActivity {

    // UI elements
    ImageView backBtn;
    Button next;
    TextView titleText;
    TextInputLayout fullName, email, password;

    String fullNameStr, emailStr, passwordStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        backBtn = findViewById(R.id.signup_back_button);
        next = findViewById(R.id.signup_next_button);

        titleText = findViewById(R.id.sigup_title_text);
        fullName = findViewById(R.id.signup_fullname);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), StartUpScreen.class));
            }
        });
    }


    /***
     *  The following functions perform validation on field that user enters & returns true if the
     *  user information can be validated.
     ***/
    private boolean validateFullName(){
        fullNameStr = fullName.getEditText().getText().toString().trim();

        if(fullNameStr.isEmpty()) {
            fullName.setError("Field can not be empty");
            return false;
        } else {
            fullName.setError(null);
            // Removes space of error message
            fullName.setErrorEnabled(false);
            return true;
        }
    }


    private boolean validateEmail(){
        emailStr = email.getEditText().getText().toString().trim();

        // To check that correct email pattern matches
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";

        if(emailStr.isEmpty()) {
            email.setError("Field can not be empty");
            return false;
        }

        if(!emailStr.matches(checkEmail)){
            email.setError("Invalid Email");
            return false;
        }

        else {
            email.setError(null);
            // Removes space of error message
            email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        passwordStr = password.getEditText().getText().toString().trim();

        String checkPassword = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";

        if (passwordStr.isEmpty()) {
            password.setError("Field can not be empty");
            return false;
        }

        else if (passwordStr.length() < 6 ) {
            password.setError("Password should contain at least 6 characters!");
            return false;
        }

        else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    /***
     *  The user presses Next button on the sign up page.
     ***/
    public void callNextSignupScreen(View view) {

        if(!validateFullName() | !validateEmail() |!validatePassword())
            return;

        Intent intent = new Intent(SignUp.this, SignUp2nd_page.class);

        // Pass on user information
        intent.putExtra("Full Name", fullNameStr);
        intent.putExtra("Email", emailStr);
        intent.putExtra("Password", passwordStr);

        // Add Transition
        Pair[] pairs = new Pair[3];
        pairs[0] = new Pair<View, String>(backBtn, "transition_back_arrow_btn");
        pairs[1] = new Pair<View, String>(next, "transition_next_btn");
        pairs[2] = new Pair<View, String>(titleText, "transition_title_text");

        // Animation only words for version greater than lollipop
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp.this,
                    pairs);
            startActivity(intent, options.toBundle());
        }

        else {
            startActivity(intent);
        }


    }
}