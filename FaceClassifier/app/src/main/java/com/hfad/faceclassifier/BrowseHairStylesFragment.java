package com.hfad.faceclassifier;

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
import com.hfad.faceclassifier.ModelClasses.Hairstyle;
import com.hfad.faceclassifier.HelperClasses.HairstyleImagesAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BrowseHairStylesFragment extends Fragment implements FilterDialog.FilterDialogListener {

    private static final int REQUEST_CODE_SPEECH_INPUT = 9999 ;
    private ImageButton filterBtn;
    private RecyclerView browseHairStyle_RV;
    private EditText searchBar;
    
    private ImageButton searchVoiceBtn;

    private ArrayList<Hairstyle> mHairStyles;

    HairstyleImagesAdapter mAdapter;

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
                R.layout.fragment_browse_hairstyles, container, false);

        mProgressCircle = view.findViewById(R.id.browse_hairstyle_progress_circle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mProgressCircle.setProgressTintList(ColorStateList.valueOf(Color.CYAN));
        }


        browseHairStyle_RV = view.findViewById(R.id.hairstyle_collection_rv);
        mHairStyles = new ArrayList<>();

        // Pass the data to adapter
        mAdapter = new HairstyleImagesAdapter(mHairStyles);

        // Assign the adapter to the RV
        browseHairStyle_RV.setAdapter(mAdapter);

        // Layout manager arranges views inside RecyclerView
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        browseHairStyle_RV.setLayoutManager(layoutManager);


        // Firebase initialization
        databaseReference = FirebaseDatabase.getInstance().getReference("HairstyleImages");
        storage = FirebaseStorage.getInstance();

        // Adds Database Listener
        dbListener = databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                mHairStyles.clear(); // Clear to prevent loading twice

                // Retrieves hairstyles' data from Firebase Storage
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {

                    String faceShape = postSnapshot.child("FaceShape").getValue().toString();
                    String imgURL = postSnapshot.child("ImageURL").getValue().toString();
                    String gender = postSnapshot.child("Gender").getValue().toString();

                    Hairstyle hairstyle = new Hairstyle(faceShape,imgURL);
                    hairstyle.setGender(gender);
                    hairstyle.setUniqueKey(postSnapshot.getKey());

                    mHairStyles.add(hairstyle);
                }

                // Notify adapter of changes in data
                mAdapter.notifyDataSetChanged();

                mProgressCircle.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.VISIBLE);
            }
        });


        // Filter
        filterBtn = view.findViewById(R.id.filter_btn);
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterDialog filterDialog = new FilterDialog();
                filterDialog.setTargetFragment(BrowseHairStylesFragment.this, 1000);
                filterDialog.show(getActivity().getSupportFragmentManager(), "Filter Dialog");

            }
        });

        // Voice Search
        searchVoiceBtn = view.findViewById(R.id.search_voice_btn);
        searchVoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceSearch();
            }
        });
        
        
        // Search Bar Search
        searchBar = view.findViewById(R.id.search_input);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    /****
     *  Starts Google's Voice Recognition
     */
    private void voiceSearch() {
        // Intent to show speech to text dialog
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi, say face shape");

        // Start Intent
        try{

            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);

        } catch (Exception e) {
            // If there was some error
            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Search RecyclerView based on Voice Search
        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT:
                if(resultCode == getActivity().RESULT_OK && null != data){
                    // Get text array from voice intent
                    ArrayList<String> result = data.getStringArrayListExtra
                            (RecognizerIntent.EXTRA_RESULTS);
                    searchBar.setText(result.get(0));
                }

                break;
        }
    }



    /*****
     * Filter the RecyclerView based on the search term (Search Logic)
     * @param searchTerm
     */
    private void filter(String searchTerm) {
        searchTerm = searchTerm.toLowerCase();

        List<String> searchTerms = Arrays.asList(searchTerm.split(" "));

        ArrayList<Hairstyle> filteredList = new ArrayList<>();

        for(Hairstyle hairstyle: mHairStyles){
            String face_shape = hairstyle.getFaceshape().toLowerCase();
            String gender = hairstyle.getGender().toLowerCase();

            if(searchTerms.contains(face_shape) && searchTerms.contains(gender))
                filteredList.add(hairstyle);

            else if(searchTerm.equals(face_shape))
                filteredList.add(hairstyle);

            else if(searchTerm.equals(gender))
                filteredList.add(hairstyle);
        }

        mAdapter.filtered(filteredList);
    }

    /******
     *
     * Filter the RecyclerView based on the selected face shape and gender in filter
     * @param selectedFaceShape
     * @param selectedGender
     */

    @Override
    public void applyFilter(String selectedFaceShape, String selectedGender) {

        String searchTerm = "";

        if (selectedFaceShape != null && !selectedFaceShape.equals("NONE")) {
            searchTerm += selectedFaceShape;
        }

        if (selectedGender != null && !selectedGender.equals("NONE")) {
            if(searchTerm.isEmpty()) {
                searchTerm += selectedGender;
            }

            else {
                searchTerm += " " + selectedGender;
            }
        }

        searchBar.setText(searchTerm);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(dbListener);
    }
}

