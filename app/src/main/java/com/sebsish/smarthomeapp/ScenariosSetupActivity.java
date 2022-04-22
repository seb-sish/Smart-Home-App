package com.sebsish.smarthomeapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ListAdapter;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sebsish.smarthomeapp.dataFormats.Module;
import com.sebsish.smarthomeapp.dataFormats.User;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ScenariosSetupActivity extends AppCompatActivity {
    private DatabaseReference myRef;
    private FirebaseUser user;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scenarios_setup_activity);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        String[] dayOfWeek = new String[]{"ssd", "sdsf"};
        Spinner spinnerTemp = findViewById(R.id.spinner_temp);
        myRef.child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().getValue(User.class);
                    for(Map.Entry<String, Object> entry : user.getModules().entrySet()) {
                        for(Field f : entry.getValue().getClass().getFields()){
                            try {
                                Object m = f.get(Module.class);
                                Log.d("tt", f.getName()+" "+m.toString());
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }


                        }

//                    ArrayAdapter<?> adapterTemp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dayOfWeek);
//                    adapterTemp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinnerTemp.setAdapter(adapterTemp);
                } else {
                    Log.e("firebase", "Error getting data", task.getException());
                }
            }
        });


        Spinner spinnerThreshold = findViewById(R.id.spinner_threshold);
        Integer[] int_numbers = new Integer[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,
                33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,
                73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100};
        ArrayAdapter<?> adapterThreshold = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, int_numbers);
        adapterThreshold.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerThreshold.setAdapter(adapterThreshold);
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