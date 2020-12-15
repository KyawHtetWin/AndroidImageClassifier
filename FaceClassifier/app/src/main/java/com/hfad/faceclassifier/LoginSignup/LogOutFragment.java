package com.hfad.faceclassifier.LoginSignup;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.hfad.faceclassifier.LoginSignup.Login;

import com.hfad.faceclassifier.R;

public class LogOutFragment extends Fragment {

    FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_out, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.signOut();
        Toast.makeText(getContext(), "Logging out", Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setClass(getActivity(), Login.class);
        getActivity().startActivity(intent);
    }

    public void setFirebaseAuth(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }
}