package com.septi.mkk.function;

import static com.septi.mkk.DashboardActivity.FINISH_CLASS_MAIN;
import static com.septi.mkk.LoginActivity.FINISH_CLASS;
import static com.septi.mkk.LoginActivity.KOSONGKAN_INPUTAN_LOGIN;
import static com.septi.mkk.fragment.FragmentIncome.BERHASIL_TAMBAH_KATEGORI_INCOME;
import static com.septi.mkk.fragment.FragmentIncome.GAGAL_SIMPAN_KATEGORI_INCOME;
import static com.septi.mkk.fragment.FragmentIncome.HIDE_MESSAGE_ERROR_NETWORK;
import static com.septi.mkk.fragment.FragmentIncome.RELOAD_DASHBOARD;
import static com.septi.mkk.fragment.FragmentIncome.RETRIEVE_INFO_SALDO_AND_DATA_INCOME;
import static com.septi.mkk.fragment.FragmentIncome.SHOW_MESSAGE_ERROR_NETWORK;
import static com.septi.mkk.fragment.FragmentIncome.STOP_PB_LOADING;
import static com.septi.mkk.fragment.FragmentIncome.SUCCESS_TAMBAH_PEMASUKAN;
import static com.septi.mkk.fragment.FragmentIncome.SUCCESS_TAMBAH_PENGELUARAN;
import static com.septi.mkk.fragment.FragmentIncome.TRIGGER_SALDO_SHOW;
import static com.septi.mkk.fragment.FragmentOutcome.DATA_PENGELUARAN_BELUM_ADA;
import static com.septi.mkk.fragment.FragmentOutcome.RETRIEVE_LIST_PENGELUARAN;
import static com.septi.mkk.fragment.FragmentOutcome.SIGNAL_OUTCOME_CAN_NOT_RETRIEVE;
import static com.septi.mkk.fragment.FragmentOutcome.SIGNAL_SHOW_LOADING_PROGRESS;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.ClientError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.septi.mkk.DashboardActivity;
import com.septi.mkk.R;
import com.septi.mkk.SplashActivity;
import com.septi.mkk.library.TinyDB;
import com.septi.mkk.preferences.PreferencesLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Functions {

    Context context;
    RequestQueue requestQueue;
    ProgressDialog dialogLoading;
    Dialog customDialog;

    Handler waitAfterLogin;

    TinyDB tinyDB;

    String urlTambahOutcome = "not-switch", switchServer = "not-switch", urlDashboard = "not-set", urlDataPengeluaran = "not-set", urlLogin = "not-set", urlHapusItemPengeluaran = "not-set", urlTambahIncome = "not-set", urlTambahPemasukan = "not-set", urlHapusSumber = "not-set", urlHapusItemPemasukan = "not-set";

    public Functions(Context context) {
        this.context = context;
    }

    /*
     * Function Fragment Income
     * */

    /**********************************************************************************************************************************************/

    /* Added 02062022
     * Function : Fungsi Hapus Sumber Pemasukan
     * @FragmentIncome
     * @param : idPemasukan
     * */
    public void procedureHapusSumberIncome(String idPemasukan) {
        Log.e("EXECUTE_API", "Prosedure Hapus Sumber Pemasukan");

        tinyDB = new TinyDB(context);

        // Identify Server Access
        switchServer = tinyDB.getString("SWITCH_SERVER");
        switch (switchServer) {
            case "HOSTING":
                urlHapusSumber = Controller.hosting + Controller.hapus_sumber_pemasukan;
                break;
            default:
                urlHapusSumber = Controller.localhost + Controller.hapus_sumber_pemasukan;
                break;
        }

        showProgressDialog("Harap tunggu, sedang menghapus data sumber pemasukan...", false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlHapusSumber, response -> {
            try {
                JSONObject objRoot = new JSONObject(response);

                Log.e("Message", "Code : " + objRoot.getString("code"));
                Log.e("Message", "Status : " + objRoot.getBoolean("status"));
                Log.e("Message", "Pesan : " + objRoot.getString("message"));

                if (objRoot.getBoolean("status")) {
                    hideProgressDialog();
                    Toast.makeText(context, objRoot.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    hideProgressDialog();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                hideProgressDialog();
            }
        },
                error -> {

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(STOP_PB_LOADING));

                    hideProgressDialog();

                    error.printStackTrace();

                    if (error instanceof NetworkError) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(GAGAL_SIMPAN_KATEGORI_INCOME));
                        showMessageError("NetworkError", "Hapus Sumber Pemasukan Gagal! Ponsel Anda tidak dapat terhubung dengan Server, Pastikan Wifi / Internet pada Ponsel Anda Aktif!");
                    } else if (error instanceof TimeoutError) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(GAGAL_SIMPAN_KATEGORI_INCOME));
                        showMessageError("TimeoutError", "Hapus Sumber Pemasukan Gagal, Request Timeout! Periksa Jaringan Internet pada Ponsel Anda, Pastikan jaringan dulu!");
                    }

                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_pemasukan", idPemasukan);

                return params;
            }
        };

        stringRequest.setShouldCache(false);
        requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Controller.timeout_long, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    /* Added 07062022
     * Function : Fungsi Hapus Item Data Pengeluaran
     * @FragmentIncome
     * @param : idOutcome
     * */
    public void procedureHapusItemPengeluaran(int idOutcome) {
        Log.e("EXECUTE_API", "Prosedure Hapus data Item Pengeluaran");

        tinyDB = new TinyDB(context);

        // Identify Server Access
        switchServer = tinyDB.getString("SWITCH_SERVER");
        switch (switchServer) {
            case "HOSTING":
                urlHapusItemPengeluaran = Controller.hosting + Controller.hapus_item_pengeluaran;
                break;
            default:
                urlHapusItemPengeluaran = Controller.localhost + Controller.hapus_item_pengeluaran;
                break;
        }

        showProgressDialog("Harap tunggu, sedang menghapus data item pengeluaran...", false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlHapusItemPengeluaran, response -> {
            try {
                JSONObject objRoot = new JSONObject(response);

                Log.e("Message", "Code : " + objRoot.getString("code"));
                Log.e("Message", "Status : " + objRoot.getBoolean("status"));
                Log.e("Message", "Pesan : " + objRoot.getString("message"));

                if (objRoot.getBoolean("status")) {
                    hideProgressDialog();
                    Toast.makeText(context, objRoot.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    hideProgressDialog();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                hideProgressDialog();
            }
        },
                error -> {

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(STOP_PB_LOADING));

                    hideProgressDialog();

                    error.printStackTrace();

                    if (error instanceof NetworkError) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(GAGAL_SIMPAN_KATEGORI_INCOME));
                        showMessageError("NetworkError", "Hapus Sumber Pemasukan Gagal! Ponsel Anda tidak dapat terhubung dengan Server, Pastikan Wifi / Internet pada Ponsel Anda Aktif!");
                    } else if (error instanceof TimeoutError) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(GAGAL_SIMPAN_KATEGORI_INCOME));
                        showMessageError("TimeoutError", "Hapus Sumber Pemasukan Gagal, Request Timeout! Periksa Jaringan Internet pada Ponsel Anda, Pastikan jaringan dulu!");
                    }

                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_outcome", String.valueOf(idOutcome));

                return params;
            }
        };

        stringRequest.setShouldCache(false);
        requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Controller.timeout_long, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    /*
     * Function : Fungsi Tambah data Pemasukan
     * @FragmentIncome
     * @param : idPemasukan
     * @param : deskripsiIncome
     * @param : jmlIncome
     * */
    public void procedureTambahPemasukan(String idPemasukan, String deskripsiIncome, String jmlIncome) {
        Log.e("EXECUTE_API", "Prosedure Tambah Pemasukan");

        tinyDB = new TinyDB(context);

        // Identify Server Access
        switchServer = tinyDB.getString("SWITCH_SERVER");
        switch (switchServer) {
            case "HOSTING":
                urlTambahPemasukan = Controller.hosting + Controller.tambahkan_pemasukan;
                break;
            default:
                urlTambahPemasukan = Controller.localhost + Controller.tambahkan_pemasukan;
                break;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlTambahPemasukan, response -> {
            try {
                JSONObject objRoot = new JSONObject(response);

                Log.e("Message", "Code : " + objRoot.getString("code"));
                Log.e("Message", "Status : " + objRoot.getBoolean("status"));
                Log.e("Message", "Pesan : " + objRoot.getString("message"));

                if (objRoot.getBoolean("status")) {
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(SUCCESS_TAMBAH_PEMASUKAN));
                } else {

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        },
                error -> {

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(STOP_PB_LOADING));

                    error.printStackTrace();

                    if (error instanceof NetworkError) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(GAGAL_SIMPAN_KATEGORI_INCOME));
                        showMessageError("NetworkError", "Tambah Data Pemasukan Gagal! Ponsel Anda tidak dapat terhubung dengan Server, Pastikan Wifi / Internet pada Ponsel Anda Aktif!");
                    } else if (error instanceof TimeoutError) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(GAGAL_SIMPAN_KATEGORI_INCOME));
                        showMessageError("TimeoutError", "Tambah Data Pemasukan Gagal, Request Timeout! Periksa Jaringan Internet pada Ponsel Anda, Pastikan jaringan dulu!");
                    }

                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_pemasukan", idPemasukan);
                params.put("keterangan", deskripsiIncome);
                params.put("jumlah_income", jmlIncome);

                return params;
            }
        };

        stringRequest.setShouldCache(false);
        requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Controller.timeout_long, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    /* Added 07062022
     * Function : Fungsi Tambah data Pengeluaran
     * @FragmentIncome
     * @param : idIncome
     * @param : idUser
     * @param : headlineOut
     * @param : keteranganOut
     * @param : jmlOut
     * */
    public void procedureTambahPengeluaran(int idIncome, int idUser, String headlineOut, String keteranganOut, int jmlOut) {
        Log.e("EXECUTE_API", "Prosedure Tambah Pengeluaran");

        tinyDB = new TinyDB(context);

        // Identify Server Access
        switchServer = tinyDB.getString("SWITCH_SERVER");
        switch (switchServer) {
            case "HOSTING":
                urlTambahOutcome = Controller.hosting + Controller.tambahkan_pengeluaran;
                break;
            default:
                urlTambahOutcome = Controller.localhost + Controller.tambahkan_pengeluaran;
                break;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlTambahOutcome, response -> {
            try {
                JSONObject objRoot = new JSONObject(response);

                Log.e("Message", "Code : " + objRoot.getString("code"));
                Log.e("Message", "Status : " + objRoot.getBoolean("status"));
                Log.e("Message", "Pesan : " + objRoot.getString("message"));

                if (objRoot.getBoolean("status")) {
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(SUCCESS_TAMBAH_PENGELUARAN));
                } else {

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        },
                error -> {

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(STOP_PB_LOADING));

                    error.printStackTrace();

                    if (error instanceof NetworkError) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(GAGAL_SIMPAN_KATEGORI_INCOME));
                        showMessageError("NetworkError", "Tambah Data Pemasukan Gagal! Ponsel Anda tidak dapat terhubung dengan Server, Pastikan Wifi / Internet pada Ponsel Anda Aktif!");
                    } else if (error instanceof TimeoutError) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(GAGAL_SIMPAN_KATEGORI_INCOME));
                        showMessageError("TimeoutError", "Tambah Data Pemasukan Gagal, Request Timeout! Periksa Jaringan Internet pada Ponsel Anda, Pastikan jaringan dulu!");
                    }

                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_income", String.valueOf(idIncome));
                params.put("id_user", String.valueOf(idUser));
                params.put("kategori_out", headlineOut);
                params.put("detail_out", keteranganOut);
                params.put("jml_out", String.valueOf(jmlOut));
                params.put("waktu_outcome", convertMilisecond2ReadableFormat(System.currentTimeMillis()));

                return params;
            }
        };

        stringRequest.setShouldCache(false);
        requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Controller.timeout_long, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    /*
     * Bookmark Here : Fungsi Penambahan Kategori Income
     * @FragmentIncome
     * @param : userID
     * @param : kategoriPemasukan
     * */
    public void procedureAddCategoryIncome(String userID, String kategoriPemasukan) {
        Log.e("EXECUTE_API", "Prosedure Tambah Kategori Pemasukan");

        tinyDB = new TinyDB(context);

        // Identify Server Access
        switchServer = tinyDB.getString("SWITCH_SERVER");
        switch (switchServer) {
            case "HOSTING":
                urlTambahIncome = Controller.hosting + Controller.tambah_kat_pemasukan;
                break;
            default:
                urlTambahIncome = Controller.localhost + Controller.tambah_kat_pemasukan;
                break;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlTambahIncome, response -> {
            try {
                JSONObject objRoot = new JSONObject(response);

                Log.e("Message", "Code : " + objRoot.getString("code"));
                Log.e("Message", "Status : " + objRoot.getBoolean("status"));
                Log.e("Message", "Pesan : " + objRoot.getString("message"));

                if (objRoot.getBoolean("status")) {
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BERHASIL_TAMBAH_KATEGORI_INCOME));

                    JSONObject data_tambah_pemasukan = objRoot.getJSONObject("data_pemasukan");
                    String id_pemasukan = data_tambah_pemasukan.getString("id_pemasukan");
                    String pesan_response = objRoot.getString("message");

                    tinyDB.putString("ID_PEMASUKAN", id_pemasukan);
                    tinyDB.putString("GET_MESSAGE", pesan_response);

                } else {
                    // Fixme : Status Simpan Gagal / False
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        },
                error -> {

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(STOP_PB_LOADING));

                    error.printStackTrace();

                    if (error instanceof NetworkError) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(GAGAL_SIMPAN_KATEGORI_INCOME));
                        showMessageError("NetworkError", "Tambah Kategori Pemasukan Gagal! Ponsel Anda tidak dapat terhubung dengan Server, Pastikan Wifi / Internet pada Ponsel Anda Aktif!");
                    } else if (error instanceof TimeoutError) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(GAGAL_SIMPAN_KATEGORI_INCOME));
                        showMessageError("TimeoutError", "Tambah Kategori Pemasukan Gagal, Request Timeout! Periksa Jaringan Internet pada Ponsel Anda, Pastikan jaringan dulu!");
                    }

                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userid", userID);
                params.put("kategori_income", kategoriPemasukan);
                params.put("waktu_tambah", convertMilisecond2ReadableFormat(System.currentTimeMillis()));

                return params;
            }
        };

        stringRequest.setShouldCache(false);
        requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Controller.timeout_long, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }

    /* Added 02062022
     * Bookmark Here : Fungsi Penghapusan data Item Pemasukan pada List Income
     * @FragmentIncome
     * @param : idincome
     * */
    public void procedureDelItemPemasukan(int idincome) {
        Log.e("EXECUTE_API", "Prosedure Hapus data Income pada List");
        Log.e("ID_INCOME", String.valueOf(idincome));

        tinyDB = new TinyDB(context);

        // Identify Server Access
        switchServer = tinyDB.getString("SWITCH_SERVER");
        switch (switchServer) {
            case "HOSTING":
                urlHapusItemPemasukan = Controller.hosting + Controller.hapus_item_pemasukan;
                break;
            default:
                urlHapusItemPemasukan = Controller.localhost + Controller.hapus_item_pemasukan;
                break;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlHapusItemPemasukan, response -> {
            try {
                JSONObject objRoot = new JSONObject(response);

                Log.e("Message", "Code : " + objRoot.getString("code"));
                Log.e("Message", "Status : " + objRoot.getBoolean("status"));
                Log.e("Message", "Pesan : " + objRoot.getString("message"));

                if (objRoot.getString("code").equals("200")) {
                    Log.e("INFOHAPUSITEM", "Berhasil menghapus item pemasukan");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(RELOAD_DASHBOARD));
                } else {
                    Log.e("INFOHAPUSITEM", "Gagal menghapus item pemasukan");
                    Toast.makeText(context, "Gagal menghapus item pemasukan!", Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("MessageCatchException", "Exception is : " + e.getMessage());
            }
        },
                error -> {

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(STOP_PB_LOADING));

                    error.printStackTrace();

                    if (error instanceof NetworkError) {
                        showMessageError("NetworkError", "Ponsel Anda tidak dapat terhubung dengan Server dan Pastikan Wifi / Internet pada Ponsel Anda Aktif!, Jika masih bermasalah hubungi Developer");
                    } else if (error instanceof TimeoutError) {
                        showMessageError("TimeoutError", "Request Timeout! Periksa Jaringan Internet pada Ponsel Anda, Kemudian tekan Reload");
                    }

                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_income", String.valueOf(idincome));

                return params;
            }
        };

        stringRequest.setShouldCache(false);
        requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Controller.timeout_long, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    /*
     * Function : Fungsi Pengambilan Informasi Saldo
     * @FragmentIncome
     * */
    public void procedureGetInformasiSaldo() {
        Log.e("EXECUTE_API", "Prosedure Get Informasi Saldo");

        tinyDB = new TinyDB(context);

        // Identify Server Access
        switchServer = tinyDB.getString("SWITCH_SERVER");
        switch (switchServer) {
            case "HOSTING":
                urlDashboard = Controller.hosting + Controller.data_income;
                break;
            default:
                urlDashboard = Controller.localhost + Controller.data_income;
                break;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlDashboard, response -> {
            try {
                JSONObject objRoot = new JSONObject(response);

                Log.e("Message", "Code : " + objRoot.getString("code"));
                Log.e("Message", "Status : " + objRoot.getBoolean("status"));
                Log.e("Message", "Pesan : " + objRoot.getString("message"));

                if (objRoot.getString("code").equals("500")) {
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(STOP_PB_LOADING));

                    showMessageError("TokenAksesNotValid", "Silahkan Anda melakukan Login ulang terhadap Aplikasi, untuk Meregenerasi Token, Jika pesan ini masih tetap muncul, tutup kemudian buka kembali apps");
                } else if (objRoot.getString("code").equals("404")) {
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(STOP_PB_LOADING));

                    showMessageError("DataPemasukanTidakDitemukan", objRoot.getString("message"));

                    procedureListOutcomeData(getMonth(), String.valueOf(getTahunInteger()));
                } else {
                    if (objRoot.getBoolean("status")) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(HIDE_MESSAGE_ERROR_NETWORK));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(STOP_PB_LOADING));

                        /*
                         * Bookmark Here : Pengambilan Data Objek Informasi Saldo
                         * */
                        JSONObject value = objRoot.getJSONObject("informasi_saldo");

                        int jmlSaldo = value.getInt("jml_saldo");
                        int jmlOut = value.getInt("jml_pengeluaran");
                        int jmlTabungan = value.getInt("jml_tabungan");
                        int jmlSedekah = value.getInt("jml_budget_sedekah");

                        tinyDB.putInt("jmlSaldo", jmlSaldo);
                        tinyDB.putInt("jmlOut", jmlOut);
                        tinyDB.putInt("jmlTabungan", jmlTabungan);
                        tinyDB.putInt("jmlSedekah", jmlSedekah);

                        ArrayList<Integer> idIncome = new ArrayList<>();
                        ArrayList<String> userNameGet = new ArrayList<>();
                        ArrayList<String> namaUserGet = new ArrayList<>();
                        ArrayList<String> jenisKelaminGet = new ArrayList<>();
                        ArrayList<String> hariTambahGet = new ArrayList<>();
                        ArrayList<String> tgltambahGet = new ArrayList<>();
                        ArrayList<String> jamTambahGet = new ArrayList<>();
                        ArrayList<String> kategoriDataGet = new ArrayList<>();
                        ArrayList<String> detailDataGet = new ArrayList<>();
                        ArrayList<Integer> jmlIncomeGet = new ArrayList<>();
                        ArrayList<Integer> jmlSisaIncomeGet = new ArrayList<>();
                        ArrayList<Integer> jmlItemOut = new ArrayList<>();

                        JSONArray dataListIncome = new JSONArray(objRoot.getString("data_income"));

                        if (dataListIncome.equals("[]")) {
                            tinyDB.putInt("JML_DATA_INCOME", 0);

                        } else { // Data Pemasukan Ada
                            for (int i = 0; i < dataListIncome.length(); i++) {
                                JSONObject objDataListIncome = dataListIncome.getJSONObject(i);

                                idIncome.add(Integer.valueOf(objDataListIncome.getString("id_income")));
                                userNameGet.add(objDataListIncome.getString("username"));
                                namaUserGet.add(objDataListIncome.getString("nama_user"));
                                jenisKelaminGet.add(objDataListIncome.getString("gender"));
                                hariTambahGet.add(objDataListIncome.getString("hari_tambah"));
                                tgltambahGet.add(objDataListIncome.getString("tgl_tambah_income"));
                                jamTambahGet.add(objDataListIncome.getString("waktu_tambah"));
                                kategoriDataGet.add(objDataListIncome.getString("kategori_inc"));
                                detailDataGet.add(objDataListIncome.getString("keterangan"));
                                jmlIncomeGet.add(Integer.valueOf(objDataListIncome.getString("jumlah_income")));
                                jmlSisaIncomeGet.add(Integer.valueOf(objDataListIncome.getString("jml_sisa_income")));
                                jmlItemOut.add(Integer.valueOf(objDataListIncome.getString("jml_item_out")));
                            }

                            tinyDB.putInt("JML_DATA_INCOME", idIncome.size());
                        }

                        tinyDB.putListInt("ARR_ID_ICM", idIncome);
                        tinyDB.putListString("ARR_USR_NM", userNameGet);
                        tinyDB.putListString("ARR_NM_USR", namaUserGet);
                        tinyDB.putListString("ARR_JNS_KLMN", jenisKelaminGet);
                        tinyDB.putListString("ARR_HARI_TBH", hariTambahGet);
                        tinyDB.putListString("ARR_TGL_TBH", tgltambahGet);
                        tinyDB.putListString("ARR_JAM_TBH", jamTambahGet);
                        tinyDB.putListString("ARR_CAT_INC", kategoriDataGet);
                        tinyDB.putListString("ARR_KETERANGAN", detailDataGet);
                        tinyDB.putListInt("ARR_JML_INC", jmlIncomeGet);
                        tinyDB.putListInt("ARR_JML_SISA_INC", jmlSisaIncomeGet);
                        tinyDB.putListInt("ARR_JML_ITEM_OUT", jmlItemOut);

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(RETRIEVE_INFO_SALDO_AND_DATA_INCOME));

                        procedureListOutcomeData(getMonth(), String.valueOf(getTahunInteger()));

                    } else {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(STOP_PB_LOADING));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(SIGNAL_OUTCOME_CAN_NOT_RETRIEVE));

                        showMessageError("ErrorResponse", "Terdapat Masalah!, " + objRoot.getString("message"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();

                /*
                 * Issue #07062022
                 * Case ketika Apache2 dihidupkan sedang MySQL mati, dan melakukan request, mendapatkan pesan error berikut
                 * W/System.err: org.json.JSONException: Value <br of type java.lang.String cannot be converted to JSONObject
                 * Login dengan : Log.e("MessageCatchException", "Exception is : " + e.getMessage());
                 * */
                if (e.getMessage().contains("Value <br")) {
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(STOP_PB_LOADING));
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(SIGNAL_OUTCOME_CAN_NOT_RETRIEVE));
                    showMessageError("JSONExceptionError", "Terjadi masalah pada server database, Harap hubungu tim developer!");
                } else {
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(STOP_PB_LOADING));
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(SIGNAL_OUTCOME_CAN_NOT_RETRIEVE));
                    showMessageError("JSONExceptionError", e.getMessage() + " - (Cek disini code 404 Crash)");
                }
            }
        },
                error -> {

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(STOP_PB_LOADING));
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(SIGNAL_OUTCOME_CAN_NOT_RETRIEVE));

                    error.printStackTrace();

                    if (error instanceof NetworkError) {
                        showMessageError("NetworkError", "Ponsel Anda tidak dapat terhubung dengan Server dan Pastikan Wifi / Internet pada Ponsel Anda Aktif!, Jika masih bermasalah hubungi Developer. (NetworkError)");
                    } else if (error instanceof TimeoutError) {
                        showMessageError("TimeoutError", "Request Timeout! Periksa Jaringan Internet pada Ponsel Anda, Kemudian tekan Reload. (TimeoutError)");
                    } else if (error instanceof ClientError) {
                        showMessageError("ClientError", "Terdapat kesalahan dalam request terhadap server, Harap hubungi tim developer. (ClientError)");
                    }

                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userid", PreferencesLogin.getUIDUser(context));
                params.put("token_akses_dashboard", PreferencesLogin.getAkunToken(context));
                params.put("this_month", getMonth());
                params.put("init_year", String.valueOf(getTahunInteger()));

                return params;
            }
        };

        stringRequest.setShouldCache(false);
        requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Controller.timeout_long, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    /**********************************************************************************************************************************************/

    /*
     * Function Aktivitas Login
     * */

    /*
     * Function : Fungsi Login dengan Authentikasi Username & Password
     * @Login
     *
     * @param : username
     * @param : password
     * @param : logAccess (Waktu Akses)
     * */
    public void procedureLogin(String username, String password, String logAccess) {
        Log.e("EXECUTE_API", "Prosedure Login Apps");

        tinyDB = new TinyDB(context);

        showProgressDialog("Mohon Tunggu, Sedang Memproses Login Akun Anda...", false);

        // Identify Server Access
        switchServer = tinyDB.getString("SWITCH_SERVER");
        switch (switchServer) {
            case "HOSTING":
                urlLogin = Controller.hosting + Controller.login;
                break;
            default:
                urlLogin = Controller.localhost + Controller.login;
                break;
        }

        Log.e("URL_LOGIN", urlLogin);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlLogin,
                response -> {
            Log.e("responseerr", response);
                    try {
                        JSONObject objRoot = new JSONObject(response);

                        Log.e("Message", "Code : " + objRoot.getString("code"));
                        Log.e("Message", "Status : " + objRoot.getBoolean("status"));
                        Log.e("Message", "Pesan : " + objRoot.getString("message"));

                        if (objRoot.getBoolean("status")) {
                            hideProgressDialog();

                            JSONArray dataAkun = new JSONArray(objRoot.getString("data_akun"));
                            for (int i = 0; i < dataAkun.length(); i++) {
                                JSONObject objDataAkun = dataAkun.getJSONObject(i);

                                String iduser = objDataAkun.getString("id_user");
                                String kode_user = objDataAkun.getString("kode_user");
                                String namauser = objDataAkun.getString("nama_user");
                                String gender = objDataAkun.getString("gender");
                                String aka = objDataAkun.getString("alias");
                                String usernames = objDataAkun.getString("username");
                                String email = objDataAkun.getString("email");
                                String registered = objDataAkun.getString("tanggal_terdaftar");
                                String token_login = objDataAkun.getString("token_akses_login");

                                PreferencesLogin.setUIDUser(context, iduser);
                                PreferencesLogin.setKodeUser(context, kode_user);
                                PreferencesLogin.setNamaUser(context, namauser);
                                PreferencesLogin.setGender(context, gender);
                                PreferencesLogin.setAlias(context, aka);
                                PreferencesLogin.setUsername(context, usernames);
                                PreferencesLogin.setEmail(context, email);
                                PreferencesLogin.setRegistered(context, registered);
                                PreferencesLogin.setAkunToken(context, token_login);
                            }

                            showProgressDialog(objRoot.getString("message"), false);
                            waitAfterLogin = new Handler();
                            waitAfterLogin.postDelayed(() -> {
                                hideProgressDialog();

                                PreferencesLogin.setStatusLogin(context, "true");

                                Intent goToDashboard = new Intent(context, DashboardActivity.class);
                                context.startActivity(goToDashboard);
                                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(FINISH_CLASS));
                            }, 3000);
                        } else {
                            hideProgressDialog();

                            customDialog(2, "User Restricted!", objRoot.getString("message"), "Tutup", false);
                        }
                    } catch (JSONException e) {
                        hideProgressDialog();

                        e.printStackTrace();
                        Log.e("MessageCatchException", "Exception is : " + e.getMessage());
                    }
                },
                error -> {
                    hideProgressDialog();
                    error.printStackTrace();

                    if (error instanceof NetworkError) {
                        customDialog(1, "Terjadi Masalah!", error.getCause().getMessage() + ", Silahkan periksa akses server, jaringan internet atau data pada ponsel Anda!", "Mengerti", false);
                    } else if (error instanceof TimeoutError) {
                        customDialog(4, "Timeout Request!", "Silahkan periksa jaringan internet atau data pada ponsel Anda!", "Mengerti", false);
                    }

                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("waktu_akses", logAccess);
                params.put("versi_apps", Controller.versionApp);
                params.put("build_version", Controller.buildVersion);
                params.put("aksi", "Login");
                return params;
            }
        };

        stringRequest.setShouldCache(false);
        requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Controller.timeout_long, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    /**********************************************************************************************************************************************/

    /*
     * Function Fragment Outcome
     * */

    /*
     * Function : Fungsi Pengambilan Informasi Data Pengeluaran
     * @FragmentOutcome
     * */
    public void procedureListOutcomeData(String switchMonth, String switchYear) {
        Log.e("EXECUTE_API", "Prosedure Get List Pengeluaran, Dieksekusi ketika pengambilan data income berhasil");

        tinyDB = new TinyDB(context);

        // Identify Server Access
        switchServer = tinyDB.getString("SWITCH_SERVER");
        switch (switchServer) {
            case "HOSTING":
                urlDataPengeluaran = Controller.hosting + Controller.data_pengeluaran;
                break;
            default:
                urlDataPengeluaran = Controller.localhost + Controller.data_pengeluaran;
                break;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlDataPengeluaran, response -> {
            try {
                JSONObject objRoot = new JSONObject(response);

                Log.e("Message", "Code : " + objRoot.getString("code"));
                Log.e("Message", "Status : " + objRoot.getBoolean("status"));
                Log.e("Message", "Pesan : " + objRoot.getString("message"));

                if (objRoot.getString("code").equals("404")) {
                    tinyDB.putInt("JML_RUPIAH_OUT", 0);

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(STOP_PB_LOADING));
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(TRIGGER_SALDO_SHOW));

                    tinyDB.putString("MESSAGE_PENGELUARAN", objRoot.getString("message"));
                    tinyDB.putString("TITLE_PENGELUARAN", "Belum ada Pengeluaran");
                    tinyDB.putInt("JML_PENGELUARAN", 0);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(DATA_PENGELUARAN_BELUM_ADA));

                } else {
                    if (objRoot.getBoolean("status")) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(HIDE_MESSAGE_ERROR_NETWORK));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(STOP_PB_LOADING));

                        /*
                         * Bookmark Here : Pengambilan Data Objek Informasi Pengeluaran
                         * */

                        String infoJmlDataPengeluaran = objRoot.getString("jml_item_outcome");
                        int jmlRupiahPengeluaran = objRoot.getInt("jumlah_pengeluaran");

                        tinyDB.putString("INFO_JML_ITEM", infoJmlDataPengeluaran);
                        tinyDB.putInt("JML_RUPIAH_OUT", jmlRupiahPengeluaran);

                        ArrayList<Integer> idOutcome = new ArrayList<>();
                        ArrayList<Integer> idDetailPengeluaran = new ArrayList<>();
                        ArrayList<String> kategoriPengeluaran = new ArrayList<>();
                        ArrayList<Integer> idSumberIncome = new ArrayList<>();
                        ArrayList<String> sourceTittle = new ArrayList<>();
                        ArrayList<Integer> idUserOuter = new ArrayList<>();
                        ArrayList<String> nameOfUser = new ArrayList<>();
                        ArrayList<String> genderUser = new ArrayList<>();
                        ArrayList<String> tglPengeluaran = new ArrayList<>();
                        ArrayList<String> jamPengeluaran = new ArrayList<>();
                        ArrayList<String> detailPengeluaran = new ArrayList<>();
                        ArrayList<Integer> rupiahPengeluaran = new ArrayList<>();

                        JSONArray dataListOutcome = new JSONArray(objRoot.getString("data_outcome"));

                        if (dataListOutcome.equals("[]")) {
                            tinyDB.putInt("INFO_JML_ITEM", 0);
                        } else {
                            for (int i = 0; i < dataListOutcome.length(); i++) {
                                JSONObject objDataListOutcome = dataListOutcome.getJSONObject(i);

                                idOutcome.add(Integer.valueOf(objDataListOutcome.getString("id_outcome")));
                                idDetailPengeluaran.add(Integer.valueOf(objDataListOutcome.getString("id_pengeluaran")));
                                kategoriPengeluaran.add(objDataListOutcome.getString("kategori_out"));
                                idSumberIncome.add(Integer.valueOf(objDataListOutcome.getString("id_sumber")));
                                sourceTittle.add(objDataListOutcome.getString("source_income"));
                                idUserOuter.add(Integer.valueOf(objDataListOutcome.getString("id_user")));
                                nameOfUser.add(objDataListOutcome.getString("dikeluarkan_oleh"));
                                genderUser.add(objDataListOutcome.getString("gender"));
                                tglPengeluaran.add(objDataListOutcome.getString("tanggal_pengeluaran"));
                                jamPengeluaran.add(objDataListOutcome.getString("jam_pengeluaran"));
                                detailPengeluaran.add(objDataListOutcome.getString("keterangan"));
                                rupiahPengeluaran.add(Integer.valueOf(objDataListOutcome.getString("jumlah_outcome")));
                            }
                        }

                        tinyDB.putListInt("ARR_ID_OUT", idOutcome);
                        tinyDB.putListInt("ARR_ID_PENGELUARAN", idDetailPengeluaran);
                        tinyDB.putListString("ARR_CAT_OUT", kategoriPengeluaran);
                        tinyDB.putListInt("ARR_ID_SOURCE_IN", idSumberIncome);
                        tinyDB.putListString("ARR_TITLE_SOURCE_IN", sourceTittle);
                        tinyDB.putListInt("ARR_ID_USER", idUserOuter);
                        tinyDB.putListString("ARR_NAME_USER", nameOfUser);
                        tinyDB.putListString("ARR_JK_USER", genderUser);
                        tinyDB.putListString("ARR_TGL_OUT", tglPengeluaran);
                        tinyDB.putListString("ARR_JAM_OUT", jamPengeluaran);
                        tinyDB.putListString("ARR_DETAIL_OUT", detailPengeluaran);
                        tinyDB.putListInt("ARR_RP_JML_OUT", rupiahPengeluaran);

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(SIGNAL_SHOW_LOADING_PROGRESS));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(RETRIEVE_LIST_PENGELUARAN));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(TRIGGER_SALDO_SHOW));

                    } else {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(STOP_PB_LOADING));

                        showMessageError("ErrorResponse", "Terdapat Masalah!, " + objRoot.getString("message"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("MessageCatchException", "Exception is : " + e.getMessage());
            }
        },
                error -> {

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(STOP_PB_LOADING));

                    error.printStackTrace();

                    if (error instanceof NetworkError) {
                        showMessageError("NetworkError", "Ponsel Anda tidak dapat terhubung dengan Server dan Pastikan Wifi / Internet pada Ponsel Anda Aktif!, Jika masih bermasalah hubungi Developer");
                    } else if (error instanceof TimeoutError) {
                        showMessageError("TimeoutError", "Request Timeout! Periksa Jaringan Internet pada Ponsel Anda, Kemudian tekan Reload");
                    }

                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("this_month", switchMonth);
                params.put("init_year", switchYear);

                return params;
            }
        };

        stringRequest.setShouldCache(false);
        requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Controller.timeout_long, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    /**********************************************************************************************************************************************/

    /*
     * Function : Fungsi Hide Keyboard Virtual Android
     * @param : view
     * */
    public void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } else {
            Toast.makeText(context, "View undifined!, Check your view.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Function : Fungsi Check Internet pada Ponsel
     * Return : True or False
     * */
    public boolean checkInet() {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        boolean connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
        return connected;
    }

    /*
     * Function : Fungsi Menampilkan Dialog Progress dengan Pesan didalamnya
     * @param : message
     * @param : cancelable (true or false)
     * */
    public void showProgressDialog(String message, boolean cancelable) {
        dialogLoading = new ProgressDialog(context);
        dialogLoading.setCancelable(cancelable);
        dialogLoading.setMessage(message);

        /* Status : Fixed (sementara)
         * Issue : 040602022
         * Menggunakan prosedure pengecekan ini tidak efektif
         * Cause : android.view.WindowManager$BadTokenException: Unable to add window -- token android.os.BinderProxy@e0365dc is not valid; is your activity running?
         * Notes : Sementara di ubah methode pengecekan seperti dibawahnya
         * */
        try {
            if (dialogLoading.isShowing()) {
                dialogLoading.dismiss();
            } else {
                dialogLoading.show();
            }
        } catch (WindowManager.BadTokenException error) {
            error.printStackTrace();
        }
    }

    /*
     * Function : Fungsi Hide Keyboard
     * Pengecekan Jika Masih Muncul dan Tidak
     * */
    public void hideProgressDialog() {
        try {
            if (dialogLoading.isShowing()) {
                dialogLoading.dismiss();
            } else {
                dialogLoading.show();
            }
        } catch (WindowManager.BadTokenException error) {
            error.printStackTrace();
        }
    }

    /*
     * Function : Inititate Error Handling jika didalam Fragment (tanpa dialog) show message
     * @param : typeErrorNet
     * @param : msgErrorNet
     * */
    private void showMessageError(String typeErrorNet, String msgErrorNet) {
        tinyDB.putString("TYPE_ERROR_NETWORK", typeErrorNet);
        tinyDB.putString("MSG_ERROR_NET", msgErrorNet);

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(SHOW_MESSAGE_ERROR_NETWORK));
    }

    /*
     * Function : Custome Dialog Popup for Error Handling (Just Activity, not Recomended for Fragment)
     * */
    public void customDialog(int type, String titledialog, String message, String buttontext, boolean cancelable) {
        customDialog = new Dialog(context);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.setContentView(R.layout.popup_dialog_custome);
        customDialog.setCancelable(cancelable);
        customDialog.setCanceledOnTouchOutside(false);

        ImageView ivGambar = customDialog.findViewById(R.id.imgInformation);
        TextView txMessage = customDialog.findViewById(R.id.tvMessageDialog);
        TextView txHeader = customDialog.findViewById(R.id.txHeaderDialog);
        TextView txButton = customDialog.findViewById(R.id.tvTextButtonDialog);
        LinearLayout btnAction = customDialog.findViewById(R.id.btnActionButtonDialog);

        switch (type) {
            case 1: // Failed to connect server, domain / ip
                txHeader.setText(titledialog);
                txMessage.setText(message);
                txButton.setText(buttontext);
                ivGambar.setImageResource(R.drawable.network_not_connected);
                break;
            case 2: // Data user tidak terdaftar di database
                txHeader.setText(titledialog);
                txMessage.setText(message);
                txButton.setText(buttontext);
                ivGambar.setImageResource(R.drawable.restricted_user);
                break;
            case 3: // Keluar Aplikasi / Logout
                txHeader.setText(titledialog);
                txMessage.setText(message);
                txButton.setText(buttontext);
                ivGambar.setImageResource(R.drawable.exit);
                break;
            case 4: // Request Timeout
                txHeader.setText(titledialog);
                txMessage.setText(message);
                txButton.setText(buttontext);
                ivGambar.setImageResource(R.drawable.timeout);
                break;
        }

        btnAction.setOnClickListener(view -> {

            if (!checkInet()) {
                Toast.makeText(context, "Pastikan ponsel Anda mengakses Internet!", Toast.LENGTH_SHORT).show();
            } else {
                switch (type) {
                    case 1:
                        customDialog.dismiss();
                        break;
                    case 4:
                    case 2:
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(KOSONGKAN_INPUTAN_LOGIN));
                        customDialog.dismiss();
                        break;
                    case 3:
                        PreferencesLogin.clearPreferencesLogin(context);
                        customDialog.dismiss();
                        context.startActivity(new Intent(context, SplashActivity.class));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(FINISH_CLASS_MAIN));
                        break;
                }
            }
        });

        hideCustomeDialog();
    }

    /*
     * Function : Hide Custome Dialog
     * Notes : Dengan Pengecekan Jika masih Muncul
     * */
    private void hideCustomeDialog() {
        if (customDialog.isShowing()) {
            customDialog.dismiss();
        } else {
            customDialog.show();
        }
    }

    /*
     * Function : Support System Environment
     * */

    /*
     * Notes : Mengambil hasil convert by time milli seconds bulan dan tahun ex : May 2022
     * Return : tanggal
     * */
    public String bulanTahun() {
        SimpleDateFormat formatingDate = new SimpleDateFormat("MMMM yyyy");
        String tanggal = formatingDate.format(System.currentTimeMillis());
        return tanggal;
    }

    /*
     * Notes : Mengambil bulan ex : January, February dst.
     * Return : parseSwitch
     * */
    public String getMonth() {
        SimpleDateFormat formatingDate = new SimpleDateFormat("MMMM");
        String switchMonth = formatingDate.format(System.currentTimeMillis());

        Log.e("MONTH_SWITCH", switchMonth);

        String parseSwitch = "";
        if (switchMonth.contains("June") || switchMonth.contains("Juni")) {
            parseSwitch = "June";
        } else if (switchMonth.contains("July") || switchMonth.contains("Juli")) {
            parseSwitch = "July";
        } else if (switchMonth.contains("August") || switchMonth.contains("Agustus")) {
            parseSwitch = "August";
        } else if (switchMonth.contains("October") || switchMonth.contains("Oktober")) {
            parseSwitch = "October";
        } else if (switchMonth.contains("December") || switchMonth.contains("Desember")) {
            parseSwitch = "December";
        } else if (switchMonth.contains("January") || switchMonth.contains("Januari")) {
            parseSwitch = "January";
        } else if (switchMonth.contains("February") || switchMonth.contains("Februari")) {
            parseSwitch = "February";
        } else if (switchMonth.contains("March") || switchMonth.contains("Maret")) {
            parseSwitch = "March";
        }

        return parseSwitch;
    }

    /*
     * Notes : Mengambil tahun ex : 2021, 2022 dst.
     * Return : Integer.valueOf(tahun)
     * */
    public int getTahunInteger() {
        SimpleDateFormat formatingDate = new SimpleDateFormat("yyyy");
        String tahun = formatingDate.format(System.currentTimeMillis());
        return Integer.valueOf(tahun);
    }

    /*
     * Notes : Mengambil bulan dalam bentuk integer ex : 1 = January, 2 = February dst.
     * Return : mountInteger
     * */
    public int getMonthInteger() {
        Integer mountInteger = 0;
        Date date = new Date();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) { // 26 +
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int month = localDate.getMonthValue();
            mountInteger = month;
        }
        return mountInteger;
    }

    /*
     * Notes : Mengambil jumlah hari pada bulan ini ex : January = 30, February = 28 dst.
     * @param : year (integer)
     * @param : month (integer)
     * */
    public int getSizeDaysThisMonth(int year, int month) {
        int getSizeDays = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) { // 26 +
            YearMonth yearMonthObject = YearMonth.of(year, month);
            getSizeDays = yearMonthObject.lengthOfMonth();
            return getSizeDays;
        }
        return getSizeDays;
    }

    /*
     * Notes : Konvert integer ke rupiah format ex : 1000 menjadi 1,000,00
     * @param : target
     * */
    public String decimal2Rp(String target) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(target));
    }

    /*
     * Notes : Konvert time milli second to readable format
     * @param : currentTimeMillis
     *
     * Reformat to 2022-05-02 22:10:24
     * */
    public String convertMilisecond2ReadableFormat(long currentTimeMillis) {
        System.out.println("Epoch Milliseconds : " + currentTimeMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date resultdate = new Date(currentTimeMillis);
        System.out.println("Result Convert : " + sdf.format(resultdate));
        return sdf.format(resultdate);
    }

    public String getGMTtimezone() {
        TimeZone timezone = TimeZone.getDefault();
        String currentGMT = (TimeZone.getTimeZone(timezone.getID()).getDisplayName(false, TimeZone.SHORT));
        return  currentGMT;
    }

    public String getTimeZoneArea() {
        TimeZone timezone = TimeZone.getDefault();
        String currentAreaTimeZone = timezone.getID();
        return  currentAreaTimeZone;
    }
}
