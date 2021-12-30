package com.sebsish.smarthomeapp;

import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    TextInputLayout username;
    TextInputLayout email;
    TextInputLayout password;
    TextInputLayout repeatPassword;

    String usernameText;
    String emailText;
    String passwordText;
    String repeatPasswordText;

    Button regBtn;
    ColorStateList buttonColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        mAuth = FirebaseAuth.getInstance();

        regBtn = findViewById(R.id.regBtn);
        username = findViewById(R.id.regUsername);
        email = findViewById(R.id.regEmail);
        password = findViewById(R.id.regPassword);
        repeatPassword = findViewById(R.id.repeatRegPassword);

        username.getEditText().addTextChangedListener(generalTextWatcher);
        email.getEditText().addTextChangedListener(generalTextWatcher);
        password.getEditText().addTextChangedListener(generalTextWatcher);
        repeatPassword.getEditText().addTextChangedListener(generalTextWatcher);

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                buttonColor = getColorStateList(R.color.darker_orange);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                buttonColor = getColorStateList(R.color.darker_orange_2);
                break;
        }
    }
    public void register(View view) {
        emailText = email.getEditText().getText().toString();
        passwordText = password.getEditText().getText().toString();

        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("reg", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("reg", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private final TextWatcher generalTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            usernameText = username.getEditText().getText().toString();
            emailText = email.getEditText().getText().toString();
            passwordText = password.getEditText().getText().toString();
            repeatPasswordText = repeatPassword.getEditText().getText().toString();

            if (s.toString().equals(usernameText)) {
                if (InputValidator.isValidUsername(usernameText)) { username.setError("");}
                else { username.setError(getString(R.string.username_required)); }
            }
            else if (s.toString().equals(emailText)) {
                if (InputValidator.isValidEmail(emailText)) { email.setError(""); }
                else { email.setError(getString(R.string.email_required)); }
            }
            if (s.toString().equals(passwordText)) {
                if (InputValidator.isValidPassword(passwordText)) { password.setError("");}
                else { password.setError(getString(R.string.easy_password));}
                if (passwordText.equals(repeatPasswordText)) { repeatPassword.setError("");}
                else repeatPassword.setError(getString(R.string.unmatch_password));
            }
            if (s.toString().equals(repeatPasswordText)) {
                if (passwordText.equals(repeatPasswordText)) { repeatPassword.setError("");}
                else repeatPassword.setError(getString(R.string.unmatch_password));}

            if (username.getError() == null && email.getError() == null &&
                password.getError() == null && repeatPassword.getError() == null &&
                !TextUtils.isEmpty(usernameText) && !TextUtils.isEmpty(emailText) &&
                !TextUtils.isEmpty(passwordText) && !TextUtils.isEmpty(repeatPasswordText) &&
                    passwordText.equals(repeatPasswordText)) {
                regBtn.setEnabled(true);
                regBtn.setBackgroundTintList(getColorStateList(R.color.orange));
            }
            else {
                findViewById(R.id.regBtn).setEnabled(false);
                regBtn.setBackgroundTintList(buttonColor);
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