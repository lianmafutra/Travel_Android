package com.app.travel.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.travel.R
import com.app.travel.model.DataItemJadwal

class JadwalAdapter(private val userList: List<DataItemJadwal?>?, private val itemClickListener: OnItemClickListener, private val context: Context) :
    RecyclerView.Adapter<JadwalAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(userList?.get(position), itemClickListener, context)
    }

    override fun getItemCount(): Int {
        return userList!!.size
    }

    interface OnItemClickListener{
        fun onItemClickedLayananSyarat(layananItem: DataItemJadwal?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_jadwal, parent, false)
        )
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
        fun bindItems(item: DataItemJadwal?, clickListener: OnItemClickListener, context: Context) {
            val tvHarga = itemView.findViewById<TextView>(R.id.tv_harga)

            tvHarga.text = item!!.harga.toString()

            itemView.setOnClickListener {
                clickListener.onItemClickedLayananSyarat(item)
            }
        }
    }

}