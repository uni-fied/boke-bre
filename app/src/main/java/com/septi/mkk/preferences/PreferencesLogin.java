package com.septi.mkk.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesLogin {

    static final String UID_USER                 = "uid";
    static final String KODE_USER                = "kode_user";
    static final String NAMA_USER                = "nama_user";
    static final String USERNAME                 = "username";
    static final String GENDER                   = "gender";
    static final String ALIAS                    = "alias";
    static final String EMAIL                    = "email";
    static final String REGISTERED               = "registered";
    static final String STATUS_LOGIN             = "false";
    static final String TOKEN_LOGIN_AKUN         = "token";

    private static SharedPreferences getSharedPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /*
     * Added : Id User
     * */

    public static void setUIDUser(Context context, String uid) {
        SharedPreferences.Editor editor =  getSharedPreference(context).edit();
        editor.putString(UID_USER, uid);
        editor.apply();
    }

    public static String getUIDUser(Context context){
        return getSharedPreference(context).getString(UID_USER,"");
    }

    /*
     * Added : Kode User
     * */

    public static void setKodeUser(Context context, String kd) {
        SharedPreferences.Editor editor =  getSharedPreference(context).edit();
        editor.putString(KODE_USER, kd);
        editor.apply();
    }

    public static String getKodeUser(Context context){
        return getSharedPreference(context).getString(KODE_USER,"");
    }

    /*
     * Added : Nama user
     * */

    public static void setNamaUser(Context context, String nm) {
        SharedPreferences.Editor editor =  getSharedPreference(context).edit();
        editor.putString(NAMA_USER, nm);
        editor.apply();
    }

    public static String getNamaUser(Context context) {
        return getSharedPreference(context).getString(NAMA_USER,"");
    }

    /*
     * Added : Gender
     * */

    public static void setGender(Context context, String gnd) {
        SharedPreferences.Editor editor =  getSharedPreference(context).edit();
        editor.putString(GENDER, gnd);
        editor.apply();
    }

    public static String getGender(Context context) {
        return getSharedPreference(context).getString(GENDER,"");
    }

    /*
     * Added : Alias
     * */

    public static void setAlias(Context context, String aka) {
        SharedPreferences.Editor editor =  getSharedPreference(context).edit();
        editor.putString(ALIAS, aka);
        editor.apply();
    }

    public static String getAlias(Context context) {
        return getSharedPreference(context).getString(ALIAS,"");
    }

    /*
     * Added : Email
     * */

    public static void setEmail(Context context, String mail) {
        SharedPreferences.Editor editor =  getSharedPreference(context).edit();
        editor.putString(EMAIL, mail);
        editor.apply();
    }

    public static String getEmail(Context context) {
        return getSharedPreference(context).getString(EMAIL,"");
    }

    /*
     * Added : Tanggal Register
     * */

    public static void setRegistered(Context context, String registed) {
        SharedPreferences.Editor editor =  getSharedPreference(context).edit();
        editor.putString(REGISTERED, registed);
        editor.apply();
    }

    public static String getRegistered(Context context) {
        return getSharedPreference(context).getString(REGISTERED,"");
    }

    /*
     * Added : Status Login
     * */

    public static void setStatusLogin(Context context, String status) {
        SharedPreferences.Editor editor =  getSharedPreference(context).edit();
        editor.putString(STATUS_LOGIN, status);
        editor.apply();
    }

    public static String getStatusLogin(Context context) {
        return getSharedPreference(context).getString(STATUS_LOGIN, "");
    }

    /*
     * Added : Username
     * */

    public static void setUsername(Context context, String username) {
        SharedPreferences.Editor editor =  getSharedPreference(context).edit();
        editor.putString(USERNAME, username);
        editor.apply();
    }

    public static String getUsername(Context context) {
        return getSharedPreference(context).getString(USERNAME, "");
    }

    /*
     * Added : Token Login
     * */

    public static void setAkunToken(Context context, String token) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(TOKEN_LOGIN_AKUN, token);
        editor.apply();
    }

    public static String getAkunToken(Context context) {
        return getSharedPreference(context).getString(TOKEN_LOGIN_AKUN, "");
    }

    /*
     * Added : Clear All Preferences
     * */

    public static void clearPreferencesLogin(Context context){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();

        editor.remove(UID_USER);
        editor.remove(KODE_USER);
        editor.remove(NAMA_USER);
        editor.remove(USERNAME);
        editor.remove(GENDER);
        editor.remove(ALIAS);
        editor.remove(EMAIL);
        editor.remove(REGISTERED);
        editor.remove(STATUS_LOGIN);
        editor.remove(TOKEN_LOGIN_AKUN);

        editor.apply();
    }

}
