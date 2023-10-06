package com.example.mulay.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mulay.Fragment.MainPage.FragmentPageAdd
import com.example.mulay.Fragment.MainPage.FragmentPageHome
import com.example.mulay.Fragment.MainPage.FragmentPageProfile
import com.example.mulay.Fragment.MainPage.FragmentPageSearch
import com.example.mulay.R
import com.example.mulay.databinding.PageMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class PageMain : AppCompatActivity() {
    private lateinit var binding: PageMainBinding
    private var clicked : Boolean = false
    private var currentPosition : String = "home"
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var userRef : DatabaseReference


    val fadeIn : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.anim_fade_in) }
    val fadeOut : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.anim_fade_out) }
    val rotateClose : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    val rotateOpen : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    val fromBottom : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    val toBottom : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PageMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(FragmentPageHome())

        //HIDDING FLOATING
        binding.musicUploadButton.visibility = View.GONE
        binding.uploadBbutton.visibility = View.GONE
        binding.linearEditProfile.visibility = View.GONE

        //BELOW THIS IS ABOUT BOTTOM NAV
        binding.musicUploadButton.startAnimation(fadeOut)
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> {
                    currentPosition = "home"
                    replaceFragment(FragmentPageHome())
                    //Edit Button Hide
                    hideButtonEdit()
                    //Upload Button Hide
                    hideButtonUpload()
                }
                R.id.search -> {
                    currentPosition = "search"
                    replaceFragment(FragmentPageSearch())
                    //Edit Button Hide
                    hideButtonEdit()
                    //Upload Button Hide
                    hideButtonUpload()
                }
                R.id.add -> {
                    currentPosition = "add"
                    replaceFragment(FragmentPageAdd())
                    //Edit Button Hide
                    showUploadButton()
                    //Upload Button Show
                    hideButtonEdit()
                }
                R.id.profile -> {
                    currentPosition = "profile"
                    replaceFragment(FragmentPageProfile())
                    //Edit Button Show
                    showEditButton()
                    //Upload Button Hide
                    hideButtonUpload()
                }
                else -> {}
            }
            binding.bottomNavigationView.menu.findItem(R.id.search)?.isEnabled = currentPosition != "search"
            binding.bottomNavigationView.menu.findItem(R.id.home)?.isEnabled = currentPosition != "home"
            binding.bottomNavigationView.menu.findItem(R.id.add)?.isEnabled = currentPosition != "add"
            binding.bottomNavigationView.menu.findItem(R.id.profile)?.isEnabled = currentPosition != "profile"
            true
        }
    }

    private fun hideButtonEdit() {
        if(binding.linearEditProfile.visibility == View.VISIBLE){
            binding.linearEditProfile.startAnimation(fadeOut)
            binding.linearEditProfile.visibility = View.GONE
            binding.linearEditProfile.setOnClickListener(null)
        }else{}
    }

    private fun showUploadButton() {
        if(binding.uploadBbutton.visibility == View.GONE){
            binding.uploadBbutton.startAnimation(fadeIn)
            binding.uploadBbutton.visibility = View.VISIBLE
            binding.uploadBbutton.setOnClickListener{
                onAddButtonClicked()
            }
            binding.musicUploadButton.setOnClickListener{
                val intent = Intent(this, PageUpload::class.java)
                startActivity(intent)
            }
        }else{ }
    }

    private fun showEditButton() {
        if(binding.linearEditProfile.visibility == View.GONE){
            binding.linearEditProfile.startAnimation(fadeIn)
            binding.linearEditProfile.visibility = View.VISIBLE
            binding.linearEditProfile.setOnClickListener{
                val intent = Intent(this, PageEditProfile::class.java)
                startActivity(intent)
            }
        }else{ }
    }

    private fun hideButtonUpload() {
        if(binding.uploadBbutton.visibility == View.VISIBLE){
            binding.musicUploadButton.startAnimation(fadeOut)
            binding.uploadBbutton.startAnimation(fadeOut)
            binding.musicUploadButton.visibility = View.GONE
            binding.uploadBbutton.visibility = View.GONE
            binding.musicUploadButton.setOnClickListener(null)
            binding.uploadBbutton.setOnClickListener(null)
        }else{ }
        clicked = false
    }

    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked : Boolean) {
        if(!clicked){
            binding.musicUploadButton.startAnimation(fromBottom)
            binding.uploadBbutton.startAnimation(rotateOpen)
        }else{
            binding.musicUploadButton.startAnimation(toBottom)
            binding.uploadBbutton.startAnimation(rotateClose)
        }
    }

    private fun setVisibility(clicked : Boolean) {
        if(clicked){
            binding.musicUploadButton.visibility = View.GONE
        }else{
            binding.musicUploadButton.visibility = View.INVISIBLE
        }
    }

    private fun setClickable(clicked: Boolean){
        if(!clicked){
            binding.musicUploadButton.isClickable = true
        }else{
            binding.musicUploadButton.isClickable = false
        }
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerMain, fragment)
        fragmentTransaction.commit()
    }
}
