package com.sebsish.smarthomeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sebsish.smarthomeapp.dataFormats.User;
import com.sebsish.smarthomeapp.ui.*;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton addButton;
    private BottomNavigationView navView;

    private DatabaseReference myRef;
    private FirebaseUser user;
    private User userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        user = FirebaseAuth.getInstance().getCurrentUser();

        setContentView(R.layout.main_activity);
        addButton = findViewById(R.id.addButton);

        navView = findViewById(R.id.nav_view);
        connectToDB();

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = new EmptyHomeFragment();
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        try {
                            if (!userData.getModules().isEmpty()) { fragment = new HomeFragment(); }}
                        catch (NullPointerException e) {
                            Log.w("Change", e);
                            connectToDB();
                        }
                        break;
                    case R.id.navigation_scenarios:
                        try {
                            if (!userData.getModules().isEmpty()) { fragment = new ScenariosFragment(); }
                                else {fragment = new EmptyHomeFragment();}}
                        catch (NullPointerException e) {
                            Log.w("Change", e);
                            connectToDB();
                        }
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

    public void addElement(View view){
        if (navView.getSelectedItemId() == R.id.navigation_scenarios) {
            Toast.makeText(this, "Недоступно в гостевом режиме.", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(MainActivity.this, ScenariosSetupActivity.class);
//            startActivity(intent);
//            overridePendingTransition(R.anim.login_to_reg_1, R.anim.login_to_reg_2);
        } else {
            Intent intent = new Intent(MainActivity.this, ModulesSetupActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.login_to_reg_1, R.anim.login_to_reg_2);
        }
    }

    public void toHomeControl (View view){
        Intent intent = new Intent(MainActivity.this, HomeControlActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.login_to_reg_1, R.anim.login_to_reg_2);
    }

    public void connectToDB() {
        myRef.child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    if (Objects.requireNonNull(task.getResult()).exists()){
                        userData = task.getResult().getValue(User.class);
                        Fragment defaultFragment = new EmptyHomeFragment();
                        if (!userData.getModules().isEmpty()) { defaultFragment = new HomeFragment(); }
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_fragment, defaultFragment).commit();
                    }
                } else { Toast.makeText(MainActivity.this,"Ошибка при чтении данных! Проверьте подключение к интернету.",Toast.LENGTH_SHORT).show(); }
            }});
    }
}