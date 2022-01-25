package com.ceinsys.togethringtaskdemo.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.ceinsys.togethringtaskdemo.R

class SplashScreen : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        mainViewModel.loginDone.observe(this,{ isLogin ->
            if (isLogin){
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 3000) // 3000 is the delayed time in milliseconds.
            }else{
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, LoginScreen::class.java)
                    startActivity(intent)
                    finish()
                }, 3000) // 3000 is the delayed time in milliseconds.
            }
        })
    }
}