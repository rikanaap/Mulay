package com.example.mulay.Activity

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mulay.Data.Song
import com.example.mulay.R
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.firebase.database.FirebaseDatabase
import com.shashank.sony.fancytoastlib.FancyToast
import com.squareup.picasso.Picasso

class PageEditMusic : AppCompatActivity() {
    private lateinit var editConfirm : ImageView
    private lateinit var editCancel : ImageView
    private lateinit var imageMusicCover : ImageView
    private lateinit var editMusicTitle : EditText
    private lateinit var editMusicLyrics : EditText
    private var currentPublicity : Boolean = false

    //to update
    private lateinit var musicId : String
    private lateinit var musicTitle : String
    private lateinit var musicStorageId : String
    private lateinit var musicPublisherId : String
    private lateinit var musicReleaseDate : String
    private lateinit var musicLyrics : String
    private lateinit var musicTranslateLyrics : String
    private lateinit var musicDuration : String
    private var musicPublicity : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_edit_music)
        //Setting view
        editCancel = findViewById(R.id.imageConfirmCancelEdit)
        editConfirm = findViewById(R.id.imageConfirmEdit)
        imageMusicCover = findViewById(R.id.imageSetMusicCoverEdit)
        editMusicLyrics = findViewById(R.id.editSetLyircsEdit)
        editMusicTitle = findViewById(R.id.editSetMusicTitleEdit)
        //Preparing intent data
        prepareData()

        //When clicked
        editCancel.setOnClickListener{
            finish()
        }

        editConfirm.setOnClickListener{
            openEditDialog()
        }

    }

    private fun openEditDialog() {
        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.dialog_confirmation_publicity, null)
        mDialog.setView(mDialogView)
        val alertDialog = mDialog.show()

        currentPublicity = musicPublicity
        val arrayPublicity = resources.getStringArray(R.array.uploadPublicity)
        val arraySwitchTo = resources.getStringArray(R.array.makeIt)
        val titlePublicity : TextView = mDialogView.findViewById(R.id.topTitlePublicityBreakSection)
        val switchPublicity : MaterialSwitch = mDialogView.findViewById(R.id.switchPublicityEdit)
        val saveEditButton : ImageView = mDialogView.findViewById(R.id.imageSaveEdit)
        val textPublicity : TextView = mDialogView.findViewById(R.id.textSetPublicityEdit)
        switchPublicity.isChecked = currentPublicity

        if(currentPublicity == false){
            titlePublicity.text = arraySwitchTo[0]
            textPublicity.text = arrayPublicity[0]
        }else if(currentPublicity == true){
            titlePublicity.text = arraySwitchTo[1]
            textPublicity.text = arrayPublicity[1]
        }

        if(currentPublicity == musicPublicity){
            saveEditButton.setImageResource(R.drawable.ic_publicity_cancel)
        } else{
            saveEditButton.setImageResource(R.drawable.ic_publicity_check)
        }
        switchPublicity.setOnCheckedChangeListener { _, isChecked ->
            currentPublicity = isChecked
            if(currentPublicity == false){
                titlePublicity.text = arraySwitchTo[0]
                textPublicity.text = arrayPublicity[0]
            }else if(currentPublicity == true){
                titlePublicity.text = arraySwitchTo[1]
                textPublicity.text = arrayPublicity[1]
            }
            if(currentPublicity == musicPublicity){
                saveEditButton.setImageResource(R.drawable.ic_publicity_cancel)
            } else{
                saveEditButton.setImageResource(R.drawable.ic_publicity_check)
            }
        }

        saveEditButton.setOnClickListener{
            saveEditedData()
            finish()
        }
    }

    private fun saveEditedData() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Updateing data....")
        progressDialog.setCancelable(false)
        progressDialog.show()
        var updatedLyrics = ""
        var updatedTitle = ""
        if(editMusicLyrics.text.isEmpty()) editMusicLyrics.setError("Please Fill")
        if(editMusicTitle.text.isEmpty()) editMusicTitle.setError("Please Fill")
        if(editMusicTitle.text.isNotEmpty() && editMusicLyrics.text.isNotEmpty()){
            updatedTitle = editMusicTitle.text.toString()
            updatedLyrics = editMusicLyrics.text.toString()
        }
        //Inserting edited data to database
        val dbRef = FirebaseDatabase.getInstance().getReference("Music").child(musicId)
        val musicUpdateData = Song(updatedTitle, musicId, musicStorageId, musicReleaseDate, musicStorageId, musicPublisherId, updatedLyrics, musicTranslateLyrics, musicDuration, currentPublicity)
        dbRef.setValue(musicUpdateData).addOnSuccessListener {
            if(progressDialog.isShowing) progressDialog.dismiss()
            FancyToast.makeText(this, "Music has been updated", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
            finish()
        }.addOnFailureListener{
            FancyToast.makeText(this, "Error $(err.message)", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
        }
    }

    private fun prepareData() {
        //Getting data from intent
        if(intent.extras != null){
            musicTitle = intent.getStringExtra("musicTitle").toString()
            musicLyrics = intent.getStringExtra("musicLyrics").toString()
            musicDuration = intent.getStringExtra("musicDuration").toString()
            musicPublicity = intent.getBooleanExtra("musicPublicity", false)
            musicStorageId = intent.getStringExtra("musicStorageId").toString()
            musicId = intent.getStringExtra("musicId").toString()
            musicReleaseDate = intent.getStringExtra("musicReleaseDate").toString()
            musicTranslateLyrics = intent.getStringExtra("musicTranslateLyrics").toString()
            musicPublisherId = intent.getStringExtra("musicPublisherId").toString()

            //Setting data
            editMusicTitle.setText(musicTitle)
            editMusicLyrics.setText(musicLyrics)
            val link = "https://firebasestorage.googleapis.com/v0/b/mulay-8734a.appspot.com/o/images%2F${musicStorageId}?alt=media&token=7785279e-56f8-4d58-aa28-e3b51ff65ad1"
            Picasso.get().load(link).placeholder(R.color.black).into(imageMusicCover)
            currentPublicity = musicPublicity
        }
    }
}