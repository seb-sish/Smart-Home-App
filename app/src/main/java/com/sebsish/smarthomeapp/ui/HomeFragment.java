package com.sebsish.smarthomeapp.ui;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sebsish.smarthomeapp.R;
import com.sebsish.smarthomeapp.dataFormats.Module;
import com.sebsish.smarthomeapp.dataFormats.User;

import java.text.MessageFormat;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private DatabaseReference myRef;
    private FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        user = FirebaseAuth.getInstance().getCurrentUser();

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        GridLayout layout = view.findViewById(R.id.layout);

        TypedArray ta = getContext().getTheme().obtainStyledAttributes(R.styleable.ViewStyle);

        myRef.child(user.getUid()).child("modules").orderByChild("position").addChildEventListener(new ChildEventListener() {
            String TAG = "database";

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded: " + dataSnapshot.getKey() + "   " + dataSnapshot.getValue());
                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.width = 0;
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                lp.setMargins(16, 16, 16, 16);

                Module module = dataSnapshot.getValue(Module.class);

                View moduleCard = inflater.inflate(R.layout.module_card_layout, null);
                moduleCard.setTag(dataSnapshot.getKey());
                TextView text = moduleCard.findViewById(R.id.module_name);
                text.setText(module.getName());
                TextView room = moduleCard.findViewById(R.id.module_room);
                room.setText(module.getRoom());

                AppCompatImageView icon = moduleCard.findViewById(R.id.module_icon);

                switch (module.getType()) {
                    case "socket_module":
                        moduleCard.findViewById(R.id.module_temp).setVisibility(View.INVISIBLE);
                        moduleCard.findViewById(R.id.module_temp_desc).setVisibility(View.INVISIBLE);
                        moduleCard.findViewById(R.id.module_hum).setVisibility(View.INVISIBLE);
                        moduleCard.findViewById(R.id.module_hum_desc).setVisibility(View.INVISIBLE);

                        icon.setBackgroundResource(R.drawable.device_socket_f);
                        ImageButton btn = moduleCard.findViewById(R.id.powerBtn);
                        if (module.getEnabled()) {
                            btn.setBackgroundTintList(ColorStateList.valueOf(Color.CYAN));
                        }

                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String moduleId = moduleCard.getTag().toString();
                                myRef.child(user.getUid()).child("modules").child(moduleId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Module module = task.getResult().getValue(Module.class);
                                            myRef.child(user.getUid()).child("modules").child(moduleId).child("enabled").setValue(!module.getEnabled());
                                        } else {
                                            Log.e("firebase", "Error getting data", task.getException());
                                        }
                                    }
                                });
                            }
                        });
                        break;
                    case "temperature_module":
                        moduleCard.findViewById(R.id.powerBtn).setVisibility(View.INVISIBLE);
                        icon.setBackgroundResource(R.drawable.device_temperature);

                        TextView temp = moduleCard.findViewById(R.id.module_temp);
                        TextView hum = moduleCard.findViewById(R.id.module_hum);

                        temp.setTextColor(getTextColor("temp", module.getTemperature()));
                        hum.setTextColor(getTextColor("hum", module.getHumidity()));

                        temp.setText(MessageFormat.format("{0}°C", String.valueOf(module.getTemperature())));
                        hum.setText(MessageFormat.format("{0}%", String.valueOf(module.getHumidity())));
                        break;
                }

                layout.addView(moduleCard, lp);
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged: " + dataSnapshot.getKey());
                View moduleCard = layout.findViewWithTag(dataSnapshot.getKey());
                Module module = dataSnapshot.getValue(Module.class);

                TextView name = moduleCard.findViewById(R.id.module_name);
                if (module.getName() != name.getText()) {
                    name.setText(module.getName());
                }
                TextView room = moduleCard.findViewById(R.id.module_room);
                if (module.getRoom() != room.getText()) {
                    room.setText(module.getRoom());
                }

                switch (module.getType()) {
                    case "socket_module":
                        ValueAnimator animator;
                        ImageButton btn = moduleCard.findViewById(R.id.powerBtn);
                        int btnColor = ta.getColor(R.styleable.ViewStyle_cardBackgroundColor, Color.BLACK);

                        if (module.getEnabled()) {
                            animator = ValueAnimator.ofInt(btnColor, Color.CYAN);
                        } else {
                            animator = ValueAnimator.ofInt(Color.CYAN, btnColor);
                        }

                        animator.setDuration(1000L);
                        animator.setEvaluator(new ArgbEvaluator());
                        animator.setInterpolator(new DecelerateInterpolator(2));
                        animator.addUpdateListener(new ObjectAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int animatedValue = (int) animation.getAnimatedValue();
                                btn.setBackgroundTintList(ColorStateList.valueOf(animatedValue));
                            }
                        });
                        animator.start();
                        Log.d("btn", "click");
                        break;
                    case "temperature_module":
                        TextView temp = moduleCard.findViewById(R.id.module_temp);
                        TextView hum = moduleCard.findViewById(R.id.module_hum);
                        temp.setTextColor(getTextColor("temp", module.getTemperature()));
                        hum.setTextColor(getTextColor("hum", module.getHumidity()));

                        temp.setText(MessageFormat.format("{0}°C", String.valueOf(module.getTemperature())));
                        hum.setText(MessageFormat.format("{0}%", String.valueOf(module.getHumidity())));
                        break;
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
                layout.removeView(view.findViewWithTag(dataSnapshot.getKey()));
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(view.getContext(), "Произошла ошибка при загрузке! Проверьте соединение с интернетом.", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    public int getTextColor(String type, int value) {
        if (type.equals("temp")) {
            if (value > 25) { return Color.RED; }
            else if (value >= 22) { return Color.YELLOW; }
            else if (value >= 17) { return Color.GREEN; }
            else { return Color.BLUE; }

        } else if (type.equals("hum")) {
            if (value > 65) { return Color.YELLOW; }
            else if (value >= 40) { return Color.GREEN; }
            else { return Color.RED; }
        }
        return Color.GRAY;
        }
    }
