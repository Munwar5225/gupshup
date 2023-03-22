package com.example.gupshup

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import android.os.Bundle
import com.google.firebase.auth.FirebaseUser
import android.widget.Toast
import android.view.MenuInflater
import com.example.gupshup.R
import android.annotation.SuppressLint
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.example.gupshup.SignUpSignIn.SignInActivity
import com.example.gupshup.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null
    var auth: FirebaseAuth? = null
    var mAuthListener: AuthStateListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding!!.root)
        auth = FirebaseAuth.getInstance()
        auth = FirebaseAuth.getInstance()
        mAuthListener = AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                println("User logged in")
                Toast.makeText(this@MainActivity, "User Logged in", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("NonConstantResourceId", "SetTextI18n")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting -> binding!!.textView.text = "Setting"
            R.id.logout -> {
                auth!!.signOut()
                val intent = Intent(this@MainActivity, SignInActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }
}