package com.hfad.faceclassifier.Common;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hfad.faceclassifier.HelperClasses.SliderAdapter;
import com.hfad.faceclassifier.HomeActivity;
import com.hfad.faceclassifier.LoginSignup.StartUpScreen;
import com.hfad.faceclassifier.R;

public class OnBoarding extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout dotsLayout;

    SliderAdapter sliderAdapter;

    TextView[] dots;
    Button letsGetStarted;

    Animation animation;

    // Position of the current slide
    int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);
        letsGetStarted = findViewById(R.id.get_started_btn);

        // Call adapter
        sliderAdapter = new SliderAdapter(this);

        // Sets the adapter
        viewPager.setAdapter(sliderAdapter);

        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);


    }

    // This method gets called when the user press the skip button
    public void skip(View view) {
        startActivity(new Intent(OnBoarding.this, StartUpScreen.class));
        // Don't come back to this activity
        finish();
    }

    // This method gets called when the user press the next button
    public void next(View view) {
        // Moves to the next slide
        viewPager.setCurrentItem(currentPosition+1);
    }


    public void get_started(View view) {
        startActivity(new Intent(OnBoarding.this, StartUpScreen.class));
        // Don't come back to this activity
        finish();
    }

    private void addDots(int position) {
        // Four slides
        dots = new TextView[4];

        // Clear the view
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            // Get the dots from HTML
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);

            // Add these dots to layout
            dotsLayout.addView(dots[i]);

        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            currentPosition = position;

            // Pass which page we are on
            addDots(position);

            // Show let's get started button on the 4th page
            if (position == 0) {
                letsGetStarted.setVisibility(View.INVISIBLE);
            } else if (position == 1) {
                letsGetStarted.setVisibility(View.INVISIBLE);
            } else if (position == 2) {
                letsGetStarted.setVisibility(View.INVISIBLE);
            } else if (position == 3) {
                animation = AnimationUtils.loadAnimation(OnBoarding.this, R.anim.bottom_anim);
                letsGetStarted.setAnimation(animation);
                letsGetStarted.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


}