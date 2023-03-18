package com.app.travel.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.travel.R
import com.app.travel.databinding.ItemJadwalBinding
import com.app.travel.model.DataItemJadwal
import com.app.travel.network.Config
import com.app.travel.network.SessionManager
import com.app.travel.utils.convertToRupiah
import com.bumptech.glide.Glide

class JadwalAdapter(
    private val dataItemJadwal: List<DataItemJadwal?>?,
    private val itemClickListener: OnItemClickListener, val context: Context
) :
    RecyclerView.Adapter<JadwalAdapter.DzikirHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DzikirHolder {
        val itemBinding =
            ItemJadwalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DzikirHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: DzikirHolder, position: Int) {
        val dzikir: DataItemJadwal = dataItemJadwal!![position]!!
        holder.bind(dzikir, itemClickListener, context)
    }

    override fun getItemCount(): Int = dataItemJadwal!!.size

    interface OnItemClickListener {
        fun onItemClickedLayananSyarat(layananItem: DataItemJadwal?, s: String)
    }

    class DzikirHolder(private val binding: ItemJadwalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: DataItemJadwal, clickListener: OnItemClickListener, context: Context) {
            val sessionManager = SessionManager(context)
            Glide
                .with(context)
                .load(sessionManager.getIPServer()+"/storage/" + item.mobil!!.foto)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .centerCrop()
                .into(binding.imgFotoUser);

            binding.tvHarga.text = item.harga?.toString()!!.convertToRupiah()
            binding.tvNamaMobil.text = item.mobil.nama.toString()
            binding.tvJenisMobil.text = item.mobil.jenis.toString()
            binding.tvWaktu.text = item.tanggal + " (" + item.jam + " WIB)"
            binding.tvKursi.text = item.kursiTersedia.toString()
            binding.tvStatus.text = item.status.toString()


            binding.btnPesan.setOnClickListener {
                clickListener.onItemClickedLayananSyarat(item, "pesan")
            }

            binding.btnReview.setOnClickListener {
                clickListener.onItemClickedLayananSyarat(item, "review")
            }

        }

    }
}