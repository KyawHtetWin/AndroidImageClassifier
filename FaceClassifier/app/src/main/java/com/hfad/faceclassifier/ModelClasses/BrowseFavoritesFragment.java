package com.hfad.faceclassifier.ModelClasses;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.hfad.faceclassifier.FilterDialog;
import com.hfad.faceclassifier.HelperClasses.FavoriteHairstylesAdapter;
import com.hfad.faceclassifier.HomeActivity;
import com.hfad.faceclassifier.ModelClasses.Hairstyle;
import com.hfad.faceclassifier.HelperClasses.HairstyleImagesAdapter;
import com.hfad.faceclassifier.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BrowseFavoritesFragment extends Fragment {

    private static final int REQUEST_CODE_SPEECH_INPUT = 9999 ;
    private ImageButton filterBtn;
    private RecyclerView browseFavorites_RV;
    private EditText searchBar;

    private ImageButton searchVoiceBtn;

    private ArrayList<Hairstyle> mHairStyles;
    public ArrayList<String> faveHairstyles;

    FavoriteHairstylesAdapter mAdapter;

    // Firebase Components
    private FirebaseStorage storage;
    private DatabaseReference databaseReference;
    private ValueEventListener dbListener;

    private ProgressBar mProgressCircle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate with RecyclerView layout
        View view = inflater.inflate(
                R.layout.fragment_browse_favorites, container, false);

        if (HomeActivity.getFaveList().size() > 0) {
            browseFavorites_RV = view.findViewById(R.id.browse_favorites_RV);
            mHairStyles = new ArrayList<>();
            faveHairstyles = HomeActivity.getFaveList();

            // Pass the data to adapter
            mAdapter = new FavoriteHairstylesAdapter(mHairStyles, faveHairstyles);

            // Assign the adapter to the RV
            browseFavorites_RV.setAdapter(mAdapter);

            // Layout manager arranges views inside RecyclerView
            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
            browseFavorites_RV.setLayoutManager(layoutManager);


            // Firebase initialization
            databaseReference = FirebaseDatabase.getInstance().getReference("HairstyleImages");
            storage = FirebaseStorage.getInstance();

            // Adds Database Listener
            dbListener = databaseReference.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    mHairStyles.clear(); // Clear to prevent loading twice

                    //if (faveHairstyles.size() > 0) {
                        for (DataSnapshot postSnapshot: snapshot.getChildren()) {

                            String faceShape = postSnapshot.child("FaceShape").getValue().toString();
                            String imgURL = postSnapshot.child("ImageURL").getValue().toString();

                            if (faveHairstyles.contains(imgURL)) {
                                String gender = postSnapshot.child("Gender").getValue().toString();

                                Hairstyle hairstyle = new Hairstyle(faceShape,imgURL);
                                hairstyle.setGender(gender);
                                hairstyle.setUniqueKey(postSnapshot.getKey());

                                mHairStyles.add(hairstyle);
                            }

                        }
                   // }
                    // Retrieves hairstyles' data from Firebase Storage
//                    else {
//                        Toast.makeText(getContext(), "You haven't favorited any hairstyles yet!", Toast.LENGTH_SHORT).show();
//                    }

                    // Notify adapter of changes in data
                    mAdapter.notifyDataSetChanged();

                    //mProgressCircle.setVisibility(View.INVISIBLE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    mProgressCircle.setVisibility(View.VISIBLE);
                }
            });
        }

        else {
            Toast.makeText(getContext(), "You haven't favorited any hairstyles yet!", Toast.LENGTH_SHORT).show();
        }

        //mProgressCircle = view.findViewById(R.id.browse_hairstyle_progress_circle);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mProgressCircle.setProgressTintList(ColorStateList.valueOf(Color.CYAN));
//        }



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(dbListener);
    }
}

