package com.app.test_nav

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import android.view.Gravity
import android.widget.Toast
import com.app.test_nav.models.UserModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.regex.Matcher
import java.util.regex.Pattern

class Signup : AppCompatActivity() {
    private lateinit var editEmail: EditText
    private lateinit var editUserName: EditText
    private lateinit var editPassword: EditText
    private lateinit var editCPassword: EditText
    private lateinit var btnSignup: Button
    private lateinit var mAuth: FirebaseAuth
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private val database= FirebaseFirestore.getInstance()
    private val collectionName="users"

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        mAuth = FirebaseAuth.getInstance()
        editUserName=findViewById(R.id.edit_name)
        editEmail=findViewById(R.id.edit_email)
        editPassword=findViewById(R.id.edit_password)
        editCPassword=findViewById(R.id.edit_cpassword)
        btnSignup=findViewById(R.id.btn_signup)

        btnSignup.setOnClickListener {
            val name = editUserName.text.toString().trim()
            val email =editEmail.text.toString().trim().lowercase()
            val pass= editPassword.text.toString().trim()
            val cpass= editCPassword.text.toString().trim()

            if(name.isNotEmpty()){
                if(email.isNotEmpty()){
                    if(pass.isNotEmpty()&&cpass.isNotEmpty()){
                        if(pass.equals(cpass)){

                            if (editEmail.text.toString().trim { it <= ' ' }.matches(emailPattern.toRegex())){
                                if(isValidPassword(pass)){
                                    btnSignup.isEnabled=false
                                    signup(name,email,pass)
                                }
                                else{
                                    val myToast = Toast.makeText(applicationContext,"Password must contain 8 characters, 1 Uppercase, 1 Number and 1 Symbol",Toast.LENGTH_LONG)
                                    myToast.setGravity(Gravity.LEFT,200,200)
                                    myToast.show()
                                }
                            }
                            else{
                                val myToast = Toast.makeText(applicationContext,"Please Enter Valid Email Id",Toast.LENGTH_LONG)
                                myToast.setGravity(Gravity.LEFT,200,200)
                                myToast.show()
                            }
                        }
                        else{
                            val myToast = Toast.makeText(applicationContext,"Password doesn't Match",Toast.LENGTH_SHORT)
                            myToast.setGravity(Gravity.LEFT,200,200)
                            myToast.show()
                            editPassword.setText("")
                            editCPassword.setText("")
                        }
                    }
                    else{
                        Toast.makeText(
                            baseContext, "Password and Confirm Password is Required to Signup",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                else{
                    Toast.makeText(
                        baseContext, "Email is Required to Signup",
                        Toast.LENGTH_SHORT
                    ).show()
                    editEmail.setText("")
                }
            }
            else{
                Toast.makeText(
                    baseContext, "Name is Required to Signup",
                    Toast.LENGTH_SHORT
                ).show()
                editUserName.setText("")
            }
        }
    }

    private fun signup (name:String,email:String,password:String){

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        btnSignup.isEnabled=true
                        val user = mAuth.currentUser
                        val check=saveUserinDB(name,email,user)
                        Toast.makeText(
                            this@Signup, "Sign Up Successfull!",
                            Toast.LENGTH_SHORT
                        ).show()
                        if(check)
                        {
                        Firebase.auth.signOut()
                        val intent = Intent(this@Signup, Login::class.java)
                        startActivity(intent)
                        }
                        else{
                            Toast.makeText(
                                this@Signup, "Some Error occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@Signup, "Unable to Sign Up!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
    }

    private fun saveUserinDB(name: String, email: String,user: FirebaseUser?): Boolean {
        val newuser= UserModel(name,email);
        if (user != null) {
            database.collection(collectionName).document(user.uid).set(newuser)
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Added document")
                }
                .addOnFailureListener {  exception ->
                    Log.w(ContentValues.TAG, "Error adding document $exception")
                }
        }
        return true
    }

    fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }

}