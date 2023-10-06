package com.example.mulay.Activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mulay.Adapter.RecycleSearchAdapter
import com.example.mulay.Data.Song
import com.example.mulay.R
import com.google.firebase.database.*

class PageSearch : AppCompatActivity() {
    private lateinit var recycleSearchOuput : RecyclerView
    private lateinit var editText : EditText
    private lateinit var motionLayout : MotionLayout
    private lateinit var filteredMusicList : MutableList<Song>
    private var originalMusicList = ArrayList<Song>()
    private lateinit var dbRef : DatabaseReference
    val fadeIn : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.anim_fade_in) }
    val fadeOut : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.anim_fade_out) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_search)
        //Setting View
        recycleSearchOuput = findViewById(R.id.recycleSearchOutput)
        editText = findViewById(R.id.editInputSearch)
        motionLayout = findViewById(R.id.motionLayoutSearch)
        //SETTING FOCUS IN EDIT
        editText.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)

        //Search Function Here
        onTextChange()
        dbRef = FirebaseDatabase.getInstance().getReference("Music")
        val query = dbRef.orderByChild("musicPublicity").equalTo(true)
        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                originalMusicList.clear()
                if (snapshot.exists()) {
                    for (musicSnap in snapshot.children) {
                        val musicData = musicSnap.getValue(Song::class.java)
                        originalMusicList.add(musicData!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        filteredMusicList = originalMusicList.toMutableList()
        recycleSearchOuput.layoutManager = LinearLayoutManager(this)
        recycleSearchOuput.setHasFixedSize(true)
        
    }

    private fun onTextChange() {
        editText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                recycleSearchOuput.visibility = View.GONE
                recycleSearchOuput.startAnimation(fadeOut)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                recycleSearchOuput.visibility = View.VISIBLE
                recycleSearchOuput.startAnimation(fadeIn)
            }

            override fun afterTextChanged(s: Editable?) {
                titleSearch(s.toString().lowercase())
            }

        })
    }

    private fun titleSearch(search : String) {
        filteredMusicList.clear()
        if (search.isBlank()) {
            filteredMusicList.clear()
        } else {
            for (item in originalMusicList) {
                if (item.musicTitle!!.lowercase().contains(search)) {
                    filteredMusicList.add(item)
                }
            }
        }

        val adapter = RecycleSearchAdapter(filteredMusicList, this)
        recycleSearchOuput.adapter = adapter
    }
}