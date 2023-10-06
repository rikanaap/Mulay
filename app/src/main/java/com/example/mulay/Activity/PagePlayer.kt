package com.example.mulay.Activity

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.example.mulay.Adapter.FragmentLyricsAdapter
import com.example.mulay.R
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.shashank.sony.fancytoastlib.FancyToast
import com.squareup.picasso.Picasso
import kotlin.math.max


class PagePlayer : AppCompatActivity() {
    //Giving variable
    private lateinit var tabLayout : TabLayout
    private lateinit var viewPager : ViewPager
    private lateinit var motionLayout : MotionLayout
    private lateinit var imageDropdown : ImageView
    private lateinit var mediaPlayer : MediaPlayer
    private lateinit var playerMusicTitle : TextView
    private lateinit var playerMusicPublisher : TextView
    private lateinit var playerSeekBar : SeekBar
    private lateinit var playerPlayPause : ImageView
    private lateinit var playerForwardButton : ImageView
    private lateinit var playerBackwardButton : ImageView
    private lateinit var playerMuteButton : ImageView
    private lateinit var playerLoopingButton : ImageView
    private lateinit var playerCurrentDuration : TextView
    private lateinit var playerFullDuration : TextView
    private lateinit var playerMusicCover : ImageView
    private lateinit var progressBar : ProgressBar
    private lateinit var playerLayout : MotionLayout
    private lateinit var dbRef : DatabaseReference

    //For the player
    private lateinit var durationHandler : Handler
    private var musicLooping : Boolean = false
    private var soundMute : Boolean = false
    private var musicplay : Boolean = true
    private var currentUserVolume : Int = 0
    private lateinit var audioManager: AudioManager


    //this is for the data to set
    private lateinit var musicDuration : String
    private lateinit var musicTitle : String
    private lateinit var musicPublisher : String
    private lateinit var musicLyrics : String
    private lateinit var musicLinkStorage : String
    private lateinit var musicLinkCover : String

    //Animation
    val rotateDropdown : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.anim_dropdown_playlist) }
    val rotateDropUp : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.anim_dropup_playlist) }
    val fadeIn : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.anim_fade_in) }
    val fadeOut : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.anim_fade_out) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_player)
        //Setting View
        playerSeekBar = findViewById(R.id.seekBarPlayer)
        playerBackwardButton = findViewById(R.id.imagePlayerBackward)
        playerForwardButton = findViewById(R.id.imagePlayerForward)
        playerFullDuration = findViewById(R.id.textSetMusicDurationPlayer)
        playerCurrentDuration = findViewById(R.id.textSetPlayerDuration)
        playerMusicTitle = findViewById(R.id.textSetMusicTitlePlayer)
        playerMusicPublisher = findViewById(R.id.textSetMusicPublisherPlayer)
        playerMusicCover = findViewById(R.id.imageSetMusicCoverPlayer)
        tabLayout = findViewById(R.id.tabLayoutLyricsPlayer)
        viewPager = findViewById(R.id.viewLyricsPlayer)
        imageDropdown = findViewById(R.id.dropDownLyrics)
        playerMuteButton = findViewById(R.id.imagePlayerMute)
        progressBar = findViewById(R.id.playerProgressBar)
        playerLayout = this.findViewById(R.id.motionLayoutPLayer)
        playerLoopingButton = findViewById(R.id.imagePlayerLooping)
        playerPlayPause = findViewById(R.id.imagePlayerPlayPause)
        mediaPlayer = MediaPlayer()

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        //For the sound, better to didnt even bother this
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        currentUserVolume =  audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        //Database conection
        dbRef = FirebaseDatabase.getInstance().getReference("User")

        //Checking user connection
        if (!isInternetAvailable()) {
            FancyToast.makeText(this,"Bad Connection", FancyToast.LENGTH_LONG, FancyToast.ERROR,false).show()
            finish()
        } else {
            FancyToast.makeText(this,"Connection Stable", FancyToast.LENGTH_LONG, FancyToast.SUCCESS,false).show()
            playerLayout.isEnabled = true
            //getting data from intent
            if(intent.extras != null){
                musicDuration = intent.getStringExtra("musicDuration").toString()
                musicPublisher = intent.getStringExtra("musicPublisher").toString()
                musicLyrics = intent.getStringExtra("musicLyrics").toString()
                musicTitle = intent.getStringExtra("musicTitle").toString()
                val newMusicLinkStorage = "https://firebasestorage.googleapis.com/v0/b/mulay-8734a.appspot.com/o/audio%2F${intent.getStringExtra("musicStorageId")}?alt=media&token=187d9c71-7be0-4b83-83b4-a283c5bb34cd"
                musicLinkCover = "https://firebasestorage.googleapis.com/v0/b/mulay-8734a.appspot.com/o/images%2F${intent.getStringExtra("musicCoverId")}?alt=media&token=7785279e-56f8-4d58-aa28-e3b51ff65ad1"
                musicLinkStorage = newMusicLinkStorage
                sharedPreferences.edit().putString("linkPlaying", musicLinkStorage).apply()
                checkMusicChange(newMusicLinkStorage)
                setAudioInformation()
            }
        }



        //For the mute
        playerMuteButton.setOnClickListener{
            soundMuted()
        }

        //For the loop
        playerLoopingButton.setOnClickListener{
            playerLoop()
        }

        //For the pause
        playerPlayPause.setOnClickListener{
            playPause()
        }

        //For forward and Backward
        playerBackwardButton.setOnClickListener{

        }

        //Hide the tabLayout and ViewPager and shown the progress bar
        progressBar.visibility = View.VISIBLE
        tabLayout.visibility = View.GONE
        viewPager.visibility = View.GONE

        //Setting Motion Layout
        motionLayout = findViewById(R.id.motionLayoutPLayer)
        motionLayout.addTransitionListener(object : MotionLayout.TransitionListener{
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int
            ) {
                playerMuteButton.isEnabled = true
                playerLoopingButton.isEnabled = true
                playerLoopingButton.startAnimation(fadeIn)
                playerMuteButton.startAnimation(fadeIn)
                imageDropdown.startAnimation(rotateDropUp)
                //Hide the tabLayout and ViewPager (Second is for hide again after the motion layout reach start again)
                tabLayout.visibility = View.GONE
                viewPager.visibility = View.GONE
            }

            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float
            ) {}

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                if(currentId == R.id.end){
                    tabLayout.visibility = View.VISIBLE
                    viewPager.visibility = View.VISIBLE
                    playerMuteButton.isEnabled = false
                    playerLoopingButton.isEnabled = false
                    playerLoopingButton.startAnimation(fadeOut)
                    playerMuteButton.startAnimation(fadeOut)
                    imageDropdown.startAnimation(rotateDropdown)
                }
            }

            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float
            ) {}

        })

        //Setting tab layout
        tabLayout.addTab(tabLayout.newTab().setText("Original"))
        tabLayout.addTab(tabLayout.newTab().setText("Translate"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = FragmentLyricsAdapter(this, supportFragmentManager, tabLayout.tabCount)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object  : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }

    private fun playPause() {
        musicplay = !musicplay
        if(musicplay == true) {
            playerPlayPause.setImageResource(R.drawable.ic_player_pause)
            mediaPlayer.start()
        } else {
            playerPlayPause.setImageResource(R.drawable.ic_player_play)
            mediaPlayer.pause()
        }
    }

    private fun playerLoop() {
        musicLooping = !musicLooping
        if(musicLooping == false) playerLoopingButton.setColorFilter(ContextCompat.getColor(this, R.color.white)) else playerLoopingButton.setColorFilter(ContextCompat.getColor(this, R.color.green))
        mediaPlayer.isLooping = musicLooping == true
    }

    private fun soundMuted() {
        soundMute = !soundMute
        if(soundMute === false){
            playerMuteButton.setImageResource(R.drawable.ic_player_speaker)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentUserVolume, 0)
        }else{
            playerMuteButton.setImageResource(R.drawable.ic_player_mute)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
        }
    }

    private fun setAudioInformation() {
        playerFullDuration.setText(musicDuration)
        playerMusicTitle.setText(musicTitle)
        playerMusicPublisher.setText(musicPublisher)
        Picasso.get().load(musicLinkCover).placeholder(R.color.black).into(playerMusicCover)
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

    private fun initializePlayer(storage : String) {
        // Set audio attributes for the media player (optional)
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        mediaPlayer.setDataSource(musicLinkStorage)
        mediaPlayer.prepare()
        // Set a listener to update the SeekBar progress as the media plays
        mediaPlayer.setOnPreparedListener { success ->
            mediaPlayer.start()
            if(success.isPlaying == true){
                progressBar.visibility = View.GONE
                val totalDuration = max(mediaPlayer.duration, 0)
                playerSeekBar.max = totalDuration
                updateSeekBar()
            }else{
                FancyToast.makeText(this, "There's some issue", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
                finish()
            }
        }

        durationHandler = Handler(Looper.getMainLooper())
        updateSeekBar()
        playerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // No implementation needed
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // No implementation needed
            }
        })
    }

    private fun checkMusicChange(newMusicLinkStorage: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val data = sharedPreferences.getString("linkPlaying", "")

        if (musicLinkStorage != data) {
            mediaPlayer.release()
            mediaPlayer.stop()
            musicLinkStorage = newMusicLinkStorage
            initializePlayer(newMusicLinkStorage)
        }else{
            initializePlayer(musicLinkStorage)
        }
    }

    private fun updateSeekBar() {
        val currentDuration = mediaPlayer.currentPosition
        playerSeekBar.progress = currentDuration
        playerCurrentDuration.setText(formateMilliSeccond(currentDuration.toLong()))
        durationHandler.postDelayed({
            updateSeekBar()
        }, 100)
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override fun onStop() {
        super.onStop()
        durationHandler.removeCallbacksAndMessages(null)
    }

}
