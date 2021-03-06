package com.septi.mkk.adapter;

import static com.septi.mkk.fragment.FragmentIncome.TAMBAH_PENGELUARAN;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.septi.mkk.R;
import com.septi.mkk.function.Functions;
import com.septi.mkk.library.TinyDB;

import java.util.ArrayList;

public class AdapterListIncome extends RecyclerView.Adapter<AdapterListIncome.MyViewHolder>{

    Functions function;

    Context contextAdapterDataIncome;
    private final ArrayList<Integer> idData_Income;
    private final ArrayList<String> userName_Income;
    private final ArrayList<String> namaUser_Income;
    private final ArrayList<String> jnsKelamin_Income;
    private final ArrayList<String> hariTambah_Income;
    private final ArrayList<String> tglTambah_Income;
    private final ArrayList<String> jamTambah_Income;
    private final ArrayList<String> kategoriData_Income;
    private final ArrayList<String> details_Income;
    private final ArrayList<Integer> jmlIncome_Income;
    private final ArrayList<Integer> jmlSisaIncome;
    private final ArrayList<Integer> jmlItemOut;


    public AdapterListIncome(Context context,
                             ArrayList<Integer> idData_Income,
                             ArrayList<String> userName_Income,
                             ArrayList<String> namaUser_Income,
                             ArrayList<String> jnsKelamin_Income,
                             ArrayList<String> hariTambah_Income,
                             ArrayList<String> tglTambah_Income,
                             ArrayList<String> jamTambah_Income,
                             ArrayList<String> kategoriData_Income,
                             ArrayList<String> details_Income,
                             ArrayList<Integer> jmlIncome_Income,
                             ArrayList<Integer> jmlSisaIncome,
                             ArrayList<Integer> jmlItemOut) {
        this.contextAdapterDataIncome = context;
        this.idData_Income = idData_Income;
        this.userName_Income = userName_Income;
        this.namaUser_Income = namaUser_Income;
        this.jnsKelamin_Income = jnsKelamin_Income;
        this.hariTambah_Income = hariTambah_Income;
        this.tglTambah_Income = tglTambah_Income;
        this.jamTambah_Income = jamTambah_Income;
        this.kategoriData_Income = kategoriData_Income;
        this.details_Income = details_Income;
        this.jmlIncome_Income = jmlIncome_Income;
        this.jmlSisaIncome = jmlSisaIncome;
        this.jmlItemOut = jmlItemOut;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contextAdapterDataIncome).inflate(R.layout.item_data_income, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        function = new Functions(contextAdapterDataIncome);

        int jmlitemout = jmlItemOut.get(position);
        holder.tvInfoAvailableItemOut.setText("Terdapat " + jmlitemout + " Item Pengeluaran pada Pemasukan ini");
        if (jmlitemout == 0) {
            holder.lyInfoAvailableItemOut.setVisibility(View.GONE);
        } else {
            holder.lyInfoAvailableItemOut.setVisibility(View.VISIBLE);
        }

        holder.tvTglJamMenit.setText(tglTambah_Income.get(position) + " - " + jamTambah_Income.get(position) + " WIB");
        holder.tvKategori.setText(kategoriData_Income.get(position));

        /*
         * Notes : Diambil dari jumlah sisa income
         * */
        holder.tvJumlahIncomeRp.setText("Rp. " + function.decimal2Rp(String.valueOf(jmlSisaIncome.get(position))) + "-,");

        holder.tvKeteranganUser.setText(details_Income.get(position) + " - " + namaUser_Income.get(position));

        String jenisKelamin = jnsKelamin_Income.get(position);

        switch (jenisKelamin) {
            case "Perempuan":
                holder.gmbUser.setImageResource(R.drawable.woman);
                break;
            default:
                holder.gmbUser.setImageResource(R.drawable.man);
                break;
        }

        holder.cvItemDataIncome.setOnLongClickListener(v -> {
            /*
             * On Long Click Item
             * */

            AlertDialog.Builder adb = new AlertDialog.Builder(contextAdapterDataIncome);
            adb.setTitle("Perhatian!");
            adb.setMessage(Html.fromHtml(contextAdapterDataIncome.getString(R.string.quest_del_edit,  "<b>" + details_Income.get(position) + " ditambahkan oleh " + namaUser_Income.get(position) + "</b>")));
            adb.setNegativeButton("Hapus", (dialog, which) -> {
                AlertDialog.Builder adb_confirm_del = new AlertDialog.Builder(contextAdapterDataIncome);
                adb_confirm_del.setTitle("Konfirmasi!");
                adb_confirm_del.setMessage(Html.fromHtml("Apakah anda yakin ingin menghapus data dengan id pemasukan <b>"+ idData_Income.get(position) +"<b>?" ));
                adb_confirm_del.setNegativeButton("Confirm", (dialog12, which12) -> {
                    if (jmlitemout != 0) {
                        Toast.makeText(contextAdapterDataIncome, "Maaf, Anda harus menghapus item-item pengeluaran yang bersumber dari pemasukan ini! untuk menghapusnya.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(contextAdapterDataIncome, "Anda melakukan penghapusan data, data berhasil terhapus", Toast.LENGTH_SHORT).show();

                        function.procedureDelItemPemasukan(idData_Income.get(position));
                    }

                });
                adb_confirm_del.setNeutralButton("Cancel", (dialog1, which1) -> {/*Menutup alert dan tidak melakukan apa-apa*/});
                adb_confirm_del.show();
            });
            adb.setPositiveButton("Edit", (dialog, which) -> {
                Toast.makeText(contextAdapterDataIncome, "Fungsi ini belum tersedia, tunggu updatenya ya :)", Toast.LENGTH_SHORT).show();
            });
            adb.setNeutralButton("Batalkan", (dialog, which) -> {/*Menutup alert dan tidak melakukan apa-apa*/});
            adb.show();
            return true;
        });

        holder.cvItemDataIncome.setOnClickListener(v -> {
            if (jmlSisaIncome.get(position).equals(0)) {
                Toast.makeText(contextAdapterDataIncome, "Oops! sumber pemasukan ini sudah habis!", Toast.LENGTH_SHORT).show();
            } else {
                TinyDB tinyDB = new TinyDB(contextAdapterDataIncome);

                tinyDB.putString("KATEGORI_PEMASUKAN", kategoriData_Income.get(position));
                tinyDB.putInt("JML_INCOME", jmlSisaIncome.get(position));
                tinyDB.putInt("ID_INCOME", idData_Income.get(position));

                LocalBroadcastManager.getInstance(contextAdapterDataIncome).sendBroadcast(new Intent(TAMBAH_PENGELUARAN));
            }
        });
    }

    @Override
    public int getItemCount() {
        return idData_Income.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvTglJamMenit, tvKategori, tvJumlahIncomeRp, tvSisaIncome, tvJmlDataOutcome, tvKeteranganUser, tvInfoAvailableItemOut;
        CardView cvItemDataIncome;
        ImageView gmbUser;
        LinearLayout lyInfoAvailableItemOut;
        
        @SuppressLint("CutPasteId")
        public MyViewHolder(View itemView) {
            super(itemView);

            tvTglJamMenit = itemView.findViewById(R.id.txTglJam);
            tvKategori = itemView.findViewById(R.id.txKategori);
            tvJumlahIncomeRp = itemView.findViewById(R.id.txJmlRupiah);
            tvSisaIncome = itemView.findViewById(R.id.txJmlRupiahSisa);
            tvJmlDataOutcome = itemView.findViewById(R.id.txJmlDataPengeluaran);
            tvKeteranganUser = itemView.findViewById(R.id.txReasonDatabyUser);
            tvInfoAvailableItemOut = itemView.findViewById(R.id.txInfoItemAvailableOut);

            cvItemDataIncome = itemView.findViewById(R.id.cardItemData);

            lyInfoAvailableItemOut = itemView.findViewById(R.id.layTersediaItemOut);

            gmbUser = itemView.findViewById(R.id.imgUser);
        }
    }
}
