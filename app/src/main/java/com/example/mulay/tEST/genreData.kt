package com.example.mulay.tEST

import com.example.mulay.Data.Genre
import com.example.mulay.R

object genreData {
    fun getItemData() : ArrayList<Genre>{
        val listPlaylist = ArrayList<Genre>()
        val item1 = Genre("1",R.drawable.image_cover, "Pop")
        val item2 = Genre("2",R.drawable.image_playlist, "Classical")
        val item3 = Genre("3",R.drawable.image_cover, "K - Pop")
        val item4 = Genre("4",R.drawable.image_playlist, "Japanese")
        val item5 = Genre("5",R.drawable.image_cover, "Indonesian")
        val item6 = Genre("6",R.drawable.image_playlist, "Dangdut")
        val item7 = Genre("7",R.drawable.image_cover, "Rock")
        val item8 = Genre("8",R.drawable.image_playlist, "J - Pop")
        val item9 = Genre("9",R.drawable.image_cover, "Ambatukam")
        val item10 = Genre("10",R.drawable.image_playlist, "Reganol")
        listPlaylist.add(item1)
        listPlaylist.add(item2)
        listPlaylist.add(item3)
        listPlaylist.add(item4)
        listPlaylist.add(item5)
        listPlaylist.add(item6)
        listPlaylist.add(item7)
        listPlaylist.add(item8)
        listPlaylist.add(item9)
        listPlaylist.add(item10)
        return listPlaylist
    }
}