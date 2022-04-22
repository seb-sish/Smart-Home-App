package com.sebsish.smarthomeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RecoveryActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;

    TextInputLayout email;
    String emailText;

    Button recoveryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recovery_activity);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        recoveryBtn = findViewById(R.id.recoveryBtn);

        email = findViewById(R.id.recoveryEmail);
        email.getEditText().addTextChangedListener(generalTextWatcher);
    }

    public void recovery(View view) {
        emailText = email.getEditText().getText().toString();
        mAuth.sendPasswordResetEmail(emailText)
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
    public void loginToGuest (View view) {
        mAuth.signInWithEmailAndPassword("llki11er228ll@gmail.com", "4381109075ViTA")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("login", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Param.METHOD, "авторизация");

                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
                                Intent intent = new Intent(RecoveryActivity.this, MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.login_to_reg_1, R.anim.login_to_reg_2);
                                finish();
                            }
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