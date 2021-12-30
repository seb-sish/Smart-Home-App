package com.sebsish.smarthomeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class RecoveryActivity extends AppCompatActivity {

    TextInputLayout email;
    String emailText;

    Button recoveryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recovery_activity);

        recoveryBtn = findViewById(R.id.recoveryBtn);

        email = findViewById(R.id.recoveryEmail);
        email.getEditText().addTextChangedListener(generalTextWatcher);
    }

    public void recovery(View view) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        emailText = email.getEditText().getText().toString();
        auth.sendPasswordResetEmail(emailText)
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
    private TextWatcher generalTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            emailText = email.getEditText().getText().toString();

            if (s.toString().equals(emailText)) {
                if (InputValidator.isValidEmail(emailText)) {
                    email.setError("");}
                else {
                    email.setError(getString(R.string.email_required)); }
            }

            if (email.getError() == null && !TextUtils.isEmpty(emailText)) {
                recoveryBtn.setEnabled(true);
                recoveryBtn.setBackgroundTintList(getColorStateList(R.color.orange));
            }
            else {
                recoveryBtn.setEnabled(false);
                recoveryBtn.setBackgroundTintList(InputValidator.getButtonColor(RecoveryActivity.this));
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {}

    };

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