package com.bcaf.inovative.data.api.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcaf.inovative.R
import com.bcaf.inovative.adapter.RecyclerViewAdapter
import com.bcaf.inovative.data.api.methods.UserApi
import com.bcaf.inovative.data.api.request.DataItem
import com.bcaf.inovative.data.api.request.GetAllPost
import com.bcaf.inovative.data.api.request.User2
import com.bcaf.inovative.utils.Constant
import com.bcaf.inovative.utils.SessionManager
import com.bcaf.inovative.utils.SessionManager.clearData
import com.example.androidstarting.callback.OnItemClickPost
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Math.abs
import kotlin.coroutines.CoroutineContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeActivity.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeActivity : Fragment(), CoroutineScope {
    private lateinit var sessionManager: SessionManager
    private lateinit var apiService: UserApi
    lateinit var progressBar: ProgressBar

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var job: Job

    private lateinit var likeCountTextView: TextView
    private var likeCount = 0
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerViewAdapter
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        job = Job()

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel() // Hentikan semua pekerjaan ketika fragment dihancurkan
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //val txtToken: TextView = view.findViewById(R.id.txtToken)
         // Atau jenis LayoutManager yang sesuai

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        // Customize the title
        (activity as AppCompatActivity).getSupportActionBar()?.setTitle("Home");


        // txtToken.text = SessionManager.getToken(requireContext())
        val btnLogout: Button = view.findViewById(R.id.btn_logout)
        val btnPost: ImageView = view.findViewById(R.id.btn_post)
        progressBar = view.findViewById(R.id.progressBar)

        view.findViewById<LinearLayout>(R.id.layoutLeaderboard).setOnClickListener{
            navigateToFragment(LeaderboardFragment())
        }

        btnLogout.setOnClickListener {
            launch {
                clearData(requireContext())
                val loginFragment = MainActivity()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frmFragmentRoot, loginFragment)
                    .commit()

            }
        }
        btnPost.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.frmFragmentRoot, PostActivity.newInstance("add", ""))
                .commit()
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Inisialisasi ApiService
        apiService = retrofit.create(UserApi::class.java)
        val token = SessionManager.getToken(requireContext())

        // Panggil fungsi untuk mendapatkan semua data
        // Panggil fungsi untuk mendapatkan semua data
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response: Response<GetAllPost> = apiService.getAllPost("Bearer $token")
                recyclerView = view.findViewById(R.id.recyclerView)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.VISIBLE
                }

                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                    }

                    val getAllPost: GetAllPost? = response.body()
                    Log.d("YourFragment", "Response: ${getAllPost?.toString()}")
                    getAllPost?.let {
                        val dataItems: List<DataItem> = it.data?.filterNotNull() ?: emptyList()
                        val dataUser: List<DataItem> = it.data?.filterNotNull() ?: emptyList()


                        adapter = RecyclerViewAdapter(
                            dataItems,
                            context = requireContext(),
                            isVoted =  { dataItem: DataItem ->
                                isVoted(dataItem)
                            },
                            itemClickListener = object : OnItemClickPost<DataItem> {
                                override fun onItemClick(item: DataItem, isVoted: Boolean) {

                                    val bundle = Bundle()
                                    bundle.putBoolean("typeReply", false)
                                    bundle.putBoolean("isVoted", isVoted)
                                    bundle.putSerializable("postItem", item)

                                    val postDetailFragment = PostDetailFragment()
                                    postDetailFragment.arguments = bundle

                                    navigateToFragment(postDetailFragment)                                }
                            },
                            delUpvote = { item ->
                                delUpvote(item)
                            },
                            addUpvote = { item ->
                                addUpvote(item)
                            },
                            replyClickListener =  object : OnItemClickPost<DataItem> {
                                override fun onItemClick(item: DataItem, isVoted: Boolean) {
                                    val bundle = Bundle()
                                    bundle.putBoolean("typeReply", true)
                                    bundle.putBoolean("isVoted", isVoted)
                                    bundle.putSerializable("postItem", item)

                                    val postDetailFragment = PostDetailFragment()
                                    postDetailFragment.arguments = bundle

                                    navigateToFragment(postDetailFragment)
                                }
                            })
                        requireActivity().runOnUiThread {
                            // Set up the RecyclerView and adapter
                            recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                            recyclerView.adapter = adapter
                        }
                    }

                } else {

                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.VISIBLE
                    }
                    Log.e("YourFragment", "Error: ${response.message()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("YourFragment", "Error body: $errorBody")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.VISIBLE
                }
                Log.e("YourFragment", "Exception: ${e.message}")
            }
        }

    }
    private fun updateLikeCount() {
        likeCountTextView.text = likeCount.toString()
    }

    fun navigateToFragment(fragment: Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        if (transaction != null) {
            transaction.replace(R.id.frmFragmentRoot, fragment)
            transaction.addToBackStack(null) // Optional: Add to back stack for back navigation
            transaction.commit()
        }
    }

    fun addUpvote(item: DataItem) {

    }

    fun delUpvote(item: DataItem) {

    }

    fun isVoted(item: DataItem): Boolean {
        return false
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeActivity.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeActivity().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    }



