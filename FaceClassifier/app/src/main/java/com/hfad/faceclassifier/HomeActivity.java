package com.hfad.faceclassifier;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.hfad.faceclassifier.LoginSignup.StartUpScreen;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class HomeActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener,
        EasyPermissions.PermissionCallbacks {

    private static final int MULTIPLE_PERMISSIONS = 7777;

    private static final float END_SCALE = 0.7f;
    /************** UI Components **************/
    // Drawer Menu
    DrawerLayout drawerLayout;
    // Navigation View
    NavigationView navigationView;
    // Menu Icon
    ImageView menuIcon;

    // Main Layout
    LinearLayout contentView;

    BrowseHairStylesFragment browseHairStylesFragment;

    /*
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.hardware.camera.autofocus" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    */

    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Initialize UI Components
        drawerLayout   = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content);

        navigationDrawer();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();

        }

    }

    /**************
     This function deals with all the actions associated with the Navigation View
     **************/

    private void navigationDrawer() {
        // Navigation Drawer
        navigationView.bringToFront(); // To interact with navigation drawer
        navigationView.setNavigationItemSelectedListener(this); // Make items clickable

        navigationView.setCheckedItem(R.id.nav_home); // Make home selected by default

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check if the drawerLayout is visible
                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    // Close the drawer
                    drawerLayout.closeDrawer(GravityCompat.START);

                else
                    // Open the drawer if it's not opened already
                    drawerLayout.openDrawer(GravityCompat.START);

            }
        });

        animateNavigationDrawer();

    }


    /**************
     This function deals with animations of changing color when the user draws the navigation
     drawer.
     **************/

    private void animateNavigationDrawer() {

        //Add any color or remove it to use the default one!
        //To make it transparent use Color.Transparent in side setScrimColor();
        //drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                // contentView for scaling at some value
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }

        });


    }

    /**************
     This function is overridden to ensure that the application close the navigation drawer if it
     is open instead of closing the application.
     **************/
    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerVisible(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);

            // If the drawer is not opened, then quit the application when back button is pressed as usual
        else
            super.onBackPressed();
    }

    /**************
     This function is triggered whenever a user clicks one of the item in the
     navigation menu. menuItem indicates the item that was clicked.
     **************/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){

            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).addToBackStack(null).commit();
                break;

            case R.id.nav_search:
                browseHairStylesFragment = new BrowseHairStylesFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        browseHairStylesFragment).addToBackStack(null).commit();
                break;

            case R.id.nav_recommendation:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RecommendedFragment()).addToBackStack(null).commit();
                break;

            case R.id.nav_ar:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ARFragment()).addToBackStack(null).commit();
                break;

            case R.id.nav_face_shape_info:
                Toast.makeText(this, "Implement Face Shape Info Page", Toast.LENGTH_SHORT).show();
                break;

            default:
                Toast.makeText(this, "NEED IMPLEMENTATION", Toast.LENGTH_SHORT).show();
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**************
     This function is triggered whenever a user clicks on the button to login or signup
     **************/

    public void startUpScreenClicked(View view) {
        startActivity(new Intent(HomeActivity.this, StartUpScreen.class));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 9999:
                browseHairStylesFragment.onActivityResult(requestCode, resultCode, data);
                break;

            case AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE:

                if(EasyPermissions.hasPermissions(this, permissions)){
                    Toast.makeText(this, "All permission granted", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        if(!EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(this, "Need permissions for app to function",
                    MULTIPLE_PERMISSIONS, permissions);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        if(EasyPermissions.somePermissionDenied(this, permissions)) {
            new AppSettingsDialog.Builder(this).build().show();
        }

    }
}