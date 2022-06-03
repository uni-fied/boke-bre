package com.septi.mkk;

import static com.septi.mkk.fragment.FragmentIncome.RELOAD_DASHBOARD;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.septi.mkk.fragment.FragmentAset;
import com.septi.mkk.fragment.FragmentIncome;
import com.septi.mkk.fragment.FragmentInvest;
import com.septi.mkk.fragment.FragmentTabungan;
import com.septi.mkk.fragment.FragmentOutcome;
import com.septi.mkk.function.Functions;
import com.septi.mkk.function.Controller;
import com.septi.mkk.library.TinyDB;
import com.septi.mkk.preferences.PreferencesLogin;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    Context contextDashboardActivity;
    TinyDB tinyDB;

    private ViewPager pagerFragment;
    FragmentManager fragmentManager;

    Fragment fragment_income = new FragmentIncome();
    final Fragment fragment_outcome = new FragmentOutcome();
    final Fragment fragment_tabungan = new FragmentTabungan();
    final Fragment fragment_investasi = new FragmentInvest();
    final Fragment fragment_aset = new FragmentAset();

    CoordinatorLayout layDashboard;

    ImageView ivIncome, ivOutcome, ivTabungan, ivInvest, ivAset;
    TextView txIncome, txOutcome, txTabungan, txInvest, txAset, txVersiApk;
    LinearLayout mIncome, mOutcome, mTabungan, mInvest, mAset;

    TextView txUid, txName, txMail;
    ImageView gbrProfile;

    Functions function;

    public static final String FINISH_CLASS_MAIN = "com.septi.mkk.FINISH_CLASS_MAIN";
    public static final String SHOW_SNACKBAR_CUSTOME = "com.septi.mkk.SHOW_SNACKBAR_CUSTOME";

    private final BroadcastReceiver broadcastActivityDashboard = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case SHOW_SNACKBAR_CUSTOME:
                    String pesan = tinyDB.getString("SET_MESSAGE_CUSTOM_SNACKBAR");
                    Snackbar mySnackbar = Snackbar.make(layDashboard, pesan, Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                    break;
                case FINISH_CLASS_MAIN:
                    PreferencesLogin.clearPreferencesLogin(contextDashboardActivity);

                    tinyDB.remove("jmlSaldo");
                    tinyDB.remove("jmlOut");
                    tinyDB.remove("jmlTabungan");
                    tinyDB.remove("jmlSedekah");
                    finish();
                    break;
            }
        }
    };

    private void registerBroadcast() {
        IntentFilter filterBroadcast = new IntentFilter();
        filterBroadcast.addAction(FINISH_CLASS_MAIN);
        LocalBroadcastManager.getInstance(contextDashboardActivity).registerReceiver(broadcastActivityDashboard, filterBroadcast);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().hide();


        contextDashboardActivity = DashboardActivity.this;
        tinyDB = new TinyDB(contextDashboardActivity);

        function = new Functions(contextDashboardActivity);

        pagerFragment = findViewById(R.id.vpContainer);
        ivIncome = findViewById(R.id.imvIncome);
        ivOutcome = findViewById(R.id.imvOutcome);
        ivTabungan = findViewById(R.id.imvTabungan);
        ivInvest = findViewById(R.id.imvInvest);
        ivAset = findViewById(R.id.imvAset);
        gbrProfile = findViewById(R.id.imgProfile);

        layDashboard = findViewById(R.id.clDashboard);

        txIncome = findViewById(R.id.txtIncome);
        txOutcome = findViewById(R.id.txtOutcome);
        txTabungan = findViewById(R.id.txtTabungan);
        txInvest = findViewById(R.id.txtInvest);
        txAset = findViewById(R.id.txtAset);

        mIncome = findViewById(R.id.mnIncome);
        mOutcome = findViewById(R.id.mnOutcome);
        mTabungan = findViewById(R.id.mnTabungan);
        mInvest = findViewById(R.id.mnInvestasi);
        mAset = findViewById(R.id.mnAset);

        txUid = findViewById(R.id.txUIDUser);
        txName = findViewById(R.id.txNameUser);
        txMail = findViewById(R.id.txMailUser);

        txVersiApk = findViewById(R.id.txVersiApl);
        txVersiApk.setText(Controller.versionApp);

        registerBroadcast();

        String genderSwitch = PreferencesLogin.getGender(contextDashboardActivity);
        switch (genderSwitch) {
            case "Laki-laki":
                gbrProfile.setImageResource(R.drawable.man);
                break;
            case "Perempuan":
                gbrProfile.setImageResource(R.drawable.woman);
                break;
        }

        txUid.setText(contextDashboardActivity.getString(R.string.uid_user, PreferencesLogin.getKodeUser(contextDashboardActivity)));
        txName.setText(contextDashboardActivity.getString(R.string.name_user, PreferencesLogin.getNamaUser(contextDashboardActivity)));
        txMail.setText(PreferencesLogin.getEmail(contextDashboardActivity));

        gbrProfile.setOnClickListener(view -> {
            function.customDialog(3, "Logout Akun?", "Apakah Anda yakin ingin keluar dari Akun ini?", "Logout", true);
        });

        fragmentManager = getSupportFragmentManager();

        final ArrayList<Fragment> list = new ArrayList<>();

        list.add(fragment_income);
        list.add(fragment_outcome);
        list.add(fragment_tabungan);
        list.add(fragment_investasi);
        list.add(fragment_aset);

        mIncome.setOnClickListener(view -> {
            pagerFragment.setCurrentItem(0, true);
        });

        mOutcome.setOnClickListener(view -> {
            pagerFragment.setCurrentItem(1, true);
        });

        mTabungan.setOnClickListener(view -> {
            pagerFragment.setCurrentItem(2, true);
        });

        mInvest.setOnClickListener(view -> {
            pagerFragment.setCurrentItem(3, true);
        });

        mAset.setOnClickListener(view -> {
            pagerFragment.setCurrentItem(4, true);
        });

        pagerFragment.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }
        });

        pagerFragment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        ivIncome.setPadding(0,0,0,0);
                        ivOutcome.setPadding(0,20,0,0);
                        ivTabungan.setPadding(0,20,0,0);
                        ivInvest.setPadding(0,20,0,0);
                        ivAset.setPadding(0,20,0,0);

                        txIncome.setTextSize(13); txIncome.setTextColor(Color.parseColor("#009688")); // Active
                        txOutcome.setTextSize(10); txOutcome.setTextColor(Color.parseColor("#40ECDC"));
                        txTabungan.setTextSize(10); txTabungan.setTextColor(Color.parseColor("#40ECDC"));
                        txInvest.setTextSize(10); txInvest.setTextColor(Color.parseColor("#40ECDC"));
                        txAset.setTextSize(10); txAset.setTextColor(Color.parseColor("#40ECDC"));
                        break;
                    case 1:
                        ivIncome.setPadding(0,20,0,0);
                        ivOutcome.setPadding(0,0,0,0);
                        ivTabungan.setPadding(0,20,0,0);
                        ivInvest.setPadding(0,20,0,0);
                        ivAset.setPadding(0,20,0,0);

                        txIncome.setTextSize(10); txIncome.setTextColor(Color.parseColor("#40ECDC"));
                        txOutcome.setTextSize(13); txOutcome.setTextColor(Color.parseColor("#009688")); // Active
                        txTabungan.setTextSize(10); txTabungan.setTextColor(Color.parseColor("#40ECDC"));
                        txInvest.setTextSize(10); txInvest.setTextColor(Color.parseColor("#40ECDC"));
                        txAset.setTextSize(10); txAset.setTextColor(Color.parseColor("#40ECDC"));
                        break;
                    case 2:
                        ivIncome.setPadding(0,20,0,0);
                        ivOutcome.setPadding(0,20,0,0);
                        ivTabungan.setPadding(0,0,0,0);
                        ivInvest.setPadding(0,20,0,0);
                        ivAset.setPadding(0,20,0,0);

                        txIncome.setTextSize(10); txIncome.setTextColor(Color.parseColor("#40ECDC"));
                        txOutcome.setTextSize(10); txOutcome.setTextColor(Color.parseColor("#40ECDC"));
                        txTabungan.setTextSize(13); txTabungan.setTextColor(Color.parseColor("#009688")); // Active
                        txInvest.setTextSize(10); txInvest.setTextColor(Color.parseColor("#40ECDC"));
                        txAset.setTextSize(10); txAset.setTextColor(Color.parseColor("#40ECDC"));
                        break;
                    case 3:
                        ivIncome.setPadding(0,20,0,0);
                        ivOutcome.setPadding(0,20,0,0);
                        ivTabungan.setPadding(0,20,0,0);
                        ivInvest.setPadding(0,0,0,0);
                        ivAset.setPadding(0,20,0,0);

                        txIncome.setTextSize(10); txIncome.setTextColor(Color.parseColor("#40ECDC"));
                        txOutcome.setTextSize(10); txOutcome.setTextColor(Color.parseColor("#40ECDC"));
                        txTabungan.setTextSize(10); txTabungan.setTextColor(Color.parseColor("#40ECDC"));
                        txInvest.setTextSize(13); txInvest.setTextColor(Color.parseColor("#009688")); // Active
                        txAset.setTextSize(10); txAset.setTextColor(Color.parseColor("#40ECDC"));
                        break;
                    case 4:
                        ivIncome.setPadding(0,20,0,0);
                        ivOutcome.setPadding(0,20,0,0);
                        ivTabungan.setPadding(0,20,0,0);
                        ivInvest.setPadding(0,20,0,0);
                        ivAset.setPadding(0,0,0,0);

                        txIncome.setTextSize(10); txIncome.setTextColor(Color.parseColor("#40ECDC"));
                        txOutcome.setTextSize(10); txOutcome.setTextColor(Color.parseColor("#40ECDC"));
                        txTabungan.setTextSize(10); txTabungan.setTextColor(Color.parseColor("#40ECDC"));
                        txInvest.setTextSize(10); txInvest.setTextColor(Color.parseColor("#40ECDC"));
                        txAset.setTextSize(13); txAset.setTextColor(Color.parseColor("#009688")); // Active
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        pagerFragment.setCurrentItem(0, true);

        LocalBroadcastManager.getInstance(contextDashboardActivity).sendBroadcast(new Intent(RELOAD_DASHBOARD));
    }
}