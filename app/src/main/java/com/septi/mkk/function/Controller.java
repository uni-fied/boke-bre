package com.septi.mkk.function;

public class Controller {

    /*
     * Endpoint API
     * */

    private static String ip_address            = "your-ip";
    private static String domain                = "journalcode.atwebpages.com";

    public static String localhost              = "http://" + ip_address + "/manajemen-keuangan/module/"; // Local
    public static String hosting                = "http://" + domain + "/api/manajemen-keuangan/module/"; // Hosting

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

    public static String data_pengeluaran       = "outcome/data_outcome.php"; // Data Outcome
    public static String tambahkan_pengeluaran  = "outcome/tambah_data_pengeluaran.php"; // Tambah Data Outcome
    public static String hapus_item_pengeluaran = "outcome/hapus_item_pengeluaran.php"; // Hapus Item Data Outcome

    /*
     * Versi & Build App
     * */

    public static String versionApp             = "V1.0 BETA RELEASE";
    public static String buildVersion           = "Build 07062022.0935.1";

    /*
     * Timeout Size Time (Millisecond)
     * */

    public static int timeout_long              = 60000;

}
