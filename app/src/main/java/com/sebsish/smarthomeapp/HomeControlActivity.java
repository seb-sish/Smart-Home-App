package com.sebsish.smarthomeapp;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sebsish.smarthomeapp.dataFormats.User;
import com.sebsish.smarthomeapp.socket.SocketClient;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Objects;

public class HomeControlActivity extends AppCompatActivity {
    private DatabaseReference myRef;
    private FirebaseUser user;

    TextInputLayout connectWifi;
    TextInputLayout connectWifiPass;
    TextInputLayout createWifi;
    TextInputLayout createWifiPass;
    CheckBox checkBox;
    User userData;

    SocketClient client = new SocketClient(this, "192.168.0.100");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        user = FirebaseAuth.getInstance().getCurrentUser();

        setContentView(R.layout.activity_home_control);

        connectWifi = findViewById(R.id.connectWifi);
        connectWifiPass = findViewById(R.id.connectWifiPass);
        createWifi = findViewById(R.id.createWifi);
        createWifiPass = findViewById(R.id.createWifiPass);
        checkBox = findViewById(R.id.checkBox);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    createWifi.setEnabled(true);
                    createWifiPass.setEnabled(true); }
                else {
                    createWifi.setEnabled(false);
                    createWifiPass.setEnabled(false); }
            }});
        Toast.makeText(this, "Подключение...", Toast.LENGTH_SHORT).show();
        client.start();
        myRef.child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    if (Objects.requireNonNull(task.getResult()).exists()){
//                        Toast.makeText(HomeControlActivity.this, "Successfully Read",Toast.LENGTH_SHORT).show();
                        userData = task.getResult().getValue(User.class);
                        Log.d("data", userData.getEmail()+" "+userData.getPassword());
                    } else { Toast.makeText(HomeControlActivity.this,"Отсутствуют данные в базе данных!",Toast.LENGTH_SHORT).show(); }
                } else { Toast.makeText(HomeControlActivity.this,"Ошибка при чтении данных!",Toast.LENGTH_SHORT).show(); }
            }
        });
    }

    public void send(View view) throws IOException {
        String connectWifiText = connectWifi.getEditText().getText().toString();
        String connectWifiPassText = connectWifiPass.getEditText().getText().toString();
        String createWifiText = createWifi.getEditText().getText().toString();
        String createWifiPassText =createWifiPass.getEditText().getText().toString();

        client.send(String.format("{" +
                        "\"firebase\": {\"email\":\"%s\", \"password\":\"%s\"}, " +
                        "\"connected_ssid\": {\"essid\":\"%s\", \"password\":\"%s\"}, " +
                        "\"created_ssid\": {\"essid\":\"%s\",\"password\":\"%s\"}" +
                        "}",
                userData.getEmail(), userData.getPassword(), connectWifiText, connectWifiPassText, createWifiText, createWifiPassText));
        Toast.makeText(this, "Данные успешно записаны! Необходимо перезагрузить хаб.", Toast.LENGTH_LONG).show();
        Exit();
    }

    public void enableAll() {
        connectWifi.setEnabled(true);
        connectWifiPass.setEnabled(true);
        checkBox.setEnabled(true);
        LinearLayout houseControl = findViewById(R.id.houseControl);
        houseControl.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        overridePendingTransition(R.anim.reg_to_login_2, R.anim.reg_to_login_1);
    }
    public void Exit () {
        finish();
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        overridePendingTransition(R.anim.reg_to_login_2, R.anim.reg_to_login_1);
    }
    public void Exit (View view) {
        finish();
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        overridePendingTransition(R.anim.reg_to_login_2, R.anim.reg_to_login_1);
    }
}
