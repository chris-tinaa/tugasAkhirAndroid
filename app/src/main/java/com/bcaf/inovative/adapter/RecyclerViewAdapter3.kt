package com.bcaf.inovative.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.text.Html
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcaf.inovative.R
import com.bcaf.inovative.data.api.request.DataItem
import com.bcaf.inovative.data.api.request.GetAllPost
import com.bcaf.inovative.data.api.request.User2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.androidstarting.callback.OnItemClickPost
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat

class RecyclerViewAdapter3(
    private val dataItems: List<DataItem>,
    private val context: Context,
    val isVoted: (DataItem) -> Boolean,
    val itemClickListener: OnItemClickPost<DataItem>,
    val replyClickListener: OnItemClickPost<DataItem>,
    val delUpvote: (DataItem) -> Unit,
    val addUpvote: (DataItem) -> Unit) :
    RecyclerView.Adapter<RecyclerViewAdapter3.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataItems[position]

        holder.titleTextView.text = item.judulPost

        holder.descriptionTextView.text = Html.fromHtml(item.deskripsi, Html.FROM_HTML_MODE_LEGACY)

        holder.emailTextView.text = item.user.name
        holder.tglTextView.text = formatDate(item.tanggalPost.toString())

        Glide.with(context) // Use "this" for an Activity or "requireContext()" for a Fragment
            .load("https://picsum.photos/300")
            .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
            .placeholder(R.drawable.cute_ava_2) // A placeholder image while loading (optional)
            .error(R.drawable.cute_ava_2) // An error image to display if loading fails (optional)
            .into(holder.civAva)

        if (!item.fotoKonten.isNullOrEmpty()) {
            var base64Image = item.fotoKonten
            base64Image = base64Image!!.removePrefix("data:image/png;base64,")
            base64Image = base64Image!!.removePrefix("data:image/jpg;base64,")
            base64Image = base64Image!!.removePrefix("data:image/jpeg;base64,")

            val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

            if (bitmap != null) {
                holder.ivFoto.setImageBitmap(bitmap)
            }
        } else {
            holder.ivFoto.visibility = View.GONE
        }

        holder.tvPeringkat.visibility = View.VISIBLE
        holder.tvPeringkat.text = "#Peringkat ${position+1}"

        val isVoted = item.isLiked

        if (isVoted) {
            holder.ivVote.setImageResource(R.drawable.upvote_blue)
            holder.tvVoteCount.text = item.upvote.toString()
            holder.tvVoteCount.setTextColor(ContextCompat.getColor(context, R.color.blue))

            holder.likeButton.setOnClickListener {
                delUpvote(item)

                item.isLiked = false
                holder.ivVote.setImageResource(R.drawable.upvote_gray)
                holder.tvVoteCount.text = (item.upvote.toString().toInt()-1).toString()
                holder.tvVoteCount.setTextColor(ContextCompat.getColor(context, R.color.lightGray))
            }
        } else {
            holder.ivVote.setImageResource(R.drawable.upvote_gray)
            holder.tvVoteCount.text = item.upvote.toString()
            holder.tvVoteCount.setTextColor(ContextCompat.getColor(context, R.color.lightGray))

            holder.likeButton.setOnClickListener {
                addUpvote(item)

                item.isLiked = true
                holder.ivVote.setImageResource(R.drawable.upvote_blue)
                holder.tvVoteCount.text = (item.upvote.toString().toInt()+1).toString()
                holder.tvVoteCount.setTextColor(ContextCompat.getColor(context, R.color.blue))

            }
        }



        holder.descriptionTextView.setOnClickListener(View.OnClickListener {
            itemClickListener.onItemClick(dataItems[position], isVoted)
        })

        holder.titleTextView.setOnClickListener(View.OnClickListener {
            itemClickListener.onItemClick(dataItems[position], isVoted)
        })

        holder.ivFoto.setOnClickListener(View.OnClickListener {
            itemClickListener.onItemClick(dataItems[position], isVoted)
        })


        holder.layoutKomentar.setOnClickListener {
            replyClickListener.onItemClick(dataItems[position], isVoted)
        }

    }
    override fun getItemCount(): Int {
        return dataItems.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.tvJudul)
        val descriptionTextView: TextView = itemView.findViewById(R.id.tvDeskripsi)
        val emailTextView: TextView = itemView.findViewById(R.id.tvNama)
        val tglTextView: TextView = itemView.findViewById(R.id.tvTanggal)
        val likeButton: LinearLayout = itemView.findViewById(R.id.layoutUpvote)
        val layoutKomentar: LinearLayout = itemView.findViewById(R.id.layoutKomentar)
        val ivVote: ImageView = itemView.findViewById(R.id.ivVote)
        val tvVoteCount: TextView = itemView.findViewById(R.id.tvVoteCount)
        val tvPeringkat: TextView = itemView.findViewById(R.id.tvPeringkat)
        val civAva: CircleImageView = itemView.findViewById(R.id.civAva)
        val ivFoto: ImageView = itemView.findViewById(R.id.ivFoto)
    }

    fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        val outputFormat = SimpleDateFormat("dd MMM")
        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date)
    }
}
