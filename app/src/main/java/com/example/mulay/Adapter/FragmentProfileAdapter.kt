package com.example.mulay.Adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.mulay.Fragment.ProfilePage.ProfileMusicPage
import com.example.mulay.Fragment.ProfilePage.ProfilePlaylistPage

class FragmentProfileAdapter(var context: Context, fragmentManager: FragmentManager, var totalTabs: Int): FragmentPagerAdapter(fragmentManager){
    override fun getCount(): Int {
        return totalTabs
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> (ProfileMusicPage())
            1 -> (ProfilePlaylistPage())
            else -> getItem(position)
        }
    }
}