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
import android.widget.TextView;

import com.septi.mkk.function.Controller;
import com.septi.mkk.function.Functions;
import com.septi.mkk.library.TinyDB;
import com.septi.mkk.preferences.PreferencesLogin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SplashActivity extends AppCompatActivity {

    Context contextSplashActivity;
    Functions function;
    TextView tvVersionSplash;
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

        tvVersionSplash = findViewById(R.id.tvVersionAppSplash);
        tvVersionSplash.setText(Controller.versionApp);

        /*
         * Added : Setup default access server db
         * */
        tinyDB.putString("SWITCH_SERVER", "HOSTING");

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


        Log.e("GETTIMZOMEGMT", function.getGMTtimezone());
        Log.e("GETTIMZOMEAREA", function.getTimeZoneArea());

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