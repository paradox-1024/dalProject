package com.app.test_nav.Session

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.app.test_nav.Login
import com.app.test_nav.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginPref {
    lateinit var pref:SharedPreferences
    lateinit var editor:SharedPreferences.Editor
    lateinit var con:Context
    var PRIVATEMODE:Int=0

    constructor(con:Context){
        this.con=con
        pref=con.getSharedPreferences(PREF_NAME,PRIVATEMODE)
        editor=pref.edit()
    }
    companion object{
        val PREF_NAME ="Login_Preference"
        val IS_LOGIN="isLoggedIn"
        val USERNAME="username"
        val EMAIL="email"
    }
    fun createLoginSession(username:String,email:String){
        editor.putBoolean(IS_LOGIN,true);
        editor.putString(USERNAME,username)
        editor.putString(EMAIL,email)
        editor.commit()
    }

    fun getUsername():String{
        var username=""

        return username
    }
    fun checkLogin(){
        if(!this.isLoggedIn()){
            var i : Intent = Intent(con,MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            con.startActivity(i)
        }
    }

    fun getUserDetails():HashMap<String,String>{
        var user:Map<String,String> = HashMap<String,String>()
        (user as HashMap).put(USERNAME,pref.getString(USERNAME,null)!!)
        (user as HashMap).put(EMAIL,pref.getString(EMAIL,null)!!)
        return user
    }

    fun logout(){
        editor.clear()
        editor.commit()
        Firebase.auth.signOut()
        var i:Intent= Intent(con,Login::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        con.startActivity(i)
    }
    fun isLoggedIn():Boolean{
        return  pref.getBoolean(IS_LOGIN,false)
    }
}