package com.example.mulay.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mulay.R

class PageDecider : AppCompatActivity() {
    private lateinit var buttonSignIn : LinearLayout
    private lateinit var buttonSignUp : LinearLayout
    private lateinit var greetText : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_decider)
        //Setting viee
        buttonSignIn = findViewById(R.id.linearDeciderSignIn)
        buttonSignUp = findViewById(R.id.linearDeciderSignUp)
        greetText = findViewById(R.id.textSetWelcomerDecider)

        animateWelcomerCharacter()

        //When decider clicked
        buttonSignIn.setOnClickListener{
            val intent = Intent(this, PageLogin::class.java)
            startActivity(intent)
        }

        buttonSignUp.setOnClickListener{
            val intent = Intent(this, PageRegister::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isTaskRoot) {
            finishAffinity() // Finish all activities in the task stack
            System.exit(0) // Exit the application process
        }
    }
    private fun animateWelcomerCharacter() {
        val currentTextHint = resources.getString(R.string.decide_text)
        greetText.text = ""

        for (i in currentTextHint.indices) {
            val index = i
            Handler().postDelayed({
                greetText.append(currentTextHint[index].toString())
            }, i * 30L)
        }
    }
}