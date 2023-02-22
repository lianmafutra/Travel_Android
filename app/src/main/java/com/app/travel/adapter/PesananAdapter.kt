package com.app.travel.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.travel.databinding.ItemHistoriPesananBinding
import com.app.travel.model.pesanan.DataItem
import com.app.travel.utils.convertToRupiah

class PesananAdapter(
    private val dataItem: List<DataItem?>?,
    private val itemClickListener: OnItemClickListener, val context: Context
) :
    RecyclerView.Adapter<PesananAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemBinding =
            ItemHistoriPesananBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(itemBinding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item: DataItem = dataItem!![position]!!
        holder.bind(item, itemClickListener, context)
    }

    override fun getItemCount(): Int = dataItem!!.size

    interface OnItemClickListener {
        fun onItemClickedLayananSyarat(item: DataItem?, jenis: String)
    }

    class Holder(private val binding: ItemHistoriPesananBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: DataItem, clickListener: OnItemClickListener, context: Context) {

            if(item.buktiPembayaran == null || item.buktiPembayaran.isEmpty()){
                binding.btnBayar.isVisible = true
            }

//
//            Glide
//                .with(context)
//                .load(Config.URL_STORAGE + item.mobil!!.foto)
//                .placeholder(R.drawable.placeholder)
//                .error(R.drawable.placeholder)
//                .centerCrop()
//                .into(binding.imgFotoUser);

            binding.tvKodeStruk.text = item.kodePesanan.toString()

            if(item.statusPembayaran == "BELUM" && item.buktiPembayaran == null) {
                binding.btnBayar.isVisible = true
                binding.tvStatus.text = "Menunggu Pembayaran"
            }
            if(item.statusPembayaran == "BELUM" && (item.buktiPembayaran != null && item.buktiPembayaran != "")) {
                binding.btnBayar.isVisible = true
                binding.tvStatus.text = "Menunggu Konfirmasi Admin"
            }

            else if(item.statusPembayaran == "LUNAS"  && item.buktiPembayaran.toString().isNotBlank()) {
                binding.btnBayar.isVisible = true
                binding.tvStatus.text = "Pembayaran Telah Lunas"
                binding.tvStatus.setTextColor(Color.parseColor("#007c00"));
            }


            if(item.statusPesanan == "SELESAI" && item.ratingKomen.toString().isBlank()) {
                binding.tvStatus.text = "Pesanan Telah Selesai"
                binding.btnReview.isVisible = true
            }

            if(item.statusPesanan == "TOLAK") {
                binding.btnBayar.isVisible = true
                binding.tvStatus.text = "Pesanan Ditolak Admin"
            }

            binding.tvJadwal.text = item.jadwal!!.lokasiKeberangkatanR!!.nama+" -> "+ item.jadwal.lokasiTujuanR!!.nama
            binding.tvTotalBiaya.text = item.total_biaya.toString().convertToRupiah()
            binding.tvTglKeberangkatan.text = item.tglKeberangkatan


            binding.btnReview.setOnClickListener {
                clickListener.onItemClickedLayananSyarat(item, "review")
            }

            binding.btnBayar.setOnClickListener {
                clickListener.onItemClickedLayananSyarat(item,"bayar")
            }

        }

    }
}