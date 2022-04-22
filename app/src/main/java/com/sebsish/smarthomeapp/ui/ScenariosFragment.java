package com.sebsish.smarthomeapp.ui;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sebsish.smarthomeapp.MainActivity;
import com.sebsish.smarthomeapp.R;
import com.sebsish.smarthomeapp.dataFormats.Module;
import com.sebsish.smarthomeapp.dataFormats.Scenario;
import com.sebsish.smarthomeapp.dataFormats.User;

import java.text.MessageFormat;
import java.util.Objects;

public class ScenariosFragment extends Fragment {
    private DatabaseReference myRef;
    private FirebaseUser user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        user = FirebaseAuth.getInstance().getCurrentUser();

        View view = inflater.inflate(R.layout.fragment_scenarios, container, false);
        GridLayout layout = view.findViewById(R.id.layout);

        TypedArray ta = getContext().getTheme().obtainStyledAttributes(R.styleable.ViewStyle);

        myRef.child(user.getUid()).child("scenarios").orderByChild("position").addChildEventListener(new ChildEventListener() {
            final String TAG = "database";

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded: " + dataSnapshot.getKey() + "   " + dataSnapshot.getValue());
                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.width = 0;
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                lp.setMargins(16, 16, 16, 16);

                Scenario scenario = dataSnapshot.getValue(Scenario.class);

                View scenarioCard = inflater.inflate(R.layout.scenario_card_layout, null);
                scenarioCard.setTag(dataSnapshot.getKey());

                TextView action = scenarioCard.findViewById(R.id.action);
                if (scenario.getAction()){action.setText("Включить");}
                else {action.setText("Выключить");}
                TextView device = scenarioCard.findViewById(R.id.device);
                myRef.child(user.getUid()).child("modules").child(scenario.getSocket_module()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()){
                            if (Objects.requireNonNull(task.getResult()).exists()){
                                Module module = task.getResult().getValue(Module.class);
                                device.setText(module.getName());
                            }
                        } else { Toast.makeText(view.getContext(),"Ошибка при чтении данных! Проверьте подключение к интернету.",Toast.LENGTH_SHORT).show(); }
                    }});
                TextView if_text = scenarioCard.findViewById(R.id.if_text);
                if (scenario.getIndicator().equals("humidity")) { if_text.setText("если влажность");}
                else { if_text.setText("если температура");}
                TextView condition = scenarioCard.findViewById(R.id.condition);
                condition.setText(MessageFormat.format("{0} {1}", scenario.getCondition(), scenario.getThreshold_temperature()));
                SwitchCompat enabled = scenarioCard.findViewById(R.id.switch_ena);
                enabled.setChecked(scenario.getEnabled());
                enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        String tag = scenarioCard.getTag().toString();
                        myRef.child(user.getUid()).child("scenarios").child(tag).child("enabled").setValue(isChecked);
                            }
                });
                layout.addView(scenarioCard, lp);
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged: " + dataSnapshot.getKey());
                View scenarioCard = layout.findViewWithTag(dataSnapshot.getKey());
                Scenario scenario = dataSnapshot.getValue(Scenario.class);

                TextView action = scenarioCard.findViewById(R.id.action);
                Boolean act = action.getText() != "Выключить";
                if (act != scenario.getAction()){
                    action.setText(scenario.getAction() ? "Включить" : "Выключить");
                }
                TextView if_text = scenarioCard.findViewById(R.id.if_text);
                if (!if_text.getText().equals(scenario.getIndicator())) {
                    if (scenario.getIndicator().equals("humidity")) { if_text.setText("если влажность");}
                    else { if_text.setText("если температура");}
                }
//                TextView device = scenarioCard.findViewById(R.id.device);
                TextView condition = scenarioCard.findViewById(R.id.condition);
                if (condition.getText() != MessageFormat.format("{0} {1}", scenario.getCondition(), scenario.getThreshold_temperature())){
                    condition.setText(MessageFormat.format("{0} {1}", scenario.getCondition(), scenario.getThreshold_temperature())); }
                SwitchCompat enabled = scenarioCard.findViewById(R.id.switch_ena);
                if (enabled.isChecked() != scenario.getEnabled()){
                    enabled.setChecked(scenario.getEnabled());
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

}