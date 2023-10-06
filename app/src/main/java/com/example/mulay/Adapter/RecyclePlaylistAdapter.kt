package com.example.mulay.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mulay.Activity.PagePlaylist
import com.example.mulay.Data.Playlist
import com.example.mulay.Data.Song
import com.example.mulay.R

class RecyclePlaylistAdapter(private val playlist: ArrayList<Playlist>, private val musicInPlaylist: ArrayList<Song>, private val context: Context) : RecyclerView.Adapter<RecyclePlaylistAdapter.ViewHolder>() {
    private var expandedPosition = -1
    private var lastClickedPosition = -1
    private var clickCount = 0
    private var clickTime = 0L
    private val doubleClickThreshold = 300 // milliseconds

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_playlist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemView = playlist[position]
        val rotateDropdown: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.anim_dropup_playlist) }

        holder.playlistCover.setImageResource(itemView.playlistImage)
        holder.playlistTitle.text = itemView.playlistTitle
        holder.playlistReleaseDate.text = itemView.playlistReleaseDate

        val isExpanded = position == expandedPosition
        holder.playlistExpandable.visibility = if (isExpanded) View.VISIBLE else View.GONE
        if (isExpanded){
            holder.playlistDropdown.startAnimation(rotateDropdown)
        } else holder.playlistDropdown.rotation = 180f

        holder.playlistExpandList.setHasFixedSize(true)
        holder.playlistExpandList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val adapter = RecycleExpandPlaylistAdapter(musicInPlaylist, context)
        holder.playlistExpandList.adapter = adapter

        holder.playlistHolder.setOnClickListener {
            val currentPosition = holder.adapterPosition

            if (currentPosition == lastClickedPosition && System.currentTimeMillis() - clickTime < doubleClickThreshold) {
                // Double tap detected
                val intent = Intent(context, PagePlaylist::class.java)
                context.startActivity(intent)
                resetClickCount()
                return@setOnClickListener
            }

            if (expandedPosition >= 0) {
                val prevExpandedPosition = expandedPosition
                expandedPosition = -1
                notifyItemChanged(prevExpandedPosition)
            }

            if (!isExpanded) {
                expandedPosition = currentPosition
                notifyItemChanged(expandedPosition)
            }

            lastClickedPosition = currentPosition
            clickCount++
            clickTime = System.currentTimeMillis()
        }
    }

    private fun resetClickCount() {
        clickCount = 0
        clickTime = 0
    }

    override fun getItemCount(): Int {
        return playlist.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val playlistCover: ImageView = v.findViewById(R.id.imageSetPlaylisytCoverRecycle)
        val playlistTitle: TextView = v.findViewById(R.id.textSetPlaylisytTitleItem)
        val playlistReleaseDate: TextView = v.findViewById(R.id.textSetPlaylistReleaseItem)
        val playlistHolder: ConstraintLayout = v.findViewById(R.id.constraintPlaylistWrapper)
        val playlistExpandable: LinearLayout = v.findViewById(R.id.linearExpandablePlaylist)
        val playlistDropdown: ImageView = v.findViewById(R.id.imageDropdownPlaylist)
        val playlistExpandList: RecyclerView = v.findViewById(R.id.recycleExpandablePlaylist)
    }
}
