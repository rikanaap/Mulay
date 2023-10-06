package com.example.mulay.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.mulay.R
import com.google.firebase.auth.FirebaseAuth

class PageSplash : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_splash)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w: Window = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        firebaseAuth = FirebaseAuth.getInstance()
        Handler().postDelayed({
            if(firebaseAuth.currentUser != null){
                val intent = Intent(this, PageMain::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, PageDecider::class.java)
                startActivity(intent)
                finish()
            }

        },2000)
    }
}