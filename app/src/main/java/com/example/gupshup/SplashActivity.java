package com.example.gupshup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gupshup.SignUpSignIn.SignUp;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread loc   =  new Thread(){
                public void run(){
                    try {
                        sleep(2500);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    finally {
                        Intent intent = new Intent(SplashActivity.this, SignUp.class);
                        startActivity(intent);
                        finish();
                    }
                }
        };
        loc.start();
    }

}