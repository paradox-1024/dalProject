package com.app.test_nav

import android.content.Intent
import android.media.metrics.Event
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.Fragment
import com.app.test_nav.Session.LoginPref
import com.app.test_nav.adapters.RecyclerItemAdapter
import com.app.test_nav.fragments.*
import com.app.test_nav.models.PostModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.app.test_nav.models.PostInfo
import com.google.android.gms.common.data.DataHolder




class MainActivity : AppCompatActivity(),Communicator {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var session: LoginPref
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        session= LoginPref(this)
        session.checkLogin()

        var user:HashMap<String,String> = session.getUserDetails()
        var username = user.get(LoginPref.USERNAME)
        var email= user.get(LoginPref.EMAIL)

         drawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView =findViewById(R.id.nav_view)
        val headerView = navView.getHeaderView(0)
        val headerUsername=headerView.findViewById<TextView>(R.id.user_name)
        headerUsername.setText(username)
        val headerEmail=headerView.findViewById<TextView>(R.id.user_email)
        headerEmail.setText(email)
        changeFragment(Home(),"Home")

        toggle= ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);

        navView.setNavigationItemSelectedListener {

            when(it.itemId)
            {

                R.id.nav_home-> changeFragment(Home(),it.title.toString())
                R.id.nav_my_post-> changeFragment(ViewMyPostFragment(),it.title.toString())

                R.id.nav_add_post-> changeFragment(AddPostFragment(),it.title.toString())

                R.id.nav_change_pass-> changeFragment(ChangePassword(),it.title.toString())

                R.id.nav_logout-> logout()

            }
            true
        }

    }

    override fun onResume() {
        super.onResume()
        drawerLayout.closeDrawers();
        if(!session.isLoggedIn()){
            val intent = Intent(this, Login::class.java);
            startActivity(intent)
        }

    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item))
        {
            true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout(){
        session.logout()
    }


    private fun changeFragment(fragment: Fragment, title:String){
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
        drawerLayout.closeDrawers()
        setTitle(title)
    }

    override fun passData(postId: String) {
        var bundle=Bundle()
        bundle.putString("postId",postId)

        val transaction=this.supportFragmentManager.beginTransaction()
        val fragmentEditMyPost= EditMyPostFragment()
        fragmentEditMyPost.arguments=bundle
        transaction.replace(R.id.frameLayout,fragmentEditMyPost)
        transaction.commit()
    }

}