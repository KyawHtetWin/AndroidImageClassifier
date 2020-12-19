package com.hfad.faceclassifier;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.hfad.faceclassifier.ModelClasses.Hairstyle;
import com.hfad.faceclassifier.HelperClasses.RecommendedHairStyleAdapter;
import com.hfad.faceclassifier.ModelClasses.UserHelper;

import java.util.ArrayList;


public class RecommendedFragment extends Fragment {

    private RecyclerView recommendedHairStyle_RV;
    private ArrayList<Hairstyle> mHairStyles;
    RecommendedHairStyleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // Firebase Components
    private FirebaseStorage storage;
    private DatabaseReference databaseReference;
    private ValueEventListener dbListener;

    private ProgressBar mProgressCircle;

    // Current User to provides recommendation
    private UserHelper currentUser;

    public void setCurrentUser(UserHelper currentUser) {this.currentUser = currentUser; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recommended, container, false);

        mProgressCircle = view.findViewById(R.id.recommended_frag_progress_circle);

        //createHairStyleList();
        mHairStyles = new ArrayList<>();

        // Initialize and set up RecyclerView
        recommendedHairStyle_RV = view.findViewById(R.id.rec_rv);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recommendedHairStyle_RV.setLayoutManager(mLayoutManager);
        mAdapter = new RecommendedHairStyleAdapter(mHairStyles);
        recommendedHairStyle_RV.setAdapter(mAdapter);

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

                    // Face Shape in database image
                    String faceShape = postSnapshot.child("FaceShape").getValue().toString();
                    // Gender in database image
                    String gender = postSnapshot.child("Gender").getValue().toString();

                    Hairstyle hairstyle = new Hairstyle(faceShape, postSnapshot.child("ImageURL").getValue().toString());
                    hairstyle.setGender(gender);
                    hairstyle.setUniqueKey(postSnapshot.getKey());

                    // Provides recommendation based on face shape and gender
                    if(currentUser != null) {
                        if(currentUser.getFaceShape().equals(hairstyle.getFaceshape()) &&
                                currentUser.getGender().equals(hairstyle.getGender())){
                            mHairStyles.add(hairstyle);
                        }

                    }
                }

                for (int i=0; i < mHairStyles.size(); i++){
                    mHairStyles.get(i).setFavoriteIconResourceId(R.drawable.favorite_icon);
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



        mAdapter.setOnItemClickListener(new RecommendedHairStyleAdapter.OnItemClickListener() {

           @Override
           public void onFavoriteClick(int position, ImageView favoriteIconImg) {
               if(!mHairStyles.get(position).getIsFavorite()) {
                   //Toast.makeText(getContext(), "Setting Favorite to true" , Toast.LENGTH_LONG).show();
                   // Set favorite
                   mHairStyles.get(position).setFavorite(true);
                   mHairStyles.get(position).setFavoriteIconResourceId(R.drawable.favorite_icon_pressed);
               }

               else{
                   //Toast.makeText(getContext(), "Setting Favorite to false" , Toast.LENGTH_LONG).show();
                   // Reset Favorite
                   mHairStyles.get(position).setFavorite(false);
                   mHairStyles.get(position).setFavoriteIconResourceId(R.drawable.favorite_icon);
               }

               // Notify the change of data
               mAdapter.notifyDataSetChanged();
           }


       });

        // Set up and attach the touch helper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recommendedHairStyle_RV);

        return view;
    }


    /*****
     * Allows user to swipe left or right to remove a hairstyle if they don't like it
     *****/
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.
            SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull
                RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            mHairStyles.remove(position);
            mAdapter.notifyDataSetChanged();

        }
    };

}