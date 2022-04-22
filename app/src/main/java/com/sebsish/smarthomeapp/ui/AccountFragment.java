package com.sebsish.smarthomeapp.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.sebsish.smarthomeapp.R;
import com.sebsish.smarthomeapp.SplashScreen;

public class AccountFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String TAG = "theme";
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        TextView username = view.findViewById(R.id.accountUsername);
        username.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        SwitchCompat themeSwitch = view.findViewById(R.id.themeSwitch);

        SharedPreferences settings = getContext().getSharedPreferences("Settings", 0);
        if (settings.getBoolean("darkMode", true)) {
            themeSwitch.setChecked(true); }
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    settings.edit().putBoolean("darkMode", true).apply();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    settings.edit().putBoolean("darkMode", false).apply();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
//                        Intent mStartActivity = new Intent(getContext(), SplashScreen.class);
//                        PendingIntent mPendingIntent = PendingIntent.getActivity(getContext(), 1, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
//                        AlarmManager mgr = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
//                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//                        System.exit(0);
            }
        });

        return view;
        }
    }