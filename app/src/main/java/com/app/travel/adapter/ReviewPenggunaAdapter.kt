package com.app.travel.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.travel.R
import com.app.travel.databinding.ItemReviewPenggunaBinding
import com.app.travel.model.DataItemReview
import com.app.travel.network.Config
import com.bumptech.glide.Glide

class ReviewPenggunaAdapter(
    private val dataItem: List<DataItemReview?>?,
    private val itemClickListener: OnItemClickListener,
    val context: Context
) : RecyclerView.Adapter<ReviewPenggunaAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemBinding =
            ItemReviewPenggunaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(itemBinding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item: DataItemReview = dataItem!![position]!!
        holder.bind(item, itemClickListener, context)
    }

    override fun getItemCount(): Int = dataItem!!.size

    interface OnItemClickListener {
        fun onItemClickedLayananSyarat(dataItemReview: DataItemReview?, s: String)
    }

    class Holder(private val binding: ItemReviewPenggunaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: DataItemReview, clickListener: OnItemClickListener, context: Context) {

            Glide.with(context).load(Config.URL_STORAGE + item.user!!.foto)
                .placeholder(R.drawable.ic_user).error(R.drawable.ic_user).centerCrop()
                .into(binding.imgFotoUser);

            binding.tvNamaUser.text = item.user.namaLengkap
            binding.tvPesan.text = item.ratingKomen
            binding.bintangReview.rating = item.ratingNilai!!.toFloat()

        }

    }
}