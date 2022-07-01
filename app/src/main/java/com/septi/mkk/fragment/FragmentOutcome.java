package com.septi.mkk.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.septi.mkk.R;
import com.septi.mkk.adapter.AdapterListIncome;
import com.septi.mkk.adapter.AdapterListOutcome;
import com.septi.mkk.function.Functions;
import com.septi.mkk.library.TinyDB;

import java.util.ArrayList;

public class FragmentOutcome extends Fragment {

    View view;
    RecyclerView rvListPengeluaran;
    Context contextFragmentOutcome;
    Functions functions;
    TinyDB tinyDB;
    ProgressBar progressLoadingOutCome;
    TextView tvJmlPengeluaranItem, tvInfoPesanPengeluaran, tvTittleInfoPesanPengeluaran;
    LinearLayout layoutErrorInfo;

    public static final String RETRIEVE_LIST_PENGELUARAN = "com.septi.mkk.RETRIEVE_LIST_PENGELUARAN";
    public static final String SIGNAL_OUTCOME_CAN_NOT_RETRIEVE = "com.septi.mkk.SIGNAL_OUTCOME_CAN_NOT_RETRIEVE";
    public static final String SIGNAL_SHOW_LOADING_PROGRESS = "com.septi.mkk.SIGNAL_SHOW_LOADING_PROGRESS";
    public static final String DATA_PENGELUARAN_BELUM_ADA = "com.septi.mkk.DATA_PENGELUARAN_BELUM_ADA";

    private final BroadcastReceiver broadcastSignalFragmentOutcome = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case DATA_PENGELUARAN_BELUM_ADA:

                    int jmlItemOutCome = tinyDB.getInt("JML_PENGELUARAN");

                    Log.e("JUMLAHOUTCOME_SAATINI", String.valueOf(jmlItemOutCome));
                    layoutErrorInfo.setVisibility(View.VISIBLE);
                    if (jmlItemOutCome == 0) {
                        progressLoadingOutCome.setVisibility(View.GONE);
                        rvListPengeluaran.setVisibility(View.GONE);

                        tvTittleInfoPesanPengeluaran.setText(tinyDB.getString("TITLE_PENGELUARAN"));
                        tvInfoPesanPengeluaran.setText(tinyDB.getString("MESSAGE_PENGELUARAN"));
                    } else {
                        progressLoadingOutCome.setVisibility(View.VISIBLE);
                        rvListPengeluaran.setVisibility(View.VISIBLE);
                        layoutErrorInfo.setVisibility(View.GONE);
                    }
                    break;
                case SIGNAL_SHOW_LOADING_PROGRESS:
                    progressLoadingOutCome.setVisibility(View.VISIBLE);
                    break;
                case RETRIEVE_LIST_PENGELUARAN:
                    progressLoadingOutCome.setVisibility(View.GONE);

                    tvJmlPengeluaranItem.setText(tinyDB.getString("INFO_JML_ITEM"));

                    retrieveDataOutcome();


                    rvListPengeluaran.setVisibility(View.VISIBLE);
                    layoutErrorInfo.setVisibility(View.GONE);

                    break;
                case SIGNAL_OUTCOME_CAN_NOT_RETRIEVE:
                    progressLoadingOutCome.setVisibility(View.GONE);
                    layoutErrorInfo.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    private void registerBroadcast() {
        IntentFilter filterBroadcast = new IntentFilter();
        filterBroadcast.addAction(RETRIEVE_LIST_PENGELUARAN);
        filterBroadcast.addAction(SIGNAL_SHOW_LOADING_PROGRESS);
        filterBroadcast.addAction(SIGNAL_OUTCOME_CAN_NOT_RETRIEVE);
        filterBroadcast.addAction(DATA_PENGELUARAN_BELUM_ADA);
        LocalBroadcastManager.getInstance(contextFragmentOutcome).registerReceiver(broadcastSignalFragmentOutcome, filterBroadcast);
    }

    public FragmentOutcome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contextFragmentOutcome = FragmentOutcome.this.getContext();
        functions = new Functions(contextFragmentOutcome);
        tinyDB = new TinyDB(contextFragmentOutcome);

        registerBroadcast();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_outcome, container, false);

        progressLoadingOutCome = view.findViewById(R.id.progresLoadingOutcome);
        tvJmlPengeluaranItem = view.findViewById(R.id.tvJmlDataItemOutcome);
        tvInfoPesanPengeluaran = view.findViewById(R.id.txSubTittleErrorInfo_DataOutcome);
        tvTittleInfoPesanPengeluaran = view.findViewById(R.id.txTittleErrorInfo_DataOutcome);
        layoutErrorInfo = view.findViewById(R.id.layInfoBelumAdaDataOutcome);

        rvListPengeluaran = view.findViewById(R.id.rvListPengeluaran);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvListPengeluaran.setLayoutManager(layoutManager);

        return view;
    }

    private void retrieveDataOutcome() {
        ArrayList<Integer> idOut = tinyDB.getListInt("ARR_ID_OUT");
        ArrayList<Integer> idDetOut = tinyDB.getListInt("ARR_ID_PENGELUARAN");
        ArrayList<String> katOut = tinyDB.getListString("ARR_CAT_OUT");
        ArrayList<Integer> idSourceIn = tinyDB.getListInt("ARR_ID_SOURCE_IN");
        ArrayList<String> titleSource = tinyDB.getListString("ARR_TITLE_SOURCE_IN");
        ArrayList<Integer> idUser = tinyDB.getListInt("ARR_ID_USER");
        ArrayList<String> nameUser = tinyDB.getListString("ARR_NAME_USER");
        ArrayList<String> jkUser = tinyDB.getListString("ARR_JK_USER");
        ArrayList<String> tglOut = tinyDB.getListString("ARR_TGL_OUT");
        ArrayList<String> jamOut = tinyDB.getListString("ARR_JAM_OUT");
        ArrayList<String> detailOut = tinyDB.getListString("ARR_DETAIL_OUT");
        ArrayList<Integer> jmlRpOut = tinyDB.getListInt("ARR_RP_JML_OUT");

        AdapterListOutcome adapterListOutcome = new AdapterListOutcome(contextFragmentOutcome,
                idOut,
                idDetOut,
                katOut,
                idSourceIn,
                titleSource,
                idUser,
                nameUser,
                jkUser,
                tglOut,
                jamOut,
                detailOut,
                jmlRpOut);
        rvListPengeluaran.setAdapter(adapterListOutcome);
    }
}