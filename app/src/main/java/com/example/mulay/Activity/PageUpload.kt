package com.example.mulay.Activity

import android.app.ProgressDialog
import android.bluetooth.BluetoothClass.Service.AUDIO
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.mulay.Data.Song
import com.example.mulay.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.shashank.sony.fancytoastlib.FancyToast
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.text.SimpleDateFormat
import java.util.*

class PageUpload : AppCompatActivity() {
    private lateinit var buttonBack : ImageView
    private lateinit var buttonUpload : ImageView
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var dbRef : DatabaseReference
    private var imageUri : Uri? = null
    private var audioUri : Uri? = null
    private var playpause : Boolean = false
    private lateinit var mediaPlayer : MediaPlayer

    //For picking data
    private lateinit var imageCover : ImageView
    private lateinit var buttonPickAudio : LinearLayout
    private lateinit var inputMusicTitle : EditText
    private lateinit var inputMusicGenre : Spinner
    private lateinit var inputMusicLyrics : EditText

    //Setting Audio File Information
    private lateinit var constraintForm : ConstraintLayout
    private lateinit var setAudioFileName : TextView
    private lateinit var setAudioFileDuration : TextView
    private lateinit var musicDuration : String
    private var musicMillsecond : Long = 0
    private lateinit var setSeekBarAudioFile : SeekBar
    private lateinit var setPlayPauseAudioFile : ImageView
    private lateinit var setCurrentDuration : TextView

    val fadeIn : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.anim_fade_in) }
    val fadeOut : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.anim_fade_out) }

    //Pick date, uses for Update
    private val formatter = SimpleDateFormat("dd-MM-yyyy_HH:mm:ss", Locale.getDefault())
    private val now = Date()
    private val date = formatter.format(now)

    //User when picking user profile photo [DONT CHANGE THIS]
    private lateinit var cropActivityResultLauncher : ActivityResultLauncher<Any?>
    private val cropActivityResultContext = object : ActivityResultContract<Any?, Uri?>(){
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Select Image")
                .setAspectRatio(1,1)
                .setActivityMenuIconColor(R.color.white)
                .setAllowFlipping(true)
                .setAllowRotation(true)
                .setCropMenuCropButtonTitle("Select")
                .getIntent(this@PageUpload)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_upload)
        //Setting View
        buttonBack = findViewById(R.id.imageConfirmCancel)
        buttonUpload = findViewById(R.id.imageConfirmUpload)
        imageCover = findViewById(R.id.imageSetMusicCoverUpload)
        inputMusicGenre = findViewById(R.id.spinerInputGenreUpload)
        inputMusicLyrics = findViewById(R.id.editInputLyricsUpload)
        inputMusicTitle = findViewById(R.id.editInputMusicTitleUpload)
        buttonPickAudio = findViewById(R.id.buttonPickAudioUpload)
        setAudioFileName = findViewById(R.id.textSetFileNameUpload)
        setAudioFileDuration = findViewById(R.id.textSetFileDurationUpload)
        setSeekBarAudioFile = findViewById(R.id.seekBarPlayerUpload)
        constraintForm = findViewById(R.id.constraintMediaFormFileSection)
        setPlayPauseAudioFile = findViewById(R.id.imageStopMusicUpload)
        setCurrentDuration = findViewById(R.id.textSetCurrentDurationUpload)
        mediaPlayer = MediaPlayer()

        constraintForm.visibility = View.GONE
        constraintForm.isEnabled = false
        //CHANGE PROFILE PICTURE
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContext) {
            it?.let { uri ->
                imageUri = uri
                imageCover.setImageURI(uri)
            }
        }
        //Setting database
        firebaseAuth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("Music")

        //When user pick image Cover
        imageCover.setOnClickListener{
            cropActivityResultLauncher.launch(null)
        }

        buttonPickAudio.setOnClickListener{
            val intent = Intent()
            intent.setType("audio/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select Audio"), AUDIO)
        }

        //When cancel clicked
        buttonBack.setOnClickListener{
            if(mediaPlayer.isPlaying == true){
                mediaPlayer.stop()
            }
            finish()
        }

        //When upload clicked
        buttonUpload.setOnClickListener{
            val title = inputMusicTitle.text.toString()
            val lyrics = inputMusicLyrics.text.toString()
            val coverUri = imageUri
            val audioUri = audioUri
            if(coverUri != null && audioUri != null && title.isNotEmpty() && lyrics.isNotEmpty()){
                openUploadDialog(coverUri, audioUri, title, lyrics)
            }else{
                if(coverUri == null){
                    Toast.makeText(this, "Please change your music cover", Toast.LENGTH_SHORT).show()
                }
                if(audioUri == null){
                    Toast.makeText(this, "Please pick your audio file", Toast.LENGTH_SHORT).show()
                }
                if (lyrics.isEmpty()){
                    inputMusicLyrics.setError("Please Fill")
                }
                if(title.isEmpty()){
                    inputMusicTitle.setError("Please Fill")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            if(requestCode == AUDIO){
                audioUri = data!!.data
                setAudioFile(audioUri!!)
            }
        }
    }

    private fun setAudioFile(uri : Uri) {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(applicationContext, uri)
        val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val audioFileTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        var millSecond = durationStr!!.toInt()
        musicMillsecond = millSecond.toLong()
        val duration = formateMilliSeccond(musicMillsecond)
        musicDuration = duration.toString()
        //Hide pick button & Show Form Audio
        buttonPickAudio.startAnimation(fadeOut)
        buttonPickAudio.visibility = View.GONE
        buttonPickAudio.isEnabled = false
        constraintForm.startAnimation(fadeIn)
        constraintForm.visibility = View.VISIBLE
        constraintForm.isEnabled = true

        if(inputMusicTitle.text.isEmpty()){
            inputMusicTitle.setText(audioFileTitle)
        }

        //For the form audio
        setAudioFileName.setText(audioFileTitle)
        setAudioFileDuration.setText(musicDuration)

        setMusidPlayer(uri)
    }

    private fun setMusidPlayer(mediaUri : Uri) {
        // Set audio attributes for the media player (optional)
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        mediaPlayer.setAudioAttributes(audioAttributes)
        mediaPlayer.isLooping = true

        // Set the data source of the media player
        mediaPlayer.setDataSource(applicationContext, mediaUri)

        // Prepare the media player asynchronously
        mediaPlayer.prepareAsync()

        // Set up the SeekBar
        setSeekBarAudioFile.visibility = View.VISIBLE
        setSeekBarAudioFile.max = 100
        setPlayPauseAudioFile.setOnClickListener{
            playpause  = !playpause

            if(playpause == false){
                mediaPlayer.start()
                setPlayPauseAudioFile.setImageResource(R.drawable.ic_upload_pause)
            }else{
                mediaPlayer.pause()
                setPlayPauseAudioFile.setImageResource(R.drawable.ic_search_play)
            }
        }

        // Set a listener to update the SeekBar progress as the media plays
        mediaPlayer.setOnPreparedListener {
            setSeekBarAudioFile.max = mediaPlayer.duration
            mediaPlayer.start()
            updateSeekBar()
        }

        // Set a listener for the SeekBar progress change
        setSeekBarAudioFile.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Not used in this example
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Not used in this example
            }
        })
    }

    private fun updateSeekBar() {
        // Update the SeekBar progress while the media is playing
        setSeekBarAudioFile.progress = mediaPlayer.currentPosition
        setCurrentDuration.setText(formateMilliSeccond(setSeekBarAudioFile.progress.toLong()))
        if (mediaPlayer.isPlaying == true) {
            Handler().postDelayed({
                updateSeekBar()
            }, 150)
        }
    }

    private fun formateMilliSeccond(milliseconds: Long): String? {
        var finalTimerString = ""
        var secondsString = ""

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()

        // Add hours if there
        if (hours > 0) {
            finalTimerString = "$hours:"
        }

        // Prepending 0 to seconds if it is one digit
        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }
        finalTimerString = "$finalTimerString$minutes:$secondsString"
        // return timer string
        return finalTimerString
    }

    private fun openUploadDialog(coverUri : Uri, audioFileUri : Uri, title : String, lyrics : String) {
        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.dialog_confirmation_upload, null)
        mDialog.setView(mDialogView)
        val alertDialog = mDialog.show()

        val textContent : TextView = mDialogView.findViewById(R.id.textSetMTitleMusicDialog)
        textContent.setText("Are you sure want to upload\n$title?")
        val checkboxTerms : CheckBox = mDialogView.findViewById(R.id.checkboxInputAcceptDialog)
        val buttonTerms : LinearLayout = mDialogView.findViewById(R.id.linearTermsButton)
        buttonTerms.setOnClickListener{
            openTermsDialog()
        }

        val buttonUploadMusic : LinearLayout = mDialogView.findViewById(R.id.linearUploadButton)
        buttonUploadMusic.setOnClickListener{
            if(checkboxTerms.isChecked){
                uploadMusicData(coverUri, audioFileUri, title, lyrics)
                if (alertDialog.isShowing == false){
                    finish()
                }
            }else{
                Toast.makeText(this, "Please accept our terms and condition", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun uploadMusicData(coverUri : Uri, musicUri : Uri, title : String, lyrics : String) {
        //THIS ONE IS FOR UNIQUE VARIABLE
        val musicId = dbRef.push().key!!
        val imgId = "$musicId-$date"
        val audioId = "$musicId-$date"
        val publisherId = firebaseAuth.currentUser!!.uid

        //Uploading image
        fun uploadImage(onComplete: () -> Unit) {
            val coverStorageReference = FirebaseStorage.getInstance().getReference("images/$imgId")
            if (coverUri != null) {
                coverStorageReference.putFile(coverUri!!)
                    .addOnSuccessListener {
                        imageUri = null
                        imageCover.setImageResource(R.color.black)
                        onComplete()

                    }.addOnFailureListener{
                        Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Mohon masukkan foto", Toast.LENGTH_SHORT).show()
            }


        }

        fun uploadAudio(onComplete: () -> Unit) {
            val audioStorageReference = FirebaseStorage.getInstance().getReference("audio/$audioId")
            if (musicUri != null) {
                audioStorageReference.putFile(musicUri!!)
                    .addOnSuccessListener {
                        audioUri = null
                        setAudioFileDuration.setText("")
                        setAudioFileName.setText("")
                        onComplete()

                    }.addOnFailureListener {
                        Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
            } else {
                FancyToast.makeText(this, "Mohon Sertakan File Musik", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
            }
        }

        //For upload to database
        val musicUpload = Song(title,musicId, audioId, date, imgId, publisherId, lyrics, "Coming Soon", musicDuration, false)

        if(imageUri != null && audioUri != null && title.isNotEmpty() && lyrics.isNotEmpty() && musicId.isNotEmpty()){
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Uploading data...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            dbRef.child(musicId).setValue(musicUpload).addOnCompleteListener{
                uploadImage {
                    FancyToast.makeText(this, "Your Music is on the air", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
                }
                uploadAudio {
                    FancyToast.makeText(this, "Your Music is public", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
                    finish()
                }
            }.addOnFailureListener{
                FancyToast.makeText(this, "Error $(err.message)", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
            }
        }
    }

    private fun openTermsDialog() {
        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.dialog_terms_condition, null)
        mDialog.setView(mDialogView)
        val alertDialog = mDialog.show()

        val buttonBackDialog : ImageView = mDialogView.findViewById(R.id.imageCloseDialogTerms)
        buttonBackDialog.setOnClickListener{
            alertDialog.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
    }
}