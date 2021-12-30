package com.sebsish.smarthomeapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.sebsish.smarthomeapp.R;

public class AccountFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        TextView username = view.findViewById(R.id.accountUsername);
        username.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        return view;
    }
}