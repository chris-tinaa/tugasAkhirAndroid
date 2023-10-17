package com.bcaf.inovative.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bcaf.inovative.R
import com.bcaf.inovative.data.api.fragment.ReplyActivity
import com.bcaf.inovative.data.api.request.DataItem
import com.bcaf.inovative.data.api.request.GetAllPost
import com.bcaf.inovative.data.api.request.GetAllPostId
import com.bcaf.inovative.data.api.request.User2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat

class RecyclerViewAdapter2(
    private val dataItem: DataItem,
    val context: Context) :
    RecyclerView.Adapter<RecyclerViewAdapter2.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reply, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataItem.listReply?.get(position)

        // Mengambil komentar (comment) dari ListReplyItem pertama (asumsi ada lebih dari 1 reply)

        Glide.with(context) // Use "this" for an Activity or "requireContext()" for a Fragment
            .load("https://picsum.photos/300")
            .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
            .placeholder(R.drawable.cute_ava_2) // A placeholder image while loading (optional)
            .error(R.drawable.cute_ava_2) // An error image to display if loading fails (optional)
            .into(holder.civAva)


        // Set nama user ke TextView
        holder.namaTextView.text = item!!.user.name
        holder.komenTextView.text = item!!.comment ?: ""
        holder.tglTextView.text = formatDate(item!!.tanggalReply.toString()) ?: ""

    }

    override fun getItemCount(): Int {
        return dataItem.listReply!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val civAva: CircleImageView = itemView.findViewById(R.id.civAva)
        val namaTextView: TextView = itemView.findViewById(R.id.tvNama)
        val komenTextView: TextView = itemView.findViewById(R.id.tvDeskripsi)
        val tglTextView: TextView = itemView.findViewById(R.id.tvTanggal)

    }

    fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        val outputFormat = SimpleDateFormat("dd MMM")
        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date)
    }
}
