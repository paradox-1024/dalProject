package com.app.test_nav.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.app.test_nav.R
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import com.app.test_nav.Login
import com.app.test_nav.models.PostInfo
import com.app.test_nav.models.PostModel
import com.app.test_nav.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.ktx.Firebase


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var postTitle: EditText
private lateinit var postDesc: EditText
private lateinit var postLocation: EditText
private lateinit var btnPost: Button
private var imageUri: Uri? = null
private var signedInUser: UserModel? =null
//this is the FirebaseFirestore initialization
private lateinit var firestoreDb : FirebaseFirestore


/**
 *
 * A simple [Fragment] subclass.
 * Use the [EditMyPostFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


private const val TAG = "EditPost"
class EditMyPostFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var firebase : FirebaseFirestore

    var postId:String?=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        firestoreDb = FirebaseFirestore.getInstance()

        //val user = FirebaseAuth.getInstance().currentUser
        //get the signed in user information

        firestoreDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                Log.i(TAG, "signed in user: $userSnapshot")
                signedInUser = userSnapshot.toObject(UserModel::class.java)
                Log.i(TAG, "signed in user: $signedInUser")

            }
            .addOnFailureListener{ exception ->
                Log.i(TAG, "Failure fetching the user who has signed in")

            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_edit_my_post, container, false)
        postId=arguments?.getString("postId")
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditMyPostFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditMyPostFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        postTitle = view.findViewById(R.id.mypostTitleInputField)
        postDesc = view.findViewById(R.id.mypostDescInputField)
        postLocation = view.findViewById(R.id.mypostLocationInputField)
        btnPost = view.findViewById(R.id.btn_update_post)

        requireActivity().setTitle("Edit Post");

        btnPost.setOnClickListener {
            errorCheckPostOperation()
        }

        firebase = FirebaseFirestore.getInstance()

       firebase.collection("posts").document(postId.toString()).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null) {
                        postTitle.setText(document.data?.get("postTitle").toString())
                        postDesc.setText( document.data?.get("postDescription").toString())
                        postLocation.setText(document.data?.get("postLocation").toString())
                    } else {
                        Log.d("Edit my post", "No such document")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Edit my post", "get failed with ", exception)
            }

    }

    private fun errorCheckPostOperation() {
        val titleText = postTitle.text.toString()
        val descText = postDesc.text.toString()
        val locationText = postLocation.text.toString()

        if (titleText.isEmpty() || descText.isEmpty()) {
            Toast.makeText(
                activity?.applicationContext, "Title and Description is Required to add a Post",
                Toast.LENGTH_SHORT
            ).show()
        }
        else if (signedInUser == null) {
            Toast.makeText(activity?.applicationContext, "No signed in user",  Toast.LENGTH_SHORT).show()

        }
        else{
            val dialogBuilder = AlertDialog.Builder(this.requireActivity()!!)
            if (locationText.isEmpty()) {
                // set message of alert dialog when location is not specified
                dialogBuilder.setMessage("Do you want to post without adding location details?")
                    .setCancelable(false)

                    .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                            dialog, id -> savePost(titleText,descText)
                    })

                    .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                            dialog, id -> dialog.cancel()
                    })

                val alert = dialogBuilder.create()
                alert.setTitle("Please Confirm")
                // show alert dialog
                alert.show()
            }
            else{
                // set confirmation alert message dialog when location is specified
                dialogBuilder.setMessage("Post the advertisement?")
                    .setCancelable(false)

                    .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                            dialog, id -> savePost(titleText,descText,locationText)
                    })

                    .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                            dialog, id -> dialog.cancel()
                    })

                val alert = dialogBuilder.create()
                alert.setTitle("Please Confirm")
                // show alert dialog
                alert.show()
            }
        }

    }

    private fun savePost(titleText: String, descText: String, locationText: String ="") {
        btnPost.isEnabled = false
        val ref = FirebaseDatabase.getInstance().getReference("posts").child(postId.toString())
       Log.d(TAG,"!!!!!!!!!!! Firebase reference: ${ref.toString()}")
        firestoreDb.collection("posts").document(postId.toString()).update("postTitle", titleText,
            "postLocation", locationText,
            "postDescription", descText,
        "postDate",System.currentTimeMillis()).addOnSuccessListener {
            Toast.makeText(activity?.applicationContext, "Your post has been Updated successfully!", Toast.LENGTH_SHORT).show()
            var fr = getFragmentManager()?.beginTransaction()
            fr?.replace(R.id.frameLayout, ViewMyPostFragment(),"My Post")
            fr?.commit()
        }
            .addOnFailureListener {
                Toast.makeText(activity?.applicationContext, "Some Error Occurred", Toast.LENGTH_SHORT).show()
            }

    }


}


