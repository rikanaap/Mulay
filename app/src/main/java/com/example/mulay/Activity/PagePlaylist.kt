package com.example.mulay.Activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mulay.Adapter.RecycleSongAdapter
import com.example.mulay.R
import com.example.mulay.tEST.songData
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur

class PagePlaylist : AppCompatActivity() {
    private lateinit var blurView : BlurView
    private lateinit var recyclePlaylist : RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_playlist)
        recyclePlaylist = findViewById(R.id.recycleviewSetPlaylist)
        blurView = findViewById(R.id.blurView)
        setBlurView()

        val data = songData.getItemData()
        recyclePlaylist.layoutManager = GridLayoutManager(this,3, LinearLayoutManager.VERTICAL, false)
        recyclePlaylist.setHasFixedSize(true)
        val popularSongAdapter = RecycleSongAdapter(data, this)
        recyclePlaylist.adapter = popularSongAdapter
    }

    private fun setBlurView() { //THIS IS FOR THE BLURRY, BETTER NOT TO CHANGE ANYTHING IN THIS CLASS
        val radius = 2f
        val decorView : View = getWindow().getDecorView();
        val rootView : ViewGroup = decorView.findViewById(android.R.id.content);
        val windowBackground : Drawable = decorView.getBackground();

        blurView.setupWith(rootView, RenderScriptBlur(this)) // or RenderEffectBlur
            .setFrameClearDrawable(windowBackground) // Optional
            .setBlurRadius(radius)
    }
}