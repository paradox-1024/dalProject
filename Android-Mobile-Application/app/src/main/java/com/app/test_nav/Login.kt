package com.app.test_nav

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import android.widget.Toast
import com.app.test_nav.Session.LoginPref
import com.app.test_nav.models.UserModel
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {

    private var signedInUser: UserModel? =null
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignup: Button
    lateinit var session:LoginPref
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    private lateinit var mAuth:FirebaseAuth
    private val database= FirebaseFirestore.getInstance()
    private val collectionName="users"

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        editEmail=findViewById(R.id.edit_email)
        editPassword=findViewById(R.id.edit_password)
        btnLogin=findViewById(R.id.btn_login)
        btnSignup=findViewById(R.id.btn_signup)
        session = LoginPref(this)
        btnSignup.setOnClickListener {

            val intent= Intent(this,Signup::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener {

            if (editEmail.text.toString().trim { it <= ' ' }.matches(emailPattern.toRegex())) {
                val email =editEmail.text.toString().trim().lowercase()
                val password = editPassword.text.toString().trim()

                if (email.isEmpty()||password.isEmpty()) {
                    Toast.makeText(
                        baseContext, "Please enter Email and Password to login",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else{
                    btnLogin.isEnabled=false
                    login(email,password);
                    }

            }
            else {
                Toast.makeText(applicationContext, "Invalid email address", Toast.LENGTH_SHORT).show()
            }

        }
        if(session.isLoggedIn()) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

    }

    private fun login( email:String,password:String){

            var username:String="USER";
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        userLogin()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext, "Please use correct credentials to Login",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    btnLogin.isEnabled=true
                }

    }

       fun  getUserDetails(email:String) : String {
        var userName:String="USER";
        database.collection(collectionName).whereEqualTo("userEmail",email).get()
            .addOnSuccessListener { querySnapshot ->
                userName=querySnapshot.first().data.getValue("userName").toString();
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents $exception")
                userName="USER";
            }
        return userName
    }
   fun userLogin(){
       //get the signed in user information
       database.collection("users")
           .document(FirebaseAuth.getInstance().currentUser?.uid as String)
           .get()
           .addOnSuccessListener { userSnapshot ->
               signedInUser = userSnapshot.toObject(UserModel::class.java)
               Log.i("userLogin Function", "signed in user: $signedInUser")
               session.createLoginSession(signedInUser?.userName.toString(),signedInUser?.userEmail.toString())
               if(session.isLoggedIn()) {
                   val intent = Intent(applicationContext, MainActivity::class.java)
                   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                   startActivity(intent)
                   finish()
               }

           }
           .addOnFailureListener{ exception ->
               Log.i("userLogin Function", "Failure fetching the user who has signed in")
               Toast.makeText(this, "Unable to Sign In", Toast.LENGTH_SHORT).show()

           }
    }
}