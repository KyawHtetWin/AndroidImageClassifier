package com.hfad.faceclassifier;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hfad.faceclassifier.ModelClasses.BrowseFavoritesFragment;
import com.google.firebase.firestore.*;
import com.hfad.faceclassifier.ModelClasses.UserHelper;

import java.text.BreakIterator;
import java.util.concurrent.Executor;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfileOptions#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileOptions extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private UserHelper currentUser;
    private View profileView;
    private UserProfile profile;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;

    TextView faceShapeTxt;
    TextView emailText;

    String userId;
    String currentUserName;
    String currentFaceShape;
    String currentUserEmail;

    private BrowseFavoritesFragment browseFavesFragment;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserProfileOptions() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfileOptions.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfileOptions newInstance(String param1, String param2) {
        UserProfileOptions fragment = new UserProfileOptions();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        currentUser = new UserHelper();

        // Retrieves the user id of the current user
        userId = fAuth.getCurrentUser().getUid();

        getUserInfo();

//        faceShapeTxt.setText("Face Shape : " + currentUser.getFaceShape());
//        emailText.setText("Email: " + currentUser.getEmail());

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_profile_options, container, false);

        currentUser = new UserHelper();
        faceShapeTxt = v.findViewById(R.id.faceShapeTxt);
        emailText = v.findViewById(R.id.emailTxt);
        getUserInfo();

        faceShapeTxt.setText("Face Shape: " + currentUser.getFaceShape());
        emailText.setText("Email: " + currentUser.getEmail());


        return v;

    }

//    public void onClick(View v) {
//        viewFavesButton = this.getView().findViewById(R.id.favoriteButton);
//    }


    private void getUserInfo() {

        DocumentReference documentReference = fStore.collection("users").document(userId);
        if (documentReference != null) {
//            Toast.makeText(getContext(), "document reference not null", Toast.LENGTH_SHORT).show();
            documentReference.addSnapshotListener(this.getActivity(), new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot,
                                    @javax.annotation.Nullable FirebaseFirestoreException e) {
                    Log.d("********** HOMEACTIVITY" , "Firebase exception", e);
                    if(documentSnapshot.exists()){
                        //Toast.makeText(getContext(), "documentSnapshot exists", Toast.LENGTH_SHORT).show();
                        currentUser.setFullName(documentSnapshot.getString("fullname"));
                        currentUser.setEmail(documentSnapshot.getString("email"));
                        currentUser.setFaceShape(documentSnapshot.getString("faceshape"));
                        currentUserEmail = documentSnapshot.getString("email");
                        currentUserName = documentSnapshot.getString("fullname");
                        currentFaceShape = documentSnapshot.getString("faceshape");
                        faceShapeTxt.setText("Face Shape: " + currentUser.getFaceShape());
                        emailText.setText("Email: " + currentUser.getEmail());
                    }

                    else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }
            });

        }
    }



}