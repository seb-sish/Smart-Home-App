package com.sebsish.smarthomeapp;
import com.sebsish.smarthomeapp.modules.*;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ModulesSetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modules_setup_activity);
    }

    public void socketConnect(View view) {
        Intent intent = new Intent(this, SocketModule.class);
        startActivity(intent);
        overridePendingTransition(R.anim.login_to_reg_1, R.anim.login_to_reg_2);
    }
    public void lightConnect(View view) {
        Toast.makeText(ModulesSetupActivity.this,"В разработке...",Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, SocketModule.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.login_to_reg_1, R.anim.login_to_reg_2);
    }
    public void switcherConnect(View view) {
        Toast.makeText(ModulesSetupActivity.this,"В разработке...",Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, SocketModule.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.login_to_reg_1, R.anim.login_to_reg_2);
    }
    public void tempModuleConnect(View view) {
        Intent intent = new Intent(this, TempModule.class);
        startActivity(intent);
        overridePendingTransition(R.anim.login_to_reg_1, R.anim.login_to_reg_2);
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