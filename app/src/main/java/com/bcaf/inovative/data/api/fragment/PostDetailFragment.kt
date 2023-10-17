package com.bcaf.inovative.data.api.fragment

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Html
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.auth0.android.jwt.JWT
import com.bcaf.inovative.R
import com.bcaf.inovative.adapter.RecyclerViewAdapter2
import com.bcaf.inovative.data.api.methods.UserApi
import com.bcaf.inovative.data.api.request.DataItem
import com.bcaf.inovative.data.api.request.GetAllPost
import com.bcaf.inovative.data.api.request.Post
import com.bcaf.inovative.data.api.request.Reply
import com.bcaf.inovative.data.api.request.User2
import com.bcaf.inovative.data.api.request.User3
import com.bcaf.inovative.utils.Constant
import com.bcaf.inovative.utils.SessionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.textfield.TextInputLayout
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat

class PostDetailFragment : Fragment() {

    private lateinit var dataItem: DataItem
    private var isVoted: Boolean? = false
    private lateinit var progressBar: ProgressBar
    private lateinit var userApi: UserApi


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_detail, container, false)

        val typeReply = arguments?.getBoolean("typeReply", false)
        dataItem = arguments?.getSerializable("postItem") as DataItem
        isVoted = arguments?.getBoolean("isVoted", false)


        val tlKomentar = view.findViewById<TextInputLayout>(R.id.tlKomentar)
        if (typeReply!!) {
            tlKomentar.editText!!.requestFocus()

            // Show the keyboard
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(tlKomentar.editText!!, InputMethodManager.SHOW_IMPLICIT)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        userApi = retrofit.create(UserApi::class.java)

        progressBar = view.findViewById(R.id.spinner)

        Glide.with(requireContext()) // Use "this" for an Activity or "requireContext()" for a Fragment
            .load("https://picsum.photos/300")
            .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
            .placeholder(R.drawable.cute_ava_2) // A placeholder image while loading (optional)
            .error(R.drawable.cute_ava_2) // An error image to display if loading fails (optional)
            .into(view.findViewById<CircleImageView>(R.id.imageView))

        Glide.with(requireContext()) // Use "this" for an Activity or "requireContext()" for a Fragment
            .load("https://picsum.photos/300")
            .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
            .placeholder(R.drawable.cute_ava_2) // A placeholder image while loading (optional)
            .error(R.drawable.cute_ava_2) // An error image to display if loading fails (optional)
            .into(view.findViewById<CircleImageView>(R.id.civAvaCurrent))

        view.findViewById<TextView>(R.id.txtNama).text = dataItem.user.name
        view.findViewById<TextView>(R.id.txtTanggalPost).text =
            formatDate(dataItem.tanggalPost.toString())
        view.findViewById<TextView>(R.id.txtJudulPost).text = dataItem.judulPost
        view.findViewById<TextView>(R.id.txtDeskripsi).text =
            Html.fromHtml(dataItem.deskripsi, Html.FROM_HTML_MODE_LEGACY)

        val ivFoto = view.findViewById<ImageView>(R.id.ivFoto)

        if (!dataItem.fotoKonten.isNullOrBlank()) {
            var base64Image = dataItem.fotoKonten
            base64Image = base64Image!!.removePrefix("data:image/png;base64,")
            base64Image = base64Image!!.removePrefix("data:image/jpg;base64,")
            base64Image = base64Image!!.removePrefix("data:image/jpeg;base64,")

            val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)

            if (decodedBytes != null) {
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                if (bitmap != null) {
                    ivFoto.setImageBitmap(bitmap)
                } else {
                    // Handle the case where decoding to a bitmap fails
                }
            } else {
                // Handle the case where decoding the bytes from Base64 fails
            }
        } else {
            ivFoto.visibility = View.GONE
        }

        val ivUpvote = view.findViewById<ImageView>(R.id.ivUpvote)
        val tvUpvote = view.findViewById<TextView>(R.id.likeCountTextView)

        if (!dataItem.isLiked) {
            ivUpvote.setImageResource(R.drawable.upvote_blue)
            tvUpvote.text = dataItem.upvote.toString()
            tvUpvote.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))

//            view.findViewById<LinearLayout>(R.id.likeIcon).setOnClickListener {
//                delUpvote(dataItem)
//            }
        } else {
            ivUpvote.setImageResource(R.drawable.upvote_gray)
            tvUpvote.text = dataItem.upvote.toString()
            tvUpvote.setTextColor(ContextCompat.getColor(requireContext(), R.color.lightGray))

//            view.findViewById<LinearLayout>(R.id.likeIcon).setOnClickListener {
//                addUpvote(dataItem)
//            }
        }

        view.findViewById<LinearLayout>(R.id.likeIcon).setOnClickListener {
            if (!dataItem.isLiked) {
                dataItem.isLiked = true

                ivUpvote.setImageResource(R.drawable.upvote_blue)
                tvUpvote.text = "Upvote (${(dataItem.upvote.toString().toInt() + 1).toString()})"
                tvUpvote.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))

            } else {
                dataItem.isLiked = false
                ivUpvote.setImageResource(R.drawable.upvote_gray)
                tvUpvote.text = "Upvote (${(dataItem.upvote.toString().toInt() - 1).toString()})"
                tvUpvote.setTextColor(ContextCompat.getColor(requireContext(), R.color.lightGray))

            }
        }

        val rvReply = view.findViewById<RecyclerView>(R.id.rvReply)
        val adapter = RecyclerViewAdapter2(dataItem, requireContext())

        // Set up the RecyclerView and adapter
        rvReply.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rvReply.adapter = adapter

        val tlKomentar = view.findViewById<TextInputLayout>(R.id.tlKomentar)
        tlKomentar.setEndIconOnClickListener {
            progressBar.visibility = View.VISIBLE
            val token = SessionManager.getToken(requireContext())
            val jwt = JWT(token!!)
            val userId = jwt.getClaim("id").asString().toString() //get custom claims

            val komen = tlKomentar.editText!!.text.toString()

            val reply = Reply(
                comment = komen,
                post = dataItem,
                user = User3(idUser = userId.toInt())
            )
            Log.i("Data reply", reply.toString())
            // Memanggil fungsi untuk memasukkan data pengguna
            insertData(reply)
        }
    }

    private fun insertData(reply: Reply) {
        val token = SessionManager.getToken(requireContext())

        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Memanggil API untuk memasukkan data pengguna
                val response: Response<GetAllPost> = userApi.createReply("Bearer $token", reply)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val createdReply: GetAllPost? = response.body()
                        // Handle the created user
                        Log.d("Response", "User created: $createdReply")

                        // Tampilkan toast jika data berhasil disimpan
                        Toast.makeText(
                            requireContext(),
                            "Data berhasil disimpan",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Handle unsuccessful response
                        Log.e("API_ERROR", "Unsuccessful response: ${response.code()}")
                        Toast.makeText(
                            requireContext(),
                            "Gagal menyimpan data. Status code: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                // Handle network errors or other exceptions
                Log.e("API_ERROR", "Request failed: ${e.message}", e)
            } finally {
                exitFragment()
            }
        }
    }

    fun exitFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        // Check if there are fragments in the back stack
        if (fragmentManager.backStackEntryCount > 0) {
            // Pop the back stack to navigate back to the previous fragment
            fragmentManager.popBackStack()
        } else {
            // If there are no fragments in the back stack, you can decide what to do
            // For example, you can replace the current fragment with another fragment
            fragmentManager.beginTransaction().replace(R.id.frmFragmentRoot, HomeActivity())
                .commit()
        }
    }

    fun addUpvote(item: DataItem) {

    }

    fun delUpvote(item: DataItem) {

    }

    fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        val outputFormat = SimpleDateFormat("dd MMM")
        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date)
    }
}