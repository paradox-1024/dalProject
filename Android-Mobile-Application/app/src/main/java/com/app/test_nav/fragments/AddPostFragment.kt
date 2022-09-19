package com.app.test_nav.fragments

import android.app.Activity
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
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import com.app.test_nav.MainActivity
import com.app.test_nav.R
import com.app.test_nav.Session.LoginPref
import androidx.navigation.fragment.findNavController
import com.app.test_nav.Login
import com.app.test_nav.models.PostModel
import com.app.test_nav.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

private const val TAG = "AddPost"
private const val PHOTO_CODE = 123

class AddPostFragment : Fragment() {

    private var signedInUser: UserModel? =null
    lateinit var toggle: ActionBarDrawerToggle

    //this is the FirebaseFirestore initialization
    private lateinit var firestoreDb : FirebaseFirestore
    private lateinit var storageReference: StorageReference
    private lateinit var postTitle: EditText
    private lateinit var postDesc: EditText
    private lateinit var postLocation: EditText
    private lateinit var imageView: ImageView
    private lateinit var btnPost: Button
    private lateinit var btnUploadImages: Button
    private var imageUri: Uri? = null
    lateinit var session: LoginPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storageReference = FirebaseStorage.getInstance().reference
        firestoreDb = FirebaseFirestore.getInstance()

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
        return inflater.inflate(R.layout.fragment_add_post, container, false)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {

                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postTitle = view.findViewById(R.id.postTitleInputField)
        postDesc = view.findViewById(R.id.postDescInputField)
        postLocation = view.findViewById(R.id.postLocationInputField)
        btnPost = view.findViewById(R.id.btn_add_post)
        imageView = view.findViewById(R.id.imageView)
        btnUploadImages = view.findViewById(R.id.btn_upload_images)

        btnUploadImages.setOnClickListener{
            Log.i(TAG, "Open image picker on device")
            val imagesIntent = Intent(Intent.ACTION_GET_CONTENT)
            Log.i(TAG, "in here 1")

            //open the feature of application that can provide images like gallery
            imagesIntent.type = "image/*"
            Log.i(TAG, "in here 2")
            Log.i(TAG, "in here 3")
//            startActivityForResult(imagesIntent, PHOTO_CODE)
        }

        btnPost.setOnClickListener {
            errorCheckPostOperation()
        }

    }

    //function to handle exceptions and errors in post operation
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
        else if(imageUri ==null){
            Toast.makeText(activity?.applicationContext,"There is no image selected", Toast.LENGTH_SHORT).show()
            return

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


    //function to access images for gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i(TAG, "in here main function")
        val imageView = view?.findViewById<ImageView>(R.id.imageView)

        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PHOTO_CODE){
            if (resultCode == Activity.RESULT_OK){
                Log.i(TAG, "in if statement")

                //location of the photo selected by user
                imageUri = data?.data
                Log.i(TAG, "imageUri: $imageUri")
                imageView?.setImageURI(imageUri)


            }else {
                Toast.makeText(activity?.applicationContext, "Uplaod Images action canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }


    //Function to send post object to firebase and add to posts collection
    private fun savePost(titleText: String, descText:String, locationText: String =""){
        btnPost.isEnabled = false

        //the post functionality
        //upload image to Firebase storage
        val imageUploadUri = imageUri as Uri
        val imageRef = storageReference.child("images/${System.currentTimeMillis()}-photo.jpg")
        imageRef.putFile(imageUploadUri)
            .continueWithTask { imageUploadTask ->
                //retrieve image url of uploaded image
                imageRef.downloadUrl
            } .continueWithTask { downloadUrlTask ->
                //create the post object with all user input and add it to posts collection
                val post = PostModel(
                    titleText,
                    System.currentTimeMillis(),
                    descText,
                    locationText,
                    downloadUrlTask.result.toString(),
                    signedInUser
                )
                firestoreDb.collection("posts").add(post)
            }.addOnCompleteListener { postCreationTask ->

                btnPost.isEnabled = true

                if (!postCreationTask.isSuccessful){
                    Log.i(TAG,"Issues during Firebase operations", postCreationTask.exception)
                    Toast.makeText(activity?.applicationContext,"Error in uploading post", Toast.LENGTH_SHORT).show()
                }
                postTitle.text.clear()
                postDesc.text.clear()
                postLocation.text.clear()
                imageView.setImageResource(0)
                Toast.makeText(activity?.applicationContext, "Your post has been added successfully!", Toast.LENGTH_SHORT).show()
                var fr = getFragmentManager()?.beginTransaction()
                fr?.replace(R.id.frameLayout, Home(),"Home")
                fr?.commit()
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