package com.bcaf.inovative.data.api.fragment

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.auth0.android.jwt.JWT
import com.bcaf.inovative.R
import com.bcaf.inovative.data.api.methods.UserApi
import com.bcaf.inovative.data.api.request.Post
import com.bcaf.inovative.data.api.request.Post2
import com.bcaf.inovative.data.api.response.LoginResponse
import com.bcaf.inovative.utils.Constant
import com.bcaf.inovative.utils.SessionManager
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PostActivity.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostActivity : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var imgpp: ConstraintLayout
    lateinit var ivImage: ImageView
    lateinit var userId: String
    lateinit var userName: String
    //private lateinit var txtToken: TextView

    lateinit var progressBar: ProgressBar
    lateinit var bitmap: Bitmap

    private lateinit var userApi: UserApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        // Customize the title
        (activity as AppCompatActivity).getSupportActionBar()?.setTitle("Tambah Post");

        progressBar = view.findViewById(R.id.spinner)
        imgpp = view.findViewById(R.id.imgpp)
        ivImage = view.findViewById(R.id.ivImage)

        //btn openkamera
        imgpp.setOnClickListener(View.OnClickListener {
            dispatchTakePictureIntent()
        })

        val retrofit = Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        userApi = retrofit.create(UserApi::class.java)

        val editTextJudul: TextInputLayout = view.findViewById(R.id.editJudul)
        val editTextDeskripsi: TextInputLayout = view.findViewById(R.id.editdeskripsi)
        val buttonInsert: Button = view.findViewById(R.id.btnSend)

        buttonInsert.setOnClickListener {

            val bitmap = (ivImage.drawable as? BitmapDrawable)?.bitmap
            var encodedImage: String? = null
            bitmap?.let {
                val image = encodeImage(it)
                // Lakukan sesuatu dengan encodedImage
                encodedImage = image
            }

            val token = SessionManager.getToken(requireContext())
            Log.i("Hasil token", token.toString())

            val jwt = JWT(token!!)
            userName = jwt.getClaim("sub").asString().toString() //get custom claims
            userId = jwt.getClaim("id").asString().toString() //get custom claims

            val post1 = Post(
                userId.toInt(),
                userName
            )

            val post2 = Post2(
                title = editTextJudul.editText!!.text.toString(),
                email = "",
                description = editTextDeskripsi.editText!!.text.toString(),
                upvote = 0,
                fotoKonten = encodedImage,
                post1
            )

            // Memanggil fungsi untuk memasukkan data pengguna
            insertData(post2)


        }
    }

    private fun insertData(post2: Post2) {
        val token = SessionManager.getToken(requireContext())

        progressBar.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Memanggil API untuk memasukkan data pengguna
                val response: Response<LoginResponse> = userApi.createPost("Bearer $token", post2)

                    if (response.isSuccessful) {
                        val createPost: LoginResponse? = response.body()
                        // Handle the created user
                        Log.d("Response", "User created: $createPost")

                        // Tampilkan toast jika data berhasil disimpan
                        val handler = Handler(Looper.getMainLooper())
                        handler.post(Runnable {
                            progressBar.visibility = View.GONE

                            // Your UI-related code here
                            Toast.makeText(
                                requireContext(),
                                "Data berhasil disimpan",
                                Toast.LENGTH_SHORT
                            ).show()
                        })

                    } else {
                        // Handle unsuccessful response
                        Log.e("API_ERROR", "Unsuccessful response: ${response.code()}")
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


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PostActivity.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostActivity().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            takePictureIntent.resolveActivity(requireActivity().packageManager)
            startActivityForResult(takePictureIntent, 1)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            ivImage.setImageBitmap(imageBitmap)
            bitmap = imageBitmap
        }
    }

    fun createImageRequestBody(bitmap: Bitmap?): MultipartBody.Part {

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()

        val requestBody = imageBytes.toRequestBody("image/*".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(
            "image",
            System.currentTimeMillis().toString() + "image.jpg",
            requestBody
        )
    }

    private fun encodeImage(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

}