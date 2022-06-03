package com.septi.mkk.fragment;

import static com.septi.mkk.DashboardActivity.FINISH_CLASS_MAIN;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.septi.mkk.R;
import com.septi.mkk.SplashActivity;
import com.septi.mkk.adapter.AdapterListIncome;
import com.septi.mkk.function.Functions;
import com.septi.mkk.library.TinyDB;
import com.septi.mkk.preferences.PreferencesLogin;

import java.util.ArrayList;

public class FragmentIncome extends Fragment {

    Context contextFragmentIncome;
    View view;
    Boolean statusNetwork = false;
    TextView cvBulanTahun, txInfoNetworkError, btReloadNetError, txJmlItemIncome, txTitleInfoIncome, txMsgInfoIncome;
    Functions functions;
    RecyclerView rvListDataMaintain;
    TinyDB tinyDB;
    ProgressBar loadingProgress;
    LinearLayout layNetError, layBelumAdaDataIncome, layContainerBS, layIsiKategoriIncome, layTambahIncome, layTambahPengeluaran;
    CardView btTambahIncome;
    RelativeLayout layIncome;
    BottomSheetDialog bsFragmentIncome;
    View bottomSheetView;
    EditText edtKategoriIncome, edtDeskripsiIncome, edtJmlIncome;
    TextView tvButtonTambah, tvGetMessageSuccesAddTambahKategori, txBatalkanSimpanIncome, btnSimpanIncome, btnBatalkanTambahIncome, btnBatalkanTambahOutcome;
    ProgressBar loadingTambahKategoriIncome, loadingTambahIncome;

    TextView txJmlSaldo, txJmlOut, txJmlTabungan, txJmlSedekah;

    Handler loadingsuccessaddincome, waiting, waitforlogout;

    public static final String RETRIEVE_INFO_SALDO_AND_DATA_INCOME = "com.septi.mkk.RETRIEVE_INFO_SALDO_AND_DATA_INCOME";
    public static final String STOP_PB_LOADING = "com.septi.mkk.STOP_PB_LOADING";
    public static final String SHOW_MESSAGE_ERROR_NETWORK = "com.septi.mkk.SHOW_MESSAGE_ERROR_NETWORK";
    public static final String HIDE_MESSAGE_ERROR_NETWORK = "com.septi.mkk.HIDE_MESSAGE_ERROR_NETWORK";
    public static final String BERHASIL_TAMBAH_KATEGORI_INCOME = "com.septi.mkk.BERHASIL_TAMBAH_KATEGORI_INCOME";
    public static final String BERHASIL_TAMBAH_INCOME = "com.septi.mkk.BERHASIL_TAMBAH_INCOME";
    public static final String GAGAL_SIMPAN_KATEGORI_INCOME = "com.septi.mkk.GAGAL_SIMPAN_KATEGORI_INCOME";
    public static final String SUCCESS_TAMBAH_PEMASUKAN = "com.septi.mkk.SUCCESS_TAMBAH_PEMASUKAN";
    public static final String RELOAD_DASHBOARD = "com.septi.mkk.RELOAD_DASHBOARD";
    public static final String TAMBAH_PENGELUARAN = "com.septi.mkk.TAMBAH_PENGELUARAN";

    private final BroadcastReceiver broadcastSignalFragmentIncome = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case TAMBAH_PENGELUARAN:
                    bsFragmentIncome.getBehavior().setDraggable(true);
                    bsFragmentIncome.setCancelable(false);
                    bsFragmentIncome.setContentView(bottomSheetView);
                    bsFragmentIncome.show();

                    layTambahIncome.setVisibility(View.GONE);
                    layIsiKategoriIncome.setVisibility(View.GONE);
                    layTambahPengeluaran.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS_TAMBAH_PEMASUKAN:
                    loadingTambahIncome.setVisibility(View.GONE);
                    functions.showProgressDialog("Harap tunggu, sedang memproses data...", false);

                    loadingsuccessaddincome = new Handler();
                    loadingsuccessaddincome.postDelayed(() -> {
                        functions.hideProgressDialog();

                        edtDeskripsiIncome.setText("");
                        edtJmlIncome.setText("");
                        bsFragmentIncome.dismiss();

                        loadingProgress.setVisibility(View.VISIBLE);
                        functions.procedureGetInformasiSaldo();

                        layIsiKategoriIncome.setVisibility(View.VISIBLE);
                        layTambahIncome.setVisibility(View.GONE);

                        edtDeskripsiIncome.setEnabled(true);
                        edtJmlIncome.setEnabled(true);

                        btnSimpanIncome.setEnabled(true);
                        btnSimpanIncome.setText("Tambahkan Pemasukan");
                        btnSimpanIncome.setBackgroundResource(R.drawable.lbl_tabungan);
                        btnSimpanIncome.setTextColor(Color.WHITE);
                    }, 1500);
                    break;
                case GAGAL_SIMPAN_KATEGORI_INCOME:
                    bsFragmentIncome.dismiss();
                    loadingTambahKategoriIncome.setVisibility(View.GONE);
                    tvButtonTambah.setText("Tambah");
                    tvButtonTambah.setBackgroundResource(R.drawable.lbl_jumlah_saldo);
                    tvButtonTambah.setTextColor(Color.parseColor("#FFFFFF"));
                    edtKategoriIncome.setText("");
                    edtKategoriIncome.setEnabled(true);
                    break;
                case BERHASIL_TAMBAH_KATEGORI_INCOME:

                    Drawable icSuccess = getContext().getResources().getDrawable(R.drawable.ic_wait);
                    icSuccess.setBounds(0, 0, 60, 60);
                    tvGetMessageSuccesAddTambahKategori.setCompoundDrawables(icSuccess, null, null, null);
                    tvGetMessageSuccesAddTambahKategori.setText(tinyDB.getString("GET_MESSAGE"));

                    tvButtonTambah.setText("Tambah");
                    tvButtonTambah.setBackgroundResource(R.drawable.lbl_jumlah_saldo);
                    tvButtonTambah.setTextColor(Color.parseColor("#FFFFFF"));
                    edtKategoriIncome.setText("");
                    edtKategoriIncome.setEnabled(true);

                    layIsiKategoriIncome.setVisibility(View.GONE);

                    loadingTambahKategoriIncome.setVisibility(View.GONE);
                    layTambahPengeluaran.setVisibility(View.GONE);
                    layTambahIncome.setVisibility(View.VISIBLE);
                    break;
                case RETRIEVE_INFO_SALDO_AND_DATA_INCOME:

                    btTambahIncome.setClickable(true);

                    txJmlSaldo.setText("Rp. " + functions.decimal2Rp(String.valueOf(tinyDB.getInt("jmlSaldo"))) + "-,");
                    txJmlOut.setText("Rp. " + functions.decimal2Rp(String.valueOf(tinyDB.getInt("jmlOut"))) + "-,");
                    txJmlTabungan.setText("Rp. " + functions.decimal2Rp(String.valueOf(tinyDB.getInt("jmlTabungan"))) + "-,");
                    txJmlSedekah.setText("Rp. " + functions.decimal2Rp(String.valueOf(tinyDB.getInt("jmlSedekah"))) + "-,");

                    txJmlItemIncome.setText(contextFragmentIncome.getString(R.string.total_data_income,  tinyDB.getInt("JML_DATA_INCOME")));

                    retrieveDataIncome();

                    switch (txJmlItemIncome.getText().toString().substring(0, 1)) {
                        case "0":
                            layBelumAdaDataIncome.setVisibility(View.VISIBLE);
                            rvListDataMaintain.setVisibility(View.GONE);

                            txTitleInfoIncome.setText("Woapps!");
                            txMsgInfoIncome.setText(R.string.not_exist_data_income);
                            break;
                        default:
                            layBelumAdaDataIncome.setVisibility(View.GONE);
                            rvListDataMaintain.setVisibility(View.VISIBLE);
                            break;
                    }

                    loadingProgress.setVisibility(View.GONE);
                    break;
                case STOP_PB_LOADING:
                    loadingProgress.setVisibility(View.GONE);
                    break;
                case SHOW_MESSAGE_ERROR_NETWORK:

                    layNetError.setVisibility(View.VISIBLE);

                    String typeErrorNetwork = tinyDB.getString("TYPE_ERROR_NETWORK");
                    String msgErrorNet = tinyDB.getString("MSG_ERROR_NET");

                    switch (typeErrorNetwork) {
                        case "TimeoutError":
                            txInfoNetworkError.setText(msgErrorNet);
                            btReloadNetError.setText("Reload");

                            txTitleInfoIncome.setText("Terjadi Kesalahan");
                            txMsgInfoIncome.setText("Mohon maaf, Anda tidak dapat izin untuk mengakses data!");
                            break;
                        case "NetworkError":
                            txInfoNetworkError.setText(msgErrorNet);
                            btReloadNetError.setText("Try");

                            txTitleInfoIncome.setText("Terjadi Kesalahan");
                            txMsgInfoIncome.setText("Mohon maaf, Anda tidak dapat izin untuk mengakses data!");
                            break;
                        case "TokenAksesNotValid":
                            txInfoNetworkError.setText(msgErrorNet);
                            btReloadNetError.setText("Logout");

                            txTitleInfoIncome.setText("Terjadi Kesalahan");
                            txMsgInfoIncome.setText("Mohon maaf, Anda tidak dapat izin untuk mengakses data!");
                            break;
                        case "DataTidakDitemukan":
                            txInfoNetworkError.setText(msgErrorNet);
                            btReloadNetError.setText("Info");

                            layBelumAdaDataIncome.setVisibility(View.VISIBLE);

                            txTitleInfoIncome.setText("Data tidak tersedia!");
                            txMsgInfoIncome.setText(contextFragmentIncome.getString(R.string.not_exist_data_income));
                            break;
                    }
                    break;
                case HIDE_MESSAGE_ERROR_NETWORK:
                    layNetError.setVisibility(View.GONE);
                    break;
                case RELOAD_DASHBOARD:
                    Log.e("ONRESUME_DASHBOARD", "Reload Get Info Saldo");
                    resetFill();
                    loadingProgress.setVisibility(View.VISIBLE);

                    waiting = new Handler();
                    waiting.postDelayed(() -> {
                        functions.procedureGetInformasiSaldo();
                        cvBulanTahun.setText(functions.bulanTahun());
                    }, 2000);

                    break;
            }
        }
    };

    private void retrieveDataIncome() {
        ArrayList<Integer> idIncome = tinyDB.getListInt("ARR_ID_ICM");
        ArrayList<String> userName = tinyDB.getListString("ARR_USR_NM");
        ArrayList<String> namaUser = tinyDB.getListString("ARR_NM_USR");
        ArrayList<String> jnsKelamin = tinyDB.getListString("ARR_JNS_KLMN");
        ArrayList<String> hriIncome = tinyDB.getListString("ARR_HARI_TBH");
        ArrayList<String> tglIncome = tinyDB.getListString("ARR_TGL_TBH");
        ArrayList<String> jamIncome = tinyDB.getListString("ARR_JAM_TBH");
        ArrayList<String> katIncome = tinyDB.getListString("ARR_CAT_INC");
        ArrayList<String> ketIncome = tinyDB.getListString("ARR_KETERANGAN");
        ArrayList<Integer> jmlIncome = tinyDB.getListInt("ARR_JML_INC");

        AdapterListIncome adapterListIncome = new AdapterListIncome(contextFragmentIncome, idIncome, userName, namaUser, jnsKelamin, hriIncome, tglIncome, jamIncome, katIncome, ketIncome, jmlIncome);
        rvListDataMaintain.setAdapter(adapterListIncome);
    }

    private void registerBroadcast() {
        IntentFilter filterBroadcast = new IntentFilter();
        filterBroadcast.addAction(RETRIEVE_INFO_SALDO_AND_DATA_INCOME);
        filterBroadcast.addAction(STOP_PB_LOADING);
        filterBroadcast.addAction(TAMBAH_PENGELUARAN);
        filterBroadcast.addAction(SHOW_MESSAGE_ERROR_NETWORK);
        filterBroadcast.addAction(HIDE_MESSAGE_ERROR_NETWORK);
        filterBroadcast.addAction(BERHASIL_TAMBAH_KATEGORI_INCOME);
        filterBroadcast.addAction(GAGAL_SIMPAN_KATEGORI_INCOME);
        filterBroadcast.addAction(BERHASIL_TAMBAH_INCOME);
        filterBroadcast.addAction(SUCCESS_TAMBAH_PEMASUKAN);
        filterBroadcast.addAction(RELOAD_DASHBOARD);
        LocalBroadcastManager.getInstance(contextFragmentIncome).registerReceiver(broadcastSignalFragmentIncome, filterBroadcast);
    }

    public FragmentIncome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contextFragmentIncome = FragmentIncome.this.getContext();
        functions = new Functions(contextFragmentIncome);
        tinyDB = new TinyDB(contextFragmentIncome);

        /*
         * Init BottomSheet
         * */

        bsFragmentIncome = new BottomSheetDialog(contextFragmentIncome, R.style.BottomSheetDialogTheme);
        bottomSheetView = LayoutInflater.from(contextFragmentIncome.getApplicationContext()).inflate(R.layout.bs_fragment_income, getActivity().findViewById(R.id.bsTambahIncomeContainer));

        registerBroadcast();

    }

    public void resetFill() {
        btTambahIncome.setClickable(false);

        txJmlSaldo.setText("Rp. 0.00-,");
        txJmlOut.setText("Rp. 0.00-,");
        txJmlTabungan.setText("Rp. 0.00-,");
        txJmlSedekah.setText("Rp. 0.00-,");
        txJmlItemIncome.setText("0 Item Income");

        layBelumAdaDataIncome.setVisibility(View.VISIBLE);
        rvListDataMaintain.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_income, container, false);

        int sizeDaysInThisMonth = functions.getSizeDaysThisMonth(functions.getTahunInteger(), functions.getMonthInteger());

        layIncome = view.findViewById(R.id.rlIncomeLay);
        cvBulanTahun = view.findViewById(R.id.cardBulanTahun);
        btTambahIncome = view.findViewById(R.id.cvBtTambahIncome);

        rvListDataMaintain = view.findViewById(R.id.rvListDataMaintain);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvListDataMaintain.setLayoutManager(layoutManager);

        txInfoNetworkError = view.findViewById(R.id.txtErrorNetwork);
        btReloadNetError = view.findViewById(R.id.btnReloadErrorNetwork);
        txJmlSaldo = view.findViewById(R.id.tvSaldoNow);
        txJmlOut = view.findViewById(R.id.tvPengeluaran);
        txJmlTabungan = view.findViewById(R.id.tvTabungan);
        txJmlSedekah = view.findViewById(R.id.tvSedekah);
        txJmlItemIncome = view.findViewById(R.id.tvJmlDataItemIncome);
        txTitleInfoIncome = view.findViewById(R.id.tvTitleInfoDataIncome);
        txMsgInfoIncome = view.findViewById(R.id.tvMsgInfoDataIncome);

        edtKategoriIncome = bottomSheetView.findViewById(R.id.edtKategoriIncome);
        tvButtonTambah = bottomSheetView.findViewById(R.id.btnTambahKategoriIncome);
        tvGetMessageSuccesAddTambahKategori = bottomSheetView.findViewById(R.id.txGetMessageSucces);
        txBatalkanSimpanIncome = bottomSheetView.findViewById(R.id.txBatalkanProsesSimpanIncome);
        edtDeskripsiIncome = bottomSheetView.findViewById(R.id.etDeskripsiPemasukan);
        edtJmlIncome = bottomSheetView.findViewById(R.id.etJmlIncome);
        btnSimpanIncome = bottomSheetView.findViewById(R.id.btnTambahkanPemasukanNow);
        loadingTambahKategoriIncome = bottomSheetView.findViewById(R.id.progresLoadingTambahKategoriIncome);
        loadingTambahIncome = bottomSheetView.findViewById(R.id.progresLoadingTambahPemasukan);
        layContainerBS = bottomSheetView.findViewById(R.id.bsTambahIncomeContainer);
        btnBatalkanTambahIncome = bottomSheetView.findViewById(R.id.btBatalkanTambahKategoriIncome);
        btnBatalkanTambahOutcome = bottomSheetView.findViewById(R.id.btBatalkanTambahPengeluaran);

        layIsiKategoriIncome = bottomSheetView.findViewById(R.id.layoutTambahKategoriIncome);
        layTambahIncome = bottomSheetView.findViewById(R.id.layoutTambahDataPemasukan);
        layTambahPengeluaran = bottomSheetView.findViewById(R.id.layoutTambahPengeluaran);

        cvBulanTahun.setText(functions.bulanTahun());
        functions.procedureGetInformasiSaldo();

        txBatalkanSimpanIncome.setOnClickListener(view -> {
            /*
             * Fixed : Batalkan & Hapus data Input Kategori Income
             * */

            loadingTambahIncome.setVisibility(View.GONE);
            edtDeskripsiIncome.setEnabled(true);
            edtJmlIncome.setEnabled(true);

            btnSimpanIncome.setEnabled(true);
            btnSimpanIncome.setText("Tambahkan Pemasukan");
            btnSimpanIncome.setBackgroundResource(R.drawable.lbl_tabungan);
            btnSimpanIncome.setTextColor(Color.WHITE);

            bsFragmentIncome.dismiss();

            layIsiKategoriIncome.setVisibility(View.VISIBLE);
            layTambahIncome.setVisibility(View.GONE);

            /*
             * Fixed : Proses Hapus Data Kategori Pemasukan yang sudah Ditambahkan sebelumnya
             * Get by : tinyDB.getString("ID_PEMASUKAN")
             * */

            String idpemasukan = tinyDB.getString("ID_PEMASUKAN");
            functions.procedureHapusSumberIncome(idpemasukan);
        });

        btnSimpanIncome.setOnClickListener(view -> {
            /*
             * Handle Inputan
             * */

            if (edtDeskripsiIncome.getText().toString().equals("")) {
                functions.hideKeyboard(view);
                edtDeskripsiIncome.requestFocus();
                edtDeskripsiIncome.setError("Isi dahulu deskripsi income!");
            } else if (edtJmlIncome.getText().toString().equals("")) {
                functions.hideKeyboard(view);
                edtJmlIncome.requestFocus();
                edtJmlIncome.setError("Isi dahulu jumlah income Anda!");
            } else {
                edtDeskripsiIncome.setEnabled(false);
                edtJmlIncome.setEnabled(false);

                btnSimpanIncome.setEnabled(false);
                btnSimpanIncome.setText("Sedang Menyimpan Pemasukan");
                btnSimpanIncome.setBackgroundResource(R.drawable.lbl_version);
                btnSimpanIncome.setTextColor(Color.WHITE);

                loadingTambahIncome.setVisibility(View.VISIBLE);

                String idpemasukan = tinyDB.getString("ID_PEMASUKAN");
                String descpemasukan = edtDeskripsiIncome.getText().toString();
                String jmlincome = edtJmlIncome.getText().toString();

                functions.procedureTambahPemasukan(idpemasukan, descpemasukan, jmlincome);
            }
        });

        btReloadNetError.setOnClickListener(view -> {
            if (btReloadNetError.getText().toString().equals("Logout")) {
                /*
                 * Added : Clear Preferences All
                 * */
                PreferencesLogin.clearPreferencesLogin(contextFragmentIncome);

                layNetError.setVisibility(View.GONE);
                Toast.makeText(contextFragmentIncome, "Data Anda kami hapus, tunggu beberapa saat kemudian silahkan login ulang.", Toast.LENGTH_SHORT).show();

                waitforlogout = new Handler();
                waitforlogout.postDelayed((Runnable) () -> {
                    contextFragmentIncome.startActivity(new Intent(contextFragmentIncome, SplashActivity.class));
                    LocalBroadcastManager.getInstance(contextFragmentIncome).sendBroadcast(new Intent(FINISH_CLASS_MAIN));
                }, 1200);
            } else {
                LocalBroadcastManager.getInstance(contextFragmentIncome).sendBroadcast(new Intent(RELOAD_DASHBOARD));
            }
        });

        btnBatalkanTambahOutcome.setOnClickListener(v -> {
            bsFragmentIncome.dismiss();
        });

        btTambahIncome.setOnClickListener(view -> {
            statusNetwork = functions.checkInet();
            if (!statusNetwork) {
                Snackbar snackBar = Snackbar.make(layIncome, "Anda belum dapat menambah data income, Pastikan Internet Anda lebih dahulu!", Snackbar.LENGTH_LONG);
                snackBar.getView().setBackgroundColor(ContextCompat.getColor(contextFragmentIncome, R.color.primary_color));
                snackBar.show();
            } else {
                layTambahIncome.setVisibility(View.GONE);
                layTambahPengeluaran.setVisibility(View.GONE);
                layIsiKategoriIncome.setVisibility(View.VISIBLE);

                tvButtonTambah.setOnClickListener(view1 -> {

                    if (edtKategoriIncome.getText().toString().equals("")) {
                        functions.hideKeyboard(view1);
                        edtKategoriIncome.requestFocus();
                        edtKategoriIncome.setError("Isi sumber pemasukan Anda!");
                    } else {
                        loadingTambahKategoriIncome.setVisibility(View.VISIBLE);
                        functions.hideKeyboard(view1);

                        tvButtonTambah.setText("Tunggu");
                        tvButtonTambah.setBackgroundResource(R.drawable.lbl_version);
                        tvButtonTambah.setTextColor(Color.WHITE);
                        edtKategoriIncome.setEnabled(false);

                        functions.procedureAddCategoryIncome(PreferencesLogin.getUIDUser(contextFragmentIncome), edtKategoriIncome.getText().toString());
                    }
                });

                btnBatalkanTambahIncome.setOnClickListener(view2 -> {
                    bsFragmentIncome.dismiss();
                    loadingTambahKategoriIncome.setVisibility(View.GONE);
                    tvButtonTambah.setText("Tambah");
                    edtKategoriIncome.setText("");
                });

                layContainerBS.setOnClickListener(view3 -> {
                    /*
                     * Hide Keyboard if Click Container Area
                     * */
                    functions.hideKeyboard(view3);
                });

                bsFragmentIncome.getBehavior().setDraggable(true);
                bsFragmentIncome.setCancelable(false);
                bsFragmentIncome.setContentView(bottomSheetView);
                bsFragmentIncome.show();
            }
        });

        loadingProgress = view.findViewById(R.id.progresLoadingIncome);
        layNetError = view.findViewById(R.id.layErrorNetworkIncome);
        layBelumAdaDataIncome = view.findViewById(R.id.layInfoBelumAdaData);

        /******************************************************************************************/ // Added : Trace for loging

        Log.e("TRACE_PREF_FRG_INCM",
                PreferencesLogin.getUsername(contextFragmentIncome) + ", "
                        + PreferencesLogin.getUIDUser(contextFragmentIncome) + ", "
                        + PreferencesLogin.getKodeUser(contextFragmentIncome) + ", "
                        + PreferencesLogin.getNamaUser(contextFragmentIncome) + ", "
                        + PreferencesLogin.getStatusLogin(contextFragmentIncome) + ", "
                        + PreferencesLogin.getEmail(contextFragmentIncome) + ", "
                        + PreferencesLogin.getAlias(contextFragmentIncome) + ", "
                        + PreferencesLogin.getGender(contextFragmentIncome) + ", "
                        + PreferencesLogin.getRegistered(contextFragmentIncome) + ", "
                        + PreferencesLogin.getAkunToken(contextFragmentIncome));
        return view;
    }
}