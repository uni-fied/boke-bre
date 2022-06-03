package com.septi.mkk.function;

public class Controller {

    /*
     * Endpoint API
     * */

    private static String ip_address            = "192.168.0.172";

    public static String localhost              = "http://" + ip_address + "/manajemen-keuangan/module/"; // Local
    public static String hosting                = "https://academydevlts.000webhostapp.com/api/mkk/v1/module/"; // Hosting

    public static String login                  = "auth/login.php";

    /*
     * Prosedure Kelola Pemasukan
     * */

    public static String data_income            = "income/data_income.php"; // Data Income
    public static String tambah_kat_pemasukan   = "income/tambah_sumber_income.php"; // Tambah Sumber Pemasukan
    public static String tambahkan_pemasukan    = "income/tambah_detail_pemasukan.php"; // Tambah Detail Pemasukan
    public static String hapus_sumber_pemasukan = "income/hapus_sumber_pemasukan.php"; // Hapus Sumber Pemasukan
    public static String hapus_item_pemasukan   = "income/hapus_itemlist_pemasukan.php"; // Hapus Item Pemasukan dalam List

    /*
     * Prosedure Kelola Pengeluaran
     * */



    /*
     * Versi & Build App
     * */

    public static String versionApp             = "V1.0";
    public static String buildVersion           = "Build 30042022.110910";

    /*
     * Timeout Size Time (Millisecond)
     * */

    public static int timeout_long              = 5000;

}
