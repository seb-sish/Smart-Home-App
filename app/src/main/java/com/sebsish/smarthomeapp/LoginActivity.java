package com.sebsish.smarthomeapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;

    TextInputLayout email;
    TextInputLayout password;
    Button loginBtn;

    String emailText;
    String passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        loginBtn = findViewById(R.id.loginBtn);
        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);

        email.getEditText().addTextChangedListener(generalTextWatcher);
        password.getEditText().addTextChangedListener(generalTextWatcher);
    }

    public void login (View view) {
        emailText = email.getEditText().getText().toString();
        passwordText = password.getEditText().getText().toString();

        mAuth.signInWithEmailAndPassword(emailText, passwordText)
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
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.login_to_reg_1, R.anim.login_to_reg_2);
                                finish();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Неверный логин или пароль!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void toReg (View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.login_to_reg_1, R.anim.login_to_reg_2);
    }
    public void remindPassword (View view) {
        Intent intent = new Intent(this, RecoveryActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.login_to_reg_1, R.anim.login_to_reg_2);
    }

    private TextWatcher generalTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            emailText = email.getEditText().getText().toString();
            passwordText = password.getEditText().getText().toString();
            if (s.toString().equals(emailText)) {
                    if (InputValidator.isValidEmail(emailText)) {
                        email.setError("");}
                    else {
                        email.setError(getString(R.string.email_required)); }
            }
            else if (s.toString().equals(passwordText)) {
                    if (InputValidator.isValidPassword(passwordText)) {
                        password.setError("");}
                    else {
                        password.setError(getString(R.string.easy_password));}
            }
            if (email.getError() == null && password.getError() == null &&
                !TextUtils.isEmpty(emailText) && !TextUtils.isEmpty(passwordText)) {
                loginBtn.setEnabled(true);
                loginBtn.setBackgroundTintList(getColorStateList(R.color.orange));
            }
            else {
                loginBtn.setEnabled(false);
                loginBtn.setBackgroundTintList(InputValidator.getButtonColor(LoginActivity.this));
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {}

    };
}




