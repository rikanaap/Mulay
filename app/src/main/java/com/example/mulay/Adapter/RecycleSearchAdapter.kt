package com.example.mulay.Adapter

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mulay.Activity.PagePlayer
import com.example.mulay.Data.Song
import com.example.mulay.R
import com.squareup.picasso.Picasso

class RecycleSearchAdapter(private var music : List<Song>, private val context : Context):RecyclerView.Adapter<RecycleSearchAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_search_output, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemView = music[position]
        val link = "https://firebasestorage.googleapis.com/v0/b/mulay-8734a.appspot.com/o/images%2F${itemView.musicCoverId}?alt=media&token=7785279e-56f8-4d58-aa28-e3b51ff65ad1"
        Picasso.get().load(link).placeholder(R.color.black).into(holder.musicImage)
        holder.musicTitle.setText(itemView.musicTitle)
        holder.musicPublisher.setText(itemView.musicPublisherId)
        holder.musicDuration.setText(itemView.musicDuration)
        holder.musicPlayButton.setOnClickListener{
            val progressDialog = ProgressDialog(context)
            progressDialog.setMessage("please wait, your music is being created")
            progressDialog.setCancelable(false)
            progressDialog.setTitle(itemView.musicTitle)
            progressDialog.show()

            val intent = Intent(context, PagePlayer::class.java)
            intent.putExtra("musicDuration", itemView.musicDuration)
            intent.putExtra("musicPublisher", itemView.musicPublisherId)
            intent.putExtra("musicLyrics", itemView.musicLyrics)
            intent.putExtra("musicTitle", itemView.musicTitle)
            intent.putExtra("musicStorageId", itemView.musicStorageId)
            intent.putExtra("musicCoverId", itemView.musicCoverId)
            context.startActivity(intent)
            Handler().postDelayed({
                progressDialog.dismiss()
            }, 3000)
        }
    }

    override fun getItemCount(): Int {
        return music.size
    }

    fun setFilteredList(search: List<Song>){
        this.music = search
        notifyDataSetChanged()
    }


    class ViewHolder(v : View):RecyclerView.ViewHolder(v) {
        val musicImage : ImageView = v.findViewById(R.id.imageSetMusicCoverSearch)
        val musicTitle : TextView = v.findViewById(R.id.textSetMusicTitleSearch)
        val musicPublisher : TextView = v.findViewById(R.id.textSetMusicPublisherSearch)
        val musicDuration : TextView = v.findViewById(R.id.textSetMusicDurationSearch)
        val musicPlayButton : ImageView = v.findViewById(R.id.imagePlaySearch)
    }
}