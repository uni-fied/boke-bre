package com.septi.mkk.fragment;

import static com.septi.mkk.DashboardActivity.FINISH_CLASS_MAIN;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
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
    LinearLayout layNetError, layBelumAdaDataIncome, layContainerBS, layIsiKategoriIncome, layTambahIncome, layTambahPengeluaran, layProgressLoadingAddOut;
    CardView btTambahIncome;
    RelativeLayout layIncome;
    BottomSheetDialog bsFragmentIncome;
    View bottomSheetView;
    EditText edtKategoriIncome, edtDeskripsiIncome, edtJmlIncome, edtJmlAkanDikeluarkan, edtHeadlineOut, edtDetailPengeluaran;
    TextView tvInfoStatusTambahOut, tvButtonTambah, tvJmlSisaInCome_Saatini, btTambahPengeluaran, tvGetMessageSuccesAddTambahKategori, txBatalkanSimpanIncome, tvInfoSumberPemasukanBS,btnSimpanIncome, btnBatalkanTambahIncome, btnBatalkanTambahOutcome, txNameUserTambahPengeluaran;
    ProgressBar loadingTambahKategoriIncome, loadingTambahIncome;
    ImageView ivPicUserTambahOut, imgCloseBSTambahOut;

    TextView txJmlSaldo, txJmlOut, txJmlTabungan, txJmlSedekah;

    Handler loadingsuccessaddincome, waiting, waitforlogout, waitTambahOut;
    public int counter;

    public static final String RETRIEVE_INFO_SALDO_AND_DATA_INCOME = "com.septi.mkk.RETRIEVE_INFO_SALDO_AND_DATA_INCOME";
    public static final String STOP_PB_LOADING = "com.septi.mkk.STOP_PB_LOADING";
    public static final String SHOW_MESSAGE_ERROR_NETWORK = "com.septi.mkk.SHOW_MESSAGE_ERROR_NETWORK";
    public static final String HIDE_MESSAGE_ERROR_NETWORK = "com.septi.mkk.HIDE_MESSAGE_ERROR_NETWORK";
    public static final String BERHASIL_TAMBAH_KATEGORI_INCOME = "com.septi.mkk.BERHASIL_TAMBAH_KATEGORI_INCOME";
    public static final String BERHASIL_TAMBAH_INCOME = "com.septi.mkk.BERHASIL_TAMBAH_INCOME";
    public static final String GAGAL_SIMPAN_KATEGORI_INCOME = "com.septi.mkk.GAGAL_SIMPAN_KATEGORI_INCOME";
    public static final String SUCCESS_TAMBAH_PEMASUKAN = "com.septi.mkk.SUCCESS_TAMBAH_PEMASUKAN";
    public static final String SUCCESS_TAMBAH_PENGELUARAN = "com.septi.mkk.SUCCESS_TAMBAH_PENGELUARAN";
    public static final String RELOAD_DASHBOARD = "com.septi.mkk.RELOAD_DASHBOARD";
    public static final String TAMBAH_PENGELUARAN = "com.septi.mkk.TAMBAH_PENGELUARAN";
    public static final String TRIGGER_SALDO_SHOW = "com.septi.mkk.TRIGGER_SALDO_SHOW";
    public static final String TRIGGER_LOADING_SHOW_EXECUTE_API_ADD_OUTCOME = "com.septi.mkk.TRIGGER_LOADING_SHOW_EXECUTE_API_ADD_OUTCOME";

    private final BroadcastReceiver broadcastSignalFragmentIncome = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case SUCCESS_TAMBAH_PENGELUARAN:
                    waitTambahOut = new Handler();
                    waitTambahOut.postDelayed(() -> {
                        layProgressLoadingAddOut.setVisibility(View.GONE);

                        tvInfoStatusTambahOut.setVisibility(View.VISIBLE);
                        tvInfoStatusTambahOut.setBackgroundResource(R.drawable.lbl_berhasil_simpan);
                        counter = 5;
                        new CountDownTimer(5000, 1000){
                            public void onTick(long millisUntilFinished){
                                tvInfoStatusTambahOut.setText("Berhasil disimpan (" + counter + ")");
                                counter--;
                            }
                            public void onFinish(){
                                enableBSInput_Out();
                                clearInputanOut();
                                bsFragmentIncome.dismiss();


                            }
                        }.start();
                    }, 5000);
                    break;
                case TRIGGER_LOADING_SHOW_EXECUTE_API_ADD_OUTCOME:
                    tvInfoStatusTambahOut.setVisibility(View.GONE);

                    disableBSInput_Out();
                    functions.hideKeyboard(bottomSheetView);

                    layProgressLoadingAddOut.setVisibility(View.VISIBLE);
                    layProgressLoadingAddOut.startAnimation(AnimationUtils.loadAnimation(contextFragmentIncome, R.anim.fade_in));
                    break;
                case TRIGGER_SALDO_SHOW:
                    /*
                     * Informasi Saldo
                     * */
                    int dikurangPengurangan = tinyDB.getInt("jmlSaldo") - tinyDB.getInt("JML_RUPIAH_OUT");
                    txJmlSaldo.setText("Rp. " + functions.decimal2Rp(String.valueOf(dikurangPengurangan)) + "-,");

                    /*
                     * Informasi Pengeluaran
                     * */
                    txJmlOut.setText("Rp. " + functions.decimal2Rp(String.valueOf(tinyDB.getInt("JML_RUPIAH_OUT"))) + "-,");
                    break;
                case TAMBAH_PENGELUARAN:

                    bsFragmentIncome.getBehavior().setDraggable(true);
                    bsFragmentIncome.setCancelable(false);
                    bsFragmentIncome.setContentView(bottomSheetView);
                    if (!((Activity) contextFragmentIncome).isFinishing()) {
                        bsFragmentIncome.show();
                    } else {
                        bsFragmentIncome.dismiss();
                    }
                    /* Status : Fixed (sementara)
                     * Issue #04062022
                     * Notes : Sementara, jika login logout dan kemudian mengklik item list income dan muncul bottomsheet seharusnya, disini jika switch dari 1 akun ke akun yang lain tanpa menutup apps setelah logout, maka akan force close, context main tidak diindentifikasi
                     * */

                    layTambahIncome.setVisibility(View.GONE);
                    layIsiKategoriIncome.setVisibility(View.GONE);
                    layTambahPengeluaran.setVisibility(View.VISIBLE);

                    tvInfoSumberPemasukanBS.setText("Source income : (" + tinyDB.getInt("ID_INCOME") + ") " + tinyDB.getString("KATEGORI_PEMASUKAN"));
                    tvJmlSisaInCome_Saatini.setText("Rp. " + functions.decimal2Rp(String.valueOf(tinyDB.getInt("JML_INCOME"))) + "-,");

                    if (PreferencesLogin.getGender(contextFragmentIncome).equals("Perempuan")) {
                        ivPicUserTambahOut.setImageResource(R.drawable.woman);
                    } else {
                        ivPicUserTambahOut.setImageResource(R.drawable.man);
                    }
                    txNameUserTambahPengeluaran.setText(PreferencesLogin.getNamaUser(contextFragmentIncome) + " (" + PreferencesLogin.getUIDUser(contextFragmentIncome) + ")");
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

                    try {
                        /* Status : Fixed (sementara)
                         * Issue #04062022
                         * java.lang.NullPointerException: Attempt to invoke virtual method 'android.content.Context androidx.fragment.app.FragmentActivity.getApplicationContext()' on a null object reference
                         * Notes: Sementara sudah aman dengan methode try & catch
                         * */
                        Drawable icSuccess = getActivity().getApplicationContext().getResources().getDrawable(R.drawable.ic_wait_2);
                        icSuccess.setBounds(0, 0, 60, 60);
                        tvGetMessageSuccesAddTambahKategori.setCompoundDrawables(icSuccess, null, null, null);
                    } catch (Exception error) {
                        error.printStackTrace();
                    }
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

                    txJmlItemIncome.setText(contextFragmentIncome.getString(R.string.total_data_income, tinyDB.getInt("JML_DATA_INCOME")));

                    retrieveDataIncome();

                    switch (txJmlItemIncome.getText().toString().substring(0, 1)) {
                        case "0":
                            layBelumAdaDataIncome.setVisibility(View.VISIBLE);
                            rvListDataMaintain.setVisibility(View.GONE);

                            txTitleInfoIncome.setText("Woapps!");
                            txMsgInfoIncome.setText(Html.fromHtml("Belum ada data <b>Pemasukan (Income)</b> saat ini, Tekan Tombol <b>+ Tambah Income</b> diatas untuk tambah data."));
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
                        case "ClientError":
                            layBelumAdaDataIncome.setVisibility(View.VISIBLE);

                            txInfoNetworkError.setText(msgErrorNet);
                            btReloadNetError.setText("Why?");

                            txTitleInfoIncome.setText("Aplikasi Bermasalah!");
                            txMsgInfoIncome.setText("Waduh!, Hubungi tim developer untuk meminta update aplikasi terbaru.");
                            break;
                        case "JSONExceptionError":

                            layBelumAdaDataIncome.setVisibility(View.VISIBLE);

                            txInfoNetworkError.setText(msgErrorNet);
                            btReloadNetError.setText("Why?");

                            txTitleInfoIncome.setText("Terjadi Masalah");
                            txMsgInfoIncome.setText("Booaam!, Terjadi masalah pada server database!");
                            break;
                        case "TimeoutError":

                            layBelumAdaDataIncome.setVisibility(View.VISIBLE);

                            txInfoNetworkError.setText(msgErrorNet);
                            btReloadNetError.setText("Reload");

                            txTitleInfoIncome.setText("Waktu Habis");
                            txMsgInfoIncome.setText("Waduh!, Gagal mengambil data, Aplikasi kehabisan waktu");
                            break;
                        case "NetworkError":
                            txInfoNetworkError.setText(msgErrorNet);
                            btReloadNetError.setText("Try");

                            txTitleInfoIncome.setText("Gagal Menyambungkan");
                            txMsgInfoIncome.setText("Woapss!, Aplikasi gagal menghubungkan ke server, Harap hubungi tim developer.");
                            break;
                        case "TokenAksesNotValid":
                            txInfoNetworkError.setText(msgErrorNet);
                            btReloadNetError.setText("Logout");

                            txTitleInfoIncome.setText("Akses Kadaluarsa");
                            txMsgInfoIncome.setText("Mohon maaf, Akses Anda kami Tolak!");
                            break;
                        case "DataPemasukanTidakDitemukan":
                            btTambahIncome.setClickable(true);

                            txInfoNetworkError.setText(msgErrorNet);
                            btReloadNetError.setText("Info");

                            layBelumAdaDataIncome.setVisibility(View.VISIBLE);

                            layNetError.setBackgroundResource(R.drawable.lbl_jumlah_saldo);
                            txInfoNetworkError.setCompoundDrawablesWithIntrinsicBounds(R.drawable.light_idea, 0, 0, 0);

                            txTitleInfoIncome.setText("Data tidak tersedia!");
                            txMsgInfoIncome.setText(Html.fromHtml("Belum ada data <b>Pemasukan (Income)</b> saat ini, Tekan Tombol <b>+ Tambah Income</b> diatas untuk tambah data."));
                            break;
                        case "DataPengeluaranTidakDitemukan":
                            btTambahIncome.setClickable(true);

                            txInfoNetworkError.setText(msgErrorNet);
                            btReloadNetError.setText("Info");

                            layNetError.setBackgroundResource(R.drawable.lbl_jumlah_saldo);
                            txInfoNetworkError.setCompoundDrawablesWithIntrinsicBounds(R.drawable.light_idea, 0, 0, 0);
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
        ArrayList<Integer> jmlSisaIncome = tinyDB.getListInt("ARR_JML_SISA_INC");
        ArrayList<Integer> jmlItemOut = tinyDB.getListInt("ARR_JML_ITEM_OUT");

        AdapterListIncome adapterListIncome = new AdapterListIncome(contextFragmentIncome,
                idIncome,
                userName,
                namaUser,
                jnsKelamin, hriIncome, tglIncome, jamIncome, katIncome, ketIncome, jmlIncome, jmlSisaIncome, jmlItemOut);
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
        filterBroadcast.addAction(SUCCESS_TAMBAH_PENGELUARAN);
        filterBroadcast.addAction(RELOAD_DASHBOARD);
        filterBroadcast.addAction(TRIGGER_SALDO_SHOW);
        filterBroadcast.addAction(TRIGGER_LOADING_SHOW_EXECUTE_API_ADD_OUTCOME);
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

        registerBroadcast();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_income, container, false);

        int sizeDaysInThisMonth = functions.getSizeDaysThisMonth(functions.getTahunInteger(), functions.getMonthInteger());

        /*
         * Init BottomSheet
         * */

        bsFragmentIncome = new BottomSheetDialog(contextFragmentIncome, R.style.BottomSheetDialogTheme);
        bottomSheetView = LayoutInflater.from(contextFragmentIncome.getApplicationContext()).inflate(R.layout.bs_fragment_income, getActivity().findViewById(R.id.bsFragmentIncomeContainer));

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

        loadingProgress = view.findViewById(R.id.progresLoadingIncome);
        layNetError = view.findViewById(R.id.layErrorNetworkIncome);
        layBelumAdaDataIncome = view.findViewById(R.id.layInfoBelumAdaData);

        edtKategoriIncome = bottomSheetView.findViewById(R.id.edtKategoriIncome);
        tvButtonTambah = bottomSheetView.findViewById(R.id.btnTambahKategoriIncome);
        tvGetMessageSuccesAddTambahKategori = bottomSheetView.findViewById(R.id.txGetMessageSucces);
        txBatalkanSimpanIncome = bottomSheetView.findViewById(R.id.txBatalkanProsesSimpanIncome);
        edtDeskripsiIncome = bottomSheetView.findViewById(R.id.etDeskripsiPemasukan);
        edtJmlIncome = bottomSheetView.findViewById(R.id.etJmlIncome);
        btnSimpanIncome = bottomSheetView.findViewById(R.id.btnTambahkanPemasukanNow);
        loadingTambahKategoriIncome = bottomSheetView.findViewById(R.id.progresLoadingTambahKategoriIncome);
        loadingTambahIncome = bottomSheetView.findViewById(R.id.progresLoadingTambahPemasukan);
        layContainerBS = bottomSheetView.findViewById(R.id.bsFragmentIncomeContainer);
        btnBatalkanTambahIncome = bottomSheetView.findViewById(R.id.btBatalkanTambahKategoriIncome);
        btnBatalkanTambahOutcome = bottomSheetView.findViewById(R.id.btBatalkanTambahPengeluaran);

        layIsiKategoriIncome = bottomSheetView.findViewById(R.id.layoutTambahKategoriIncome);
        layTambahIncome = bottomSheetView.findViewById(R.id.layoutTambahDataPemasukan);
        layTambahPengeluaran = bottomSheetView.findViewById(R.id.layoutTambahPengeluaran);

        ivPicUserTambahOut = bottomSheetView.findViewById(R.id.imgProfileTambahOut);
        txNameUserTambahPengeluaran = bottomSheetView.findViewById(R.id.txNameUserTambahOut);
        tvInfoSumberPemasukanBS = bottomSheetView.findViewById(R.id.txInfoSumberPemasukan);
        imgCloseBSTambahOut = bottomSheetView.findViewById(R.id.imgCloseBS_tambah_Out);
        tvJmlSisaInCome_Saatini = bottomSheetView.findViewById(R.id.txJmlSisaIncomeSaatIni);

        edtJmlAkanDikeluarkan = bottomSheetView.findViewById(R.id.etJmlAkanDikeluarkan);
        btTambahPengeluaran = bottomSheetView.findViewById(R.id.btnTambahPengeluaran);
        edtHeadlineOut = bottomSheetView.findViewById(R.id.txInputanHeadlineOut);
        edtDetailPengeluaran = bottomSheetView.findViewById(R.id.txDetailPengeluaran);

        layProgressLoadingAddOut = bottomSheetView.findViewById(R.id.layProgressLoadingTambahPengeluaran);
        tvInfoStatusTambahOut = bottomSheetView.findViewById(R.id.txInfoStatusTambahOut);

        cvBulanTahun.setText(functions.bulanTahun());
        functions.procedureGetInformasiSaldo();

        btTambahPengeluaran.setOnClickListener(v -> {

            Log.e("TYPEERROR", tinyDB.getString("TYPE_ERROR_NETWORK"));
            if (edtHeadlineOut.getText().toString() == "") {
                edtHeadlineOut.requestFocus();
                edtHeadlineOut.setError("Harap isi headline pengeluaran Anda!");
            } else if (edtDetailPengeluaran.getText().toString() == "") {
                edtDetailPengeluaran.requestFocus();
                edtDetailPengeluaran.setError("Harap isi detail pengeluaran Anda!");
            } else {
                LocalBroadcastManager.getInstance(contextFragmentIncome).sendBroadcast(new Intent(TRIGGER_LOADING_SHOW_EXECUTE_API_ADD_OUTCOME));
                functions.procedureTambahPengeluaran(tinyDB.getInt("ID_INCOME"), Integer.parseInt(PreferencesLogin.getUIDUser(contextFragmentIncome)), edtHeadlineOut.getText().toString(), edtDetailPengeluaran.getText().toString(), Integer.parseInt(edtJmlAkanDikeluarkan.getText().toString()));

                Log.e("INPUTAN_PENGELUARAN", "Id Income : " + tinyDB.getInt("ID_INCOME") + ", Id User : "
                        + PreferencesLogin.getUIDUser(contextFragmentIncome) + ", Hasil potong pengeluaran : " + tinyDB.getInt("HASIL_POTONGAN_DR_PENGELUARAN")
                        + ", Headline out : " + edtHeadlineOut.getText().toString() + ", Detail info pengeluaran : " + edtDetailPengeluaran.getText().toString()
                        + ", Jumlah rupiah out : " + edtJmlAkanDikeluarkan.getText().toString());
            }
        });

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
            } else if (btReloadNetError.getText().toString().equals("Info")) {
                Toast.makeText(contextFragmentIncome, "Silahkan tambahkan data income baru.", Toast.LENGTH_SHORT).show();
            } else if (btReloadNetError.getText().toString().equals("Why?")) {
                Toast.makeText(contextFragmentIncome, "Ada masalah!, harap hubungi tim developer.", Toast.LENGTH_LONG).show();
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

        imgCloseBSTambahOut.setOnClickListener(v -> {
            clearInputanOut();
            bsFragmentIncome.dismiss();
        });

        edtJmlAkanDikeluarkan.addTextChangedListener(new GenericTextWatcher(edtJmlAkanDikeluarkan));

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

    /*
     * Bookmark Here : Procedure textwatcher pada edittext
     * */
    public class GenericTextWatcher implements TextWatcher {
        private final View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence editable, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editext) {
            String text = editext.toString();
            switch (view.getId()) {
                case R.id.etJmlAkanDikeluarkan:
                    /*
                     * Notes : Jika inputan lebih dari sama dengan 3 digit, button enable & melakukan proses begitu sebaliknya
                     * */
                    if (text.length() >= 3) {
                        btTambahPengeluaran.setEnabled(true);
                        btTambahPengeluaran.setClickable(true);
                        btTambahPengeluaran.setBackgroundResource(R.drawable.lbl_jumlah_saldo);
                    } else {
                        btTambahPengeluaran.setEnabled(false);
                        btTambahPengeluaran.setClickable(false);
                        btTambahPengeluaran.setBackgroundResource(R.drawable.lbl_jumlah_saldo_nonactive);
                    }

                    /*
                     * Notes Ketentuan : Prosedure eksekusi dijalankan ketika ada inputan dari user
                     * 1. Jika text mengandung data string "", default menjadi 0
                     * 2. Jika pengeluaran melebihi data source pemasukan maka dinetralkan
                     * 3. Jika sudah memenuhi syarat maka melakukan pemotongan
                     * */

                    int potongAngka = 0;
                    int inputanEditText = 0;

                    if (text.equals("")) {
                        // Not action else
                    } else if (Integer.parseInt(text) > tinyDB.getInt("JML_INCOME")) {
                        edtJmlAkanDikeluarkan.setText("");
                        edtJmlAkanDikeluarkan.setError("Pengeluaran melebihi jumlah source pemasukan, harap sesuaikan!");
                    } else {
                        inputanEditText = Integer.parseInt(text);
                    }

                    potongAngka = tinyDB.getInt("JML_INCOME") - inputanEditText;

                    tinyDB.putInt("HASIL_POTONGAN_DR_PENGELUARAN", potongAngka);

                    tvJmlSisaInCome_Saatini.setText("Rp. " + functions.decimal2Rp(String.valueOf(potongAngka)) + "-,");
                    break;
            }
        }
    }

    private void clearInputanOut() {
        tvInfoStatusTambahOut.setVisibility(View.GONE);

        edtJmlAkanDikeluarkan.setText("");
        edtHeadlineOut.setText("");
        edtDetailPengeluaran.setText("");
    }

    private void disableBSInput_Out() {
        edtJmlAkanDikeluarkan.setEnabled(false);
        edtHeadlineOut.setEnabled(false);
        edtDetailPengeluaran.setEnabled(false);

        btTambahPengeluaran.setEnabled(false);
        btTambahPengeluaran.setClickable(false);
        btTambahPengeluaran.setBackgroundResource(R.drawable.lbl_jumlah_saldo_nonactive);
    }

    private void enableBSInput_Out() {
        edtJmlAkanDikeluarkan.setEnabled(true);
        edtHeadlineOut.setEnabled(true);
        edtDetailPengeluaran.setEnabled(true);

        btTambahPengeluaran.setEnabled(true);
        btTambahPengeluaran.setClickable(true);
        btTambahPengeluaran.setBackgroundResource(R.drawable.lbl_jumlah_saldo);
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
}