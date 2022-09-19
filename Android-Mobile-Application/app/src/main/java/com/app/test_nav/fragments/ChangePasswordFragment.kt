package com.app.test_nav.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.app.test_nav.R
import com.app.test_nav.Session.LoginPref
import com.app.test_nav.Signup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.content.SharedPreferences
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.app.test_nav.Login
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Matcher
import java.util.regex.Pattern


private lateinit var editCurPass: EditText
private lateinit var editNewPass: EditText
private lateinit var btnChangePass: Button
private lateinit var mAuth: FirebaseAuth
lateinit var session:LoginPref
var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"



class ChangePassword : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false)
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
        mAuth = FirebaseAuth.getInstance()
        editCurPass=view.findViewById(R.id.edit_cur_pass)
        editNewPass=view.findViewById(R.id.edit_new_pass)
        btnChangePass=view.findViewById(R.id.btn_change_password)
        session= LoginPref(this.requireActivity())
        var user:HashMap<String,String> = session.getUserDetails()
        var userEmail= user.get(LoginPref.EMAIL)


        btnChangePass.setOnClickListener {
            val curPass=editCurPass.text.toString().trim()
            val newPass=editNewPass.text.toString().trim()
            if(curPass.isNotEmpty()&&newPass.isNotEmpty()) {

                if (isValidPassword(newPass)) {

                    val user = Firebase.auth.currentUser!!
                    val credential = EmailAuthProvider.getCredential(userEmail.toString(), curPass);
                    // Prompt the user to re-provide their sign-in credentials
                    user.reauthenticate(credential)
                        .addOnCompleteListener(this.requireActivity()) { task ->
                            if (task.isSuccessful) {
                                user.updatePassword(newPass).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            activity?.applicationContext,
                                            "Password Updated Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        var fr = getFragmentManager()?.beginTransaction()
                                        fr?.replace(R.id.frameLayout, Home(),"Home")
                                        fr?.commit()
                                    } else {
                                        Toast.makeText(
                                            activity?.applicationContext,
                                            "Error in Updating Password",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                            } else {
                                Toast.makeText(
                                    activity?.applicationContext,
                                    "Current password is incorrect",
                                    Toast.LENGTH_SHORT
                                ).show()
                                editCurPass.setText("")
                            }

                        }
                }
                else{
                    Toast.makeText(activity?.applicationContext, "New Password must contain 8 characters, 1 Uppercase, 1 Number and 1 Symbol", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(activity?.applicationContext, "Current password and New Password is Required", Toast.LENGTH_SHORT).show()
            }
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

    fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }
}