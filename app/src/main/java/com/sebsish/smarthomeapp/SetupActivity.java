package com.sebsish.smarthomeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class SetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_activity);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.reg_to_login_2, R.anim.reg_to_login_1);
    }
    public void Exit (View view) {
        finish();
        overridePendingTransition(R.anim.reg_to_login_2, R.anim.reg_to_login_1);
    }
}