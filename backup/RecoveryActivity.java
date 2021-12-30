package com.sebsish.smarthomeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class RecoveryActivity extends AppCompatActivity {

    TextInputLayout email;
    String emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recovery_activity);

        email = findViewById(R.id.regEmail);
        email.getEditText().addTextChangedListener(generalTextWatcher);
    }

    public void recovery(View view) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        emailText =
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RecoveryActivity.this, "На почту отправлена ссылка для восстановления!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(RecoveryActivity.this, "Аккаунта с таким E-mail адресом не существует!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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