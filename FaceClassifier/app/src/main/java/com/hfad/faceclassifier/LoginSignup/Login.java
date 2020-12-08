package com.hfad.faceclassifier.LoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hfad.faceclassifier.HomeActivity;
import com.hfad.faceclassifier.R;

public class Login extends AppCompatActivity {

    // UI elements
    TextInputLayout email, password;
    RelativeLayout progressBar;

    Button forgetPassword;

    String emailStr, passwordStr;

    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_email);
        password  = findViewById(R.id.login_password);
        progressBar = findViewById(R.id.login_progress_bar);
        forgetPassword = findViewById(R.id.login_forget_password);
        progressBar.setVisibility(View.GONE);

        fAuth = FirebaseAuth.getInstance();

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onForgotPassword(v);
            }
        });
    }


    // Sends a link to the email that the user uses to register to reset the password. The dialog
    // box is used to handle the email password reset
    public void onForgotPassword(View v) {

        final View forgetPasswordPrompt = getLayoutInflater().inflate(R.layout.forget_password_layout, null);
        final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(this);
        passwordResetDialog.setView(forgetPasswordPrompt);

        passwordResetDialog.setTitle("Reset Password ?");
        passwordResetDialog.setMessage("Enter email to receive reset link");

        // The user wants to reset password
        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // extract the email and send reset link
                TextInputLayout forgetPasswordEmail = forgetPasswordPrompt.findViewById(R.id.forget_password_email);
                String mail = forgetPasswordEmail.getEditText().getText().toString();

                fAuth.sendPasswordResetEmail(mail)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Reset Link Sent to Email", Toast.LENGTH_SHORT).show();
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        // The user don't want to reset password
        passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // close the dialog
            }
        });

        // Create and show alert dialog
        passwordResetDialog.create().show();
    }
    /******
     *
     * This function validates the field on the login page
     */
    private boolean validateFields(){
        progressBar.setVisibility(View.VISIBLE);
        emailStr = email.getEditText().getText().toString().trim();
        passwordStr = password.getEditText().getText().toString().trim();

        if(emailStr.isEmpty()){
            email.setError("Email cannot be empty");
            email.requestFocus();
            return false;
        } else if(passwordStr.isEmpty()){
            password.setError("Password cannot be empty");
            password.requestFocus();
            return false;
        } else {
            return true;
        }

    }


    // User login to the app
    public void userLogIn(View view) {

        if(!validateFields())
            return;

        progressBar.setVisibility(View.VISIBLE);

        // authenticate the user
        fAuth.signInWithEmailAndPassword(emailStr,passwordStr).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Toast.makeText(Login.this, "Successful Log in", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }

                else {
                    Toast.makeText(Login.this, "Login Error!\n" +
                            task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }


}