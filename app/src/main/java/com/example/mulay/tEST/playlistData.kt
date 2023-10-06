package com.example.mulay.tEST

import com.example.mulay.Data.Playlist
import com.example.mulay.R

object playlistData {
    fun getItemData() : ArrayList<Playlist>{
        val listPlaylist = ArrayList<Playlist>()
        val item1 = Playlist(R.drawable.image_playlist, "BlackPink - Niger Niger", "Rikanaap", "2022-03-31")
        val item2 = Playlist(R.drawable.image_playlist, "BlackPink - Niger Niger", "Rikanaap", "2022-03-31")
        val item3 = Playlist(R.drawable.image_playlist, "BlackPink - Niger Niger", "Rikanaap", "2022-03-31")
        val item4 = Playlist(R.drawable.image_playlist, "BlackPink - Niger Niger", "Rikanaap", "2022-03-31")
        val item5 = Playlist(R.drawable.image_playlist, "BlackPink - Niger Niger", "Rikanaap", "2022-03-31")
        listPlaylist.add(item1)
        listPlaylist.add(item2)
        listPlaylist.add(item3)
        listPlaylist.add(item4)
        listPlaylist.add(item5)
        return listPlaylist
    }
}