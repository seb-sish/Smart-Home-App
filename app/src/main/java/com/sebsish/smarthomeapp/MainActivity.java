package com.sebsish.smarthomeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.sebsish.smarthomeapp.ui.*;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_fragment, new HomeFragment()).commit();
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.navigation_scenarios:
                        fragment = new ScenariosFragment();
                        break;
                    case R.id.navigation_account:
                        fragment = new AccountFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_fragment, fragment).commit();
                return true;
            }});
    }
    public void accountExit (View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.login_to_reg_1, R.anim.login_to_reg_2);
        finish();
    }

    public void addDevice (View view){
        Intent intent = new Intent(MainActivity.this, SetupActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.login_to_reg_1, R.anim.login_to_reg_2);
    }
}