package com.example.mulay.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mulay.Data.Users
import com.example.mulay.R
import com.example.mulay.databinding.PageRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.shashank.sony.fancytoastlib.FancyToast
import kotlin.random.Random

class PageRegister : AppCompatActivity() {
    private lateinit var binding : PageRegisterBinding
    private lateinit var buttonRegister : Button
    private lateinit var greetText : TextView
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var dbRef : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PageRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Setting view
        buttonRegister = binding.buttonRegister
        greetText = binding.textSetGreetRegister

        //Setting connection
        firebaseAuth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("User")

        setGreet()
        buttonRegister.setOnClickListener{
            userRegister()
        }
    }

    private fun userRegister() {
        //Getting user input
        val username = binding.editInputUsernamelRegister.text.toString()
        val email = binding.editInputEmailRegister.text.toString()
        val password = binding.editInputPasswordRegister.text.toString()
        val confirmPass = binding.editInputConfirmPasswordRegister.text.toString()

        //When empty
        if(username.isEmpty()) binding.editInputUsernamelRegister.setError("Required")
        if(email.isEmpty()) binding.editInputEmailRegister.setError("Required")
        if(email.isNotEmpty()){

        }
        if(password.isEmpty()) FancyToast.makeText(this, "Empty Fields Are not Allowed!", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show()
        if(password.isNotEmpty())
        if(confirmPass.isEmpty()) FancyToast.makeText(this, "Empty Fields Are not Allowed!", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show()

        //When all not empty
        if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPass.isNotEmpty()) {
            if (!isEmailValid(email)){
                FancyToast.makeText(this, "Please use a valid email", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show()
            }else{
                if (password == confirmPass) {
                    val progressDialog = ProgressDialog(this)
                    progressDialog.setMessage("Signing Up...")
                    progressDialog.setCancelable(false)
                    progressDialog.show()

                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val uid = firebaseAuth.currentUser!!.uid
                                val userData = Users(uid, username, email, password, "", "")
                                dbRef.child(uid).setValue(userData).addOnCompleteListener { //Sending data to database
                                    if (progressDialog.isShowing) progressDialog.dismiss()
                                    val intent = Intent(this, PageMain::class.java)
                                    startActivity(intent)
                                    finishAffinity()
                                }.addOnFailureListener {
                                    FancyToast.makeText(
                                        this, "Failed to create an account", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
                                }
                            }
                        }.addOnFailureListener{
                            progressDialog.dismiss()
                            FancyToast.makeText(this, it.message, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
                        }
                }else {
                    FancyToast.makeText(this, "Password is not matching", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
                }
            }
        } else { }
    }

    private fun setGreet() {
        val arrayHint = resources.getStringArray(R.array.registerGreet)
        val randomInt = Random.nextInt(0, arrayHint.size)
        val currentTextHint = arrayHint[randomInt]
        greetText.setText(currentTextHint)
    }

    private fun isEmailValid(email: String): Boolean {
        val pattern = Regex("[a-zA-Z0-9._%+-]+@gmail.com\\b")
        return pattern.matches(email)
    }

}