package com.app.test_nav.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.icu.number.SimpleNotation
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.util.MonthDisplayHelper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.test_nav.Communicator
import com.app.test_nav.Login
import com.app.test_nav.R
import com.app.test_nav.Session.LoginPref
import com.app.test_nav.adapters.MyPostRecyclerItemAdapter
import com.app.test_nav.gestures.SwipeNoteCallBack
import com.app.test_nav.models.PostInfo
import com.app.test_nav.models.UserModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewMyPostFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewMyPostFragment : Fragment() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var postArrayList : ArrayList<PostInfo>
    private lateinit var mypostrecyclerItemAdapter: MyPostRecyclerItemAdapter
    private lateinit var progressBar2: ProgressBar
    private lateinit var firebase : FirebaseFirestore
    private  lateinit var communicator: Communicator
    private lateinit var mContext : Context
    private lateinit var itemTouchHelper : ItemTouchHelper

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    // Leave empty do disable back press or
                    // write your code which you want
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_my_post, container, false)
        communicator=activity as Communicator
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ViewMyPostFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ViewMyPostFragment().apply {
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

        requireActivity().setTitle("My posts");
        progressBar2=view.findViewById(R.id.mypostprogressBar)
        progressBar2.setVisibility(View.VISIBLE);

        recyclerView = view.findViewById(R.id.mypostsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        postArrayList = arrayListOf()
        mypostrecyclerItemAdapter = MyPostRecyclerItemAdapter (postArrayList)
        recyclerView.adapter = mypostrecyclerItemAdapter
//        val itemTouchHelper= ItemTouchHelper(simpleCallBack)
//        itemTouchHelper.attachToRecyclerView(recyclerView)
        EventChangeListner()

        val swipeNoteCallBack = object : SwipeNoteCallBack(mContext, recyclerView) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //swapped left and right delete edit functionalities
                if (direction == ItemTouchHelper.LEFT) {
                    deletePostConfirm(postArrayList[viewHolder.adapterPosition],viewHolder.adapterPosition)
                    mypostrecyclerItemAdapter.notifyDataSetChanged()
                } else if (direction == ItemTouchHelper.RIGHT) {
                    editPost(postArrayList[viewHolder.adapterPosition])

                }
            }
        }

        itemTouchHelper = ItemTouchHelper(swipeNoteCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerView)



    }


    private fun EventChangeListner() {

        val email=FirebaseAuth.getInstance().currentUser?.email
        firebase = FirebaseFirestore.getInstance()
        val postRef=firebase.collection("posts").whereEqualTo("user.userEmail",email).orderBy("postDate", Query.Direction.DESCENDING)
        postRef.addSnapshotListener(object: EventListener<QuerySnapshot> {
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {
                if(error != null) {
                    Log.e("firebase Error", error.message.toString())
                    progressBar2.setVisibility(View.GONE);
                    return
                }
                progressBar2.setVisibility(View.GONE);
                var size=value?.documentChanges?.size
                if(size==0){
                    Toast.makeText(activity?.applicationContext, "No Posts", Toast.LENGTH_SHORT).show()
                }

                for (dc: DocumentChange in value?.documentChanges!!) {

                    if(dc.type == DocumentChange.Type.ADDED) {

                        val data = dc.document.data
                        val id= dc.document.id
                        Log.i("My post Data fetched", id)
                        val temp : HashMap<String, String>? = data.get("user") as? HashMap<String, String>
                        val user = UserModel(
                            temp?.get("userName").toString(),
                            temp?.get("userEmail").toString(),
                        )
                        val post = PostInfo(
                            id,
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

                mypostrecyclerItemAdapter.notifyDataSetChanged()


                }
            })
    }


    private fun editPost(postInfo: PostInfo) {
        communicator.passData(postInfo.postId.toString())
    }

    private fun deletePostConfirm(postInfo: PostInfo,position:Int) {
        val dialogBuilder = AlertDialog.Builder(this.requireActivity()!!)
        dialogBuilder.setMessage("Do you want to delete post?")
            .setCancelable(false)

            .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                    dialog, id -> deletePost(postInfo,position)
            })

            .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle("Please Confirm")
        // show alert dialog
        alert.show()
    }
    private fun deletePost(postInfo: PostInfo,position:Int){
        firebase = FirebaseFirestore.getInstance()
        val documentId = postInfo.postId
        firebase.collection("posts").document(documentId)
            .delete()
            .addOnCompleteListener { postDeleteTask ->
                if (!postDeleteTask.isSuccessful){
                    Log.i("View My Post ","Issues during Firebase operations", postDeleteTask.exception)
                    Toast.makeText(activity?.applicationContext,"Error in deleting post!",
                        Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(activity?.applicationContext, "Deleted successfully!",
                    Toast.LENGTH_SHORT).show()
                mypostrecyclerItemAdapter.notifyItemRemoved(position)
                postArrayList.removeAt(position)
            }

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