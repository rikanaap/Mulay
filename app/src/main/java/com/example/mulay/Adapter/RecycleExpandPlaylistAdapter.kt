package com.example.mulay.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mulay.Data.Song
import com.example.mulay.R
import org.w3c.dom.Text

class RecycleExpandPlaylistAdapter(private val childPlaylist : ArrayList<Song>, private val context: Context):RecyclerView.Adapter<RecycleExpandPlaylistAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_expand_playlist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemView = childPlaylist[position]
        holder.textMusicInPlaylist.setText(itemView.musicTitle)
    }

    override fun getItemCount(): Int {
        return childPlaylist.size
    }

    class ViewHolder(v : View): RecyclerView.ViewHolder(v) {
        val textMusicInPlaylist : TextView = v.findViewById(R.id.textSetMusicInPlaylist)
    }
}