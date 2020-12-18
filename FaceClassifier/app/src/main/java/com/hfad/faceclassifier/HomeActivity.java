package com.hfad.faceclassifier;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hfad.faceclassifier.LoginSignup.LogOutFragment;
import com.hfad.faceclassifier.ModelClasses.BrowseFavoritesFragment;
import com.hfad.faceclassifier.ModelClasses.UserHelper;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class HomeActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener,
        EasyPermissions.PermissionCallbacks, HomeFragment.HomeFragmentListener {

    private static final int MULTIPLE_PERMISSIONS = 7777;

    private static final float END_SCALE = 0.7f;
    private static ArrayList<String> faveHairstyles;

//    public ArrayList<String> faveHairstyles;

    /************** UI Components **************/
    // Drawer Menu
    DrawerLayout drawerLayout;
    // Navigation View
    NavigationView navigationView;
    // Menu Icon
    ImageView menuIcon;

    // Views in Navigation menu header
    View navMenuHeader;

    // Navigation Drawer Items
    TextView navUserName, navEmail;

    // Main Layout
    LinearLayout contentView;

    // Browse HairStyle page
    BrowseHairStylesFragment browseHairStylesFragment;

    // User Profile page
    UserProfile userProfileFragment;

    // Recommendation page
    RecommendedFragment recommendedFragment;

    // Firebase
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;

    // Required Permissions to ask users
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO};

    // Represents the current User
    public UserHelper currentUser;

    // User id of the current user
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        faveHairstyles = new ArrayList<String>();

        // Initialize UI Components
        drawerLayout   = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content);

        navMenuHeader = navigationView.getHeaderView(0);
        navUserName = navMenuHeader.findViewById(R.id.nav_user_name);
        navEmail = navMenuHeader.findViewById(R.id.nav_drawer_email);

        // Initializes Firebase objects
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        currentUser = new UserHelper();

        // Retrieves the user id of the current user
        userId = fAuth.getCurrentUser().getUid();

        // Add navigation drawer
        navigationDrawer();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();

        }

        // Gets user information from Cloud Firestore
        getUserInfo();
    }


    public static ArrayList<String> getFaveList() {
        return faveHairstyles;
    }

    /**************
     This function retrieves information about the user from Cloud Firestore
     **************/
    private void getUserInfo() {

        DocumentReference documentReference = fStore.collection("users").document(userId);
        if (documentReference != null) {
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot,
                                    @javax.annotation.Nullable FirebaseFirestoreException e) {
                    Log.d("********** HOMEACTIVITY" , "Firebase exception", e);
                    if(documentSnapshot.exists()){
                        navUserName.setText(documentSnapshot.getString("fullname"));
                        currentUser.setFullName(documentSnapshot.getString("fullname"));
                        navEmail.setText(documentSnapshot.getString("email"));
                        currentUser.setEmail(documentSnapshot.getString("email"));
                        // Retrieves gender information
                        currentUser.setGender(documentSnapshot.getString("gender"));
                        // Means user have his face shape detected before
                        if(!documentSnapshot.getString("faceshape").equals("none"))
                            currentUser.setFaceShape(documentSnapshot.getString("faceshape"));
                    }

                    else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }
            });

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


//    public static UserHelper getCurrentUserHelper() {
//        UserHelper user = new UserHelper();
//        user = currentUser;
//    }

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
                //Toast.makeText(this, "Current User name: " + currentUser.getFullName(), Toast.LENGTH_SHORT).show();
                if(currentUser.getFaceShape() == null) {
                    Toast.makeText(this, "You must detect your face shape before getting a recommendation", Toast.LENGTH_SHORT).show();
                }

                else {
                    recommendedFragment = new RecommendedFragment();
                    recommendedFragment.setCurrentUser(currentUser);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            recommendedFragment).addToBackStack(null).commit();
                }
                break;

            case R.id.nav_ar:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ARFragment()).addToBackStack(null).commit();
                break;

            case R.id.nav_view_favorites:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new BrowseFavoritesFragment()).addToBackStack(null).commit();
                break;

            case R.id.nav_face_shape:
                //Toast.makeText(this, "Implement Face Shape Info Page", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FaceShapeInfoFragment()).addToBackStack(null).commit();
                break;

            case R.id.nav_logout:
                logOutPressed();
                break;

            case R.id.nav_profile:
                //Toast.makeText(this, "Profile pressed", Toast.LENGTH_SHORT).show();
                userProfileFragment = new UserProfile();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        userProfileFragment).addToBackStack(null).commit();
                break;

            case R.id.nav_about_us:
                aboutPressed();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Allows the user to log out of the app
    public void logOutPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this,
                android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);
        builder.setMessage("Are you sure you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LogOutFragment logOutFragment = new LogOutFragment();
                        logOutFragment.setFirebaseAuth(fAuth);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                logOutFragment).addToBackStack(null).commit();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }


    // Show the information on the version of our application
    public void aboutPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this,
                android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);
        builder.setTitle("About");
        builder.setMessage("Haircut For Your Face\nVersion 1.0\nDate: Dec 12th, 2020");
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
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
                    Toast.makeText(this, "All permissions granted", Toast.LENGTH_LONG).show();
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

    // The argument of the this method, faceShape, represents the face shape of user
    // that has been detected by machine learning model. Update the face shape in user
    // profile on Cloud Firestore
    @Override
    public void userFaceShape(String faceShape) {
        // Update the user face shape on Cloud Firestore
        fStore.collection("users").document(userId).update("faceshape", faceShape);
        // Update the current user information with the detected faceShape
        currentUser.setFaceShape(faceShape);
    }
}