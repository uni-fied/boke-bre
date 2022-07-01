package com.septi.mkk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.septi.mkk.function.Functions;
import com.septi.mkk.library.TinyDB;

public class LoginActivity extends AppCompatActivity {

    Context contextActivityLogin;
    TinyDB tinyDB;

    EditText tx_username, tx_password;
    Button bt_login;

    public static final String KOSONGKAN_INPUTAN_LOGIN = "com.septi.mkk.KOSONGKAN_INPUTAN_LOGIN";
    public static final String FINISH_CLASS = "com.septi.mkk.FINISH_CLASS";

    private final BroadcastReceiver broadcastActivityLogin = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case KOSONGKAN_INPUTAN_LOGIN:
                    tx_username.requestFocus();
                    tx_username.setText("");
                    tx_password.setText("");
                    break;
                case FINISH_CLASS:
                    finish();
                    break;
            }
        }
    };

    private void registerBroadcast() {
        IntentFilter filterBroadcast = new IntentFilter();
        filterBroadcast.addAction(KOSONGKAN_INPUTAN_LOGIN);
        filterBroadcast.addAction(FINISH_CLASS);
        LocalBroadcastManager.getInstance(contextActivityLogin).registerReceiver(broadcastActivityLogin, filterBroadcast);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        contextActivityLogin = LoginActivity.this;
        tinyDB = new TinyDB(contextActivityLogin);

        tx_username = findViewById(R.id.edtUsername);
        tx_password = findViewById(R.id.edtPassword);
        bt_login = findViewById(R.id.btnLogin);

        Functions function = new Functions(contextActivityLogin);
        registerBroadcast();

        bt_login.setOnClickListener(view -> {
            String username = tx_username.getText().toString();
            String kunci = tx_password.getText().toString();

            boolean statusInet = function.checkInet();

            if (statusInet == false) {
                Toast.makeText(contextActivityLogin, "Harap periksa jaringan Internet Anda!", Toast.LENGTH_SHORT).show();
            } else {
                if (username.equals("")) {
                    tx_username.requestFocus();
                    tx_username.setError("Masukan Username Dulu!");
                } else if (kunci.equals("")) {
                    tx_password.requestFocus();
                    tx_password.setError("Masukan Password Dulu!");
                } else {
                    function.hideKeyboard(view);
                    String accesTime = function.convertMilisecond2ReadableFormat(System.currentTimeMillis());
                    function.procedureLogin(username, kunci, accesTime);
                }
            }
        });
    }

}