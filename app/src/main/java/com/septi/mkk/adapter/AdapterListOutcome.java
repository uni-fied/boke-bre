package com.septi.mkk.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.septi.mkk.R;
import com.septi.mkk.function.Functions;

import java.util.ArrayList;

public class AdapterListOutcome extends RecyclerView.Adapter<AdapterListOutcome.MyViewHolder>{

    Functions function;

    Context contextAdapterDataIncome;
    private final ArrayList<Integer> idData_Outcome;
    private final ArrayList<Integer> idDetailPengeluaran;
    private final ArrayList<String> kategoriPengeluaran;
    private final ArrayList<Integer> idSumberIncome;
    private final ArrayList<String> sourceTittle;
    private final ArrayList<Integer> idUserOuter;
    private final ArrayList<String> nameOfUser;
    private final ArrayList<String> genderUser;
    private final ArrayList<String> tglPengeluaran;
    private final ArrayList<String> jamPengeluaran;
    private final ArrayList<String> detailPengeluaran;
    private final ArrayList<Integer> rupiahPengeluaran;


    public AdapterListOutcome(Context context,
                              ArrayList<Integer> idData_Outcome,
                              ArrayList<Integer> idDetailPengeluaran,
                              ArrayList<String> kategoriPengeluaran,
                              ArrayList<Integer> idSumberIncome,
                              ArrayList<String> sourceTittle,
                              ArrayList<Integer> idUserOuter,
                              ArrayList<String> nameOfUser,
                              ArrayList<String> genderUser,
                              ArrayList<String> tglPengeluaran,
                              ArrayList<String> jamPengeluaran,
                              ArrayList<String> detailPengeluaran,
                              ArrayList<Integer> rupiahPengeluaran) {
        this.contextAdapterDataIncome = context;
        this.idData_Outcome = idData_Outcome;
        this.idDetailPengeluaran = idDetailPengeluaran;
        this.kategoriPengeluaran = kategoriPengeluaran;
        this.idSumberIncome = idSumberIncome;
        this.sourceTittle = sourceTittle;
        this.idUserOuter = idUserOuter;
        this.nameOfUser = nameOfUser;
        this.genderUser = genderUser;
        this.tglPengeluaran = tglPengeluaran;
        this.jamPengeluaran = jamPengeluaran;
        this.detailPengeluaran = detailPengeluaran;
        this.rupiahPengeluaran = rupiahPengeluaran;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contextAdapterDataIncome).inflate(R.layout.item_data_outcome, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        function = new Functions(contextAdapterDataIncome);

        holder.tvTglJamOut.setText(tglPengeluaran.get(position) + " " + jamPengeluaran.get(position) + " WIB");

        if (genderUser.get(position).equals("Perempuan")) {
            holder.imgUserOuter.setImageResource(R.drawable.woman);
        } else {
            holder.imgUserOuter.setImageResource(R.drawable.man);
        }

        holder.tvJmlRpOut.setText("Rp. " + function.decimal2Rp(String.valueOf(rupiahPengeluaran.get(position))) + "-,");
        holder.tvKategoriOut.setText(kategoriPengeluaran.get(position));
        holder.tvDetailOut.setText(detailPengeluaran.get(position) + " oleh " + nameOfUser.get(position));
        holder.tvSumberInofOut.setText("Sumber Pemasukan : " + sourceTittle.get(position));

        holder.itemPengeluaran.setOnLongClickListener(v -> {
            /*
             * On Long Click Item
             * */

            AlertDialog.Builder adb = new AlertDialog.Builder(contextAdapterDataIncome);
            adb.setTitle("Perhatian!");
            adb.setMessage(Html.fromHtml("Apakah Anda ingin menghapus data pengeluaran <b>" + kategoriPengeluaran.get(position) + " (" + idData_Outcome.get(position) + ") </b> ini?"));
            adb.setNegativeButton("Hapus", (dialog, which) -> {
                function.procedureHapusItemPengeluaran(idData_Outcome.get(position));
            });
            adb.setPositiveButton("Batalkan", (dialog, which) -> {
            });
            adb.show();
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return idData_Outcome.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CardView itemPengeluaran;
        TextView tvTglJamOut, tvKategoriOut, tvJmlRpOut, tvDetailOut, tvSumberInofOut;
        ImageView imgUserOuter;

        @SuppressLint("CutPasteId")
        public MyViewHolder(View itemView) {
            super(itemView);

            itemPengeluaran = itemView.findViewById(R.id.cardItemDataOutCome);
            tvTglJamOut = itemView.findViewById(R.id.txTglJam_Out);
            tvKategoriOut = itemView.findViewById(R.id.txKategori_Out);
            tvJmlRpOut = itemView.findViewById(R.id.txJmlRupiah_Out);
            tvDetailOut = itemView.findViewById(R.id.txReasonDatabyUser_Out);
            tvSumberInofOut = itemView.findViewById(R.id.txtSumberIncomefrom);
            imgUserOuter = itemView.findViewById(R.id.imgUser_Out);

        }
    }
}
