package com.example.gupshup.SignupOptions

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gupshup.ManageOTP
import com.example.gupshup.R
import com.hbb20.CountryCodePicker

class PhoneAuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_auth)
        val ccp:CountryCodePicker = findViewById(R.id.cpp)
        val phoneNumber: EditText  = findViewById(R.id.etPhoneNumber)
        val sendOtp:Button   =findViewById(R.id.btnsendOTP)
        sendOtp.setOnClickListener {
            if(phoneNumber.text.toString().length<7){
                Toast.makeText(this, "Please Type the number", Toast.LENGTH_LONG).show()
            }
            else{
                val intent = Intent(this, ManageOTP::class.java)
                val number  = ccp.selectedCountryCode
                val phone = phoneNumber.text.toString()
                intent.putExtra("phoneNumber", "+$number$phone")
                                startActivity(intent)
            }
        }
    }
}