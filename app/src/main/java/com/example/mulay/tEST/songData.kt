package com.example.mulay.tEST

import com.example.mulay.Data.Song

object songData {
    fun getItemData() : ArrayList<Song>{
        val listSong = ArrayList<Song>()
        val item1 = Song("Ambatukam", "Ambatukam","2003-12-32", "Rikanaapsdafdasfmnasjf hsbahfnbfhbhafjbjasbfjbajwbf","Lyrics", "Lyrics Transalate","16.43", "false", "00.00",  false)
        val item2 = Song("Ambatukam", "Ambatukam","2003-12-32", "Rikanaapsdafdasfmnasjf hsbahfnbfhbhafjbjasbfjbajwbf","Lyrics", "Lyrics Transalate","16.43", "false", "00.00", false)
        val item3 = Song("Ambatukam", "Ambatukam","2003-12-32", "Rikanaapsdafdasfmnasjf hsbahfnbfhbhafjbjasbfjbajwbf","Lyrics", "Lyrics Transalate","16.43", "false", "00.00", false)
        val item4 = Song("Ambatukam", "Ambatukam","2003-12-32", "Rikanaapsdafdasfmnasjf hsbahfnbfhbhafjbjasbfjbajwbf","Lyrics", "Lyrics Transalate","16.43", "false", "00.00", false)
        listSong.add(item1)
        listSong.add(item2)
        listSong.add(item3)
        listSong.add(item4)
        return listSong
    }

}