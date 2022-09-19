package com.app.test_nav.fragments

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.test_nav.Login
import com.app.test_nav.R
import com.app.test_nav.Session.LoginPref
import com.app.test_nav.adapters.RecyclerItemAdapter
import com.app.test_nav.models.PostModel
import com.app.test_nav.models.UserModel
import com.google.firebase.firestore.*
import java.lang.Long

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var postArrayList : ArrayList<PostModel>
    private lateinit var recyclerItemAdapter: RecyclerItemAdapter
    private lateinit var progressBar1: ProgressBar
    private lateinit var firebase : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // threadpolicy to access data from internet
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)


        requireActivity().setTitle("Home");
        progressBar1=view.findViewById(R.id.progressBar)
        progressBar1.setVisibility(View.VISIBLE);

        recyclerView = view.findViewById(R.id.postsListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        postArrayList = arrayListOf()
        recyclerItemAdapter = RecyclerItemAdapter(postArrayList)

        recyclerView.adapter = recyclerItemAdapter
        EventChangeListner()
    }

    private fun EventChangeListner() {

        firebase = FirebaseFirestore.getInstance()
        firebase.collection("posts").orderBy("postDate", Query.Direction.DESCENDING)
            .addSnapshotListener(object: EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if(error != null) {
                        Log.e("firebase Error", error.message.toString())
                        return
                    }
                    progressBar1.setVisibility(View.GONE);
                    for (dc: DocumentChange in value?.documentChanges!!) {

                        if(dc.type == DocumentChange.Type.ADDED) {

                            val data = dc.document.data
                            val temp : HashMap<String, String>? = data.get("user") as? HashMap<String, String>
                            val user = UserModel(
                                temp?.get("userName").toString(),
                                temp?.get("userEmail").toString(),
                            )
                            val post = PostModel(
                                data?.get("postTitle").toString(),
                                data?.get("postDate") as Number,
                                data?.get("postDescription").toString(),
                                data?.get("postLocation").toString(),
                                data?.get("imageUrl").toString(),
                                user,
                            )
                            postArrayList.add(post)
                        }
                    }
                    recyclerItemAdapter.notifyDataSetChanged()
                }
            })
    }
    override fun onResume() {
        super.onResume()
        session= LoginPref(this.requireActivity())
        if(!session.isLoggedIn()){
            activity?.let{
                val intent = Intent (it, Login::class.java)
                it.startActivity(intent)
            }
        }
    }
}