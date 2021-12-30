package com.sebsish.smarthomeapp;

import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

public class InputValidator extends AppCompatActivity {
    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {return false;}
        else {return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();}}

    public static boolean isValidPassword(CharSequence password) {
        if (TextUtils.isEmpty(password)) {return false;}
        else { return Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,20}$").matcher(password).matches(); }}

    public static boolean isValidUsername(CharSequence password) {
        if (TextUtils.isEmpty(password)) {return false;}
        else { return Pattern.compile("^(?=.{4,20}$)(?![_.0-9])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$").matcher(password).matches(); }}


}
