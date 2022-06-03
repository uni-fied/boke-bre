package com.septi.mkk;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.septi.mkk.function.Functions;
import com.septi.mkk.library.TinyDB;
import com.septi.mkk.preferences.PreferencesLogin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SplashActivity extends AppCompatActivity {

    Context contextSplashActivity;
    Functions function;
    TinyDB tinyDB;
    Handler landingpage;

    String statusLogin = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        contextSplashActivity = SplashActivity.this;
        function = new Functions(contextSplashActivity);
        tinyDB = new TinyDB(contextSplashActivity);

        /*
         * Added : New handler delay before process
         * */
        landingpage = new Handler();

        /*
         * Added : Check status login true or false
         * */
        statusLogin = PreferencesLogin.getStatusLogin(contextSplashActivity);

        /*
         * Added : Setup default access server db
         * */
        tinyDB.putString("SWITCH_SERVER", "LOCAL");

        landingpage.postDelayed(() -> {

            if (statusLogin.equals("true")) {
                startActivity(new Intent(contextSplashActivity, DashboardActivity.class));
                finish();
            } else {
                startActivity(new Intent(contextSplashActivity, LoginActivity.class));
                finish();
            }
        }, 1200);

        /******************************************************************************************/ // Added : Trace for loging

        Log.e("TRACE_PREF_FRG_SPLASH",
                PreferencesLogin.getUsername(contextSplashActivity) + ", "
                        + PreferencesLogin.getUIDUser(contextSplashActivity) + ", "
                        + PreferencesLogin.getKodeUser(contextSplashActivity) + ", "
                        + PreferencesLogin.getNamaUser(contextSplashActivity) + ", "
                        + PreferencesLogin.getStatusLogin(contextSplashActivity) + ", "
                        + PreferencesLogin.getEmail(contextSplashActivity) + ", "
                        + PreferencesLogin.getAlias(contextSplashActivity) + ", "
                        + PreferencesLogin.getGender(contextSplashActivity) + ", "
                        + PreferencesLogin.getRegistered(contextSplashActivity) + ", "
                        + PreferencesLogin.getAkunToken(contextSplashActivity));



        // ex : Build 29042022.061104
        Log.e("BUILD_VERSION", function.buildVersionApp());

        // ex : true or false
        Log.e("STATUS_LOGIN", statusLogin);

        // ex : April
        Log.e("GET_MONTH", function.getMonth());

        // ex : 2022
        Log.e("GET_YEAR", String.valueOf(function.getTahunInteger()));

        // ex : Time Milis
        Log.e("GET_TIMEMILIS", String.valueOf(System.currentTimeMillis()));

        long currentDateTime = System.currentTimeMillis();

        Date currentDate = new Date(currentDateTime);

        //printing value of Date
        System.out.println("current Date: " + currentDate);

        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
        @SuppressLint("SimpleDateFormat") DateFormat formatnew = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //formatted value of current Date
        System.out.println("Milliseconds to Date: " + df.format(currentDate));
        System.out.println("Milliseconds to Date new format: " + formatnew.format(currentDate));

        //Converting milliseconds to Date using Calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentDateTime);
        System.out.println("Milliseconds to Date using Calendar:" + df.format(calendar.getTime()));
        System.out.println("Milliseconds to Date using Calendar New format :" + formatnew.format(calendar.getTime()));

        //Copying one Date's value into another Date in Java
        Date now = new Date();
        Date copiedDate = new Date(now.getTime());

        System.out.println("Original Date: " + df.format(now));
        System.out.println("Copied Date: " + df.format(copiedDate));


        buildStatusBar();
    }

    private void buildStatusBar() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}