package com.example.gupshup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.gupshup.Models.UserModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import java.util.concurrent.TimeUnit

class ManageOTP : AppCompatActivity() {
    private lateinit var auth :FirebaseAuth
    private  var number:String= ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var storedVerificationId: String? = ""
    private lateinit var user:FirebaseUser
    private var authStateListener: FirebaseAuth.AuthStateListener? = null
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_otp)

        auth = FirebaseAuth.getInstance()
        authStateListener = FirebaseAuth.AuthStateListener {
            val user = auth.currentUser
            if (user != null) {
                updateUI(user)
            } else {
                Toast.makeText(this, "User Not Null", Toast.LENGTH_SHORT).show()
            }
        }
        database = FirebaseDatabase.getInstance()
        val intent = intent
        number = intent.getStringExtra("phoneNumber")!!
        val verifyOTP: Button  = findViewById(R.id.btnVerifyOTP)
        val otp:EditText  = findViewById(R.id.etOTP)
        Toast.makeText(this, "OTP is send to your number $number", Toast.LENGTH_LONG).show()
        callbacks = object:PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                storedVerificationId = p0
                resendToken = p1
            }

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                Toast.makeText(applicationContext, "Sign in Successfully",Toast.LENGTH_LONG).show()
                signInWithPhoneAuthCredential(p0)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(applicationContext, "Sign in failed",Toast.LENGTH_LONG).show()

            }


        }
        sendOTP(number)
        verifyOTP.setOnClickListener {
            if(otp.text.toString().length<6){
                Toast.makeText(this, "Please provide OTP", Toast.LENGTH_LONG).show()
            }
            else if(otp.text.toString().trim()!=storedVerificationId)
            {
            Toast.makeText(this, "Please Provide Correct OTP", Toast.LENGTH_SHORT).show()
            }
            else
            {
                user = auth.currentUser!!
                val userModel = UserModel()
                userModel.userId = user.uid
                userModel.username = user.displayName
                userModel.picture = Objects.requireNonNull(user.photoUrl).toString()
                database.reference.child("users").child(user.uid).setValue(userModel)
                updateUI(user)
            }
        }
    }

    private fun sendOTP(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener!!)
        val currentUser = auth.currentUser
        if (currentUser != null) {
            updateUI(currentUser)
        }
        }

    private fun updateUI(firebaseUser:FirebaseUser) {
        if(firebaseUser.equals(null)) {
         Toast.makeText(this, "Cannot update UI", Toast.LENGTH_LONG).show()
        }
        else{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this, "Sign in Successfully",Toast.LENGTH_LONG).show()
                    val userModel = UserModel()

                    userModel.userId = user.uid
                    userModel.username = user.displayName
                    userModel.picture = Objects.requireNonNull(user.photoUrl).toString()
                    database.reference.child("users").child(user.uid).setValue(userModel)
                    updateUI(user)
                } else {
                    // Sign in failed, display a message and update the UI
                    Toast.makeText(this, "Sign in failed",Toast.LENGTH_LONG).show()
                }
            }
    }
}