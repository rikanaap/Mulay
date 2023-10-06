package com.example.mulay.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mulay.R
import com.example.mulay.databinding.PageLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.shashank.sony.fancytoastlib.FancyToast
import kotlin.random.Random

class PageLogin : AppCompatActivity() {
    private lateinit var binding : PageLoginBinding
    private lateinit var buttonLogin : Button
    private lateinit var greetText : TextView
    private lateinit var firebaseAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PageLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Setting view
        buttonLogin = binding.buttonConfirmLogin
        greetText = binding.textSetGreetLogin
        setGreet()

        //Setting connection
        firebaseAuth = FirebaseAuth.getInstance()

        buttonLogin.setOnClickListener{
            userLogin()
        }
    }

    private fun userLogin() {
        val email = binding.editInputEmailLogin.text.toString()
        val password = binding.editInputPasswordLogin.text.toString()
        //Checking
        if(email.isEmpty()) binding.editInputEmailLogin.setError("Required")
        if(password.isEmpty()) binding.editInputPasswordLogin.setError("Required")

        if (email.isNotEmpty() && password.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    val intent = Intent(this, PageMain::class.java)
                    startActivity(intent)
                    finishAffinity()
                } else {
                    FancyToast.makeText(this, it.exception.toString(), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
                }
            }
        } else {
            FancyToast.makeText(this, "Empty Fields Are not Allowed!", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show()
        }
    }

    private fun setGreet() {
        val arrayHint = resources.getStringArray(R.array.registerGreet)
        val randomInt = Random.nextInt(0, arrayHint.size)
        val currentTextHint = arrayHint[randomInt]
        greetText.setText(currentTextHint)
    }
}