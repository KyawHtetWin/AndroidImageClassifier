package com.hfad.faceclassifier;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hfad.faceclassifier.ModelClasses.UserHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;

    TextView userNameText;

    String userId;
    private UserHelper currentUser;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfile newInstance(String param1, String param2) {
        UserProfile fragment = new UserProfile();
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


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);

        currentUser = new UserHelper();
        userNameText = v.findViewById(R.id.userNameText);
        getUserInfo();

        userNameText.setText(currentUser.getFullName());


        return v;
    }

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
                        userNameText.setText(currentUser.getFullName());
                    }

                    else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }
            });

        }
    }

}