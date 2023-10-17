package com.bcaf.inovative.data.api.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.bcaf.inovative.R
import com.bcaf.inovative.data.api.methods.UserApi
import com.bcaf.inovative.data.api.request.User
import com.bcaf.inovative.data.api.response.LoginResponse
import com.bcaf.inovative.utils.Constant
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterActivity.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterActivity : Fragment() {

    lateinit var progressBar: ProgressBar
    private var exitTime: Long = 0
    private lateinit var userApi: UserApi
    private lateinit var tvLogin: TextView
    private lateinit var progressBar2: ProgressBar


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        return inflater.inflate(R.layout.fragment_register_activity, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvLogin = view.findViewById(R.id.tvLogin)
        progressBar2 = view.findViewById(R.id.progressBar2)

        val retrofit = Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        userApi = retrofit.create(UserApi::class.java)

        val editTextName: TextInputLayout = view.findViewById(R.id.editNama)
        val editTextUserName: TextInputLayout = view.findViewById(R.id.editUsername)
        val editTextPassword: TextInputLayout = view.findViewById(R.id.editPassword)

        val buttonInsert: Button = view.findViewById(R.id.btnSend)
        buttonInsert.setOnClickListener {
            val nama = editTextName.editText!!.text.toString()
            val userName = editTextUserName.editText!!.text.toString()
            val password = editTextPassword.editText!!.text.toString()

            val user = User(email = "${userName}@gmail.com", userName = userName, password = password, nama = nama,"pria")

            // Memanggil fungsi untuk memasukkan data pengguna
            insertData(user)
            progressBar2.visibility = View.VISIBLE
        }

        tvLogin.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frmFragmentRoot, MainActivity.newInstance("", ""))
                .commit()
        }
    }

    private fun insertData(user: User) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Memanggil API untuk memasukkan data pengguna
                val response: Response<LoginResponse> = userApi.createUser(user)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val createdUser: LoginResponse? = response.body()
                        // Handle the created user
                        Log.d("Response", "User created: $createdUser")

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
                navigateToFragment(MainActivity())
            }
        }


    }

    fun navigateToFragment(fragment: Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        if (transaction != null) {
            transaction.replace(R.id.frmFragmentRoot, fragment)
            transaction.commit()
        }
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegisterActivity.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterActivity().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}