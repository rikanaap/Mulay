package com.example.mulay.Adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.mulay.Fragment.LyricsPage.OriginalLyricsPage
import com.example.mulay.Fragment.LyricsPage.TransaltedLyricsPage

internal class FragmentLyricsAdapter(var context: Context, fragmentManager: FragmentManager, var totalTabs: Int): FragmentPagerAdapter(fragmentManager){
    override fun getCount(): Int {
        return totalTabs
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> (OriginalLyricsPage())
            1 -> (TransaltedLyricsPage())
            else -> getItem(position)
        }
    }
}