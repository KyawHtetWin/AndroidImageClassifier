package com.hfad.faceclassifier.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hfad.faceclassifier.R;

import java.util.Calendar;

public class SignUp2nd_page extends AppCompatActivity {

    // UI elements
    ImageView backBtn;
    Button next;
    TextView titleText;
    RadioGroup genderRadioGroup;
    RadioButton selectedGender;
    DatePicker datePicker;

    String fullNameStr, emailStr, passwordStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2nd_page);

        backBtn = findViewById(R.id.signup2_back_button);
        next = findViewById(R.id.signup_next_button);

        titleText = findViewById(R.id.sigup_title_text);
        genderRadioGroup = findViewById(R.id.gender_radio_group);
        datePicker = findViewById(R.id.agePicker);

        fullNameStr = getIntent().getExtras().getString("Full Name");
        emailStr = getIntent().getExtras().getString("Email");
        passwordStr = getIntent().getExtras().getString("Password");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), StartUpScreen.class));
            }
        });
    }

    private boolean validateGender(){
        // Gender not selected yet
        if(genderRadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    /***
     *  The user presses Next button on the sign up 2nd page.
     ***/
    public void callNextSignupScreen(View view) {

        if(!validateGender())
            return;

        Intent intent = new Intent(SignUp2nd_page.this, Signup3rd_page.class);

        // Get user entered information
        selectedGender = findViewById(genderRadioGroup.getCheckedRadioButtonId());

        String selectedGenderStr = selectedGender.getText().toString();
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        String birthday = day + "/" + month + "/" + year;

        intent.putExtra("Gender", selectedGenderStr);
        intent.putExtra("Birthday", birthday);
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
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp2nd_page.this,
                    pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }

    }
}