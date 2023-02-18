package com.app.travel.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.travel.R
import com.app.travel.databinding.ItemPesananBinding
import com.app.travel.model.pesanan.DataItem
import com.app.travel.network.Config
import com.app.travel.utils.convertToRupiah
import com.bumptech.glide.Glide

class PesananAdapter(
    private val dataItem: List<DataItem?>?,
    private val itemClickListener: OnItemClickListener, val context: Context
) :
    RecyclerView.Adapter<PesananAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemBinding =
            ItemPesananBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(itemBinding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item: DataItem = dataItem!![position]!!
        holder.bind(item, itemClickListener, context)
    }

    override fun getItemCount(): Int = dataItem!!.size

    interface OnItemClickListener {
        fun onItemClickedLayananSyarat(item: DataItem?)
    }

    class Holder(private val binding: ItemPesananBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: DataItem, clickListener: OnItemClickListener, context: Context) {
//
//            Glide
//                .with(context)
//                .load(Config.URL_STORAGE + item.mobil!!.foto)
//                .placeholder(R.drawable.placeholder)
//                .error(R.drawable.placeholder)
//                .centerCrop()
//                .into(binding.imgFotoUser);

            binding.tvNamaMobil.text = item.kodePesanan.toString()



            binding.btnReview.setOnClickListener {
                clickListener.onItemClickedLayananSyarat(item)
            }

        }

    }
}