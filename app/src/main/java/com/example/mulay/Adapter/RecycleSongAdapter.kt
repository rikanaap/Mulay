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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.mulay.Activity.PagePlayer
import com.example.mulay.Data.Song
import com.example.mulay.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso


class RecycleSongAdapter(private val song: ArrayList<Song>, private val context: Context):RecyclerView.Adapter<RecycleSongAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_musicpage, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemView = song[position]
        var publisherUsername = ""
        val dbRef = itemView.musicPublisherId?.let { FirebaseDatabase.getInstance().getReference("User").child(it) }
        dbRef!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                publisherUsername = snapshot.child("userUsername").getValue().toString()
                holder.textPublisher.setText("@$publisherUsername")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        val link = "https://firebasestorage.googleapis.com/v0/b/mulay-8734a.appspot.com/o/images%2F${itemView.musicCoverId}?alt=media&token=7785279e-56f8-4d58-aa28-e3b51ff65ad1"
        Picasso.get().load(link).placeholder(R.color.black).into(holder.imageCover)
        holder.textTitle.setText(itemView.musicTitle)
        holder.textDuration.setText(itemView.musicDuration)
        holder.musicWrapper.setOnClickListener{
            val progressDialog = ProgressDialog(context)
            progressDialog.setMessage("Please wait.....")
            progressDialog.setInverseBackgroundForced(true)
            progressDialog.setCancelable(false)
            progressDialog.setTitle(itemView.musicTitle)
            progressDialog.show()

            val intent = Intent(context, PagePlayer::class.java)
            intent.putExtra("musicDuration", itemView.musicDuration)
            intent.putExtra("musicPublisher", publisherUsername)
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
        return song.size
    }

    class ViewHolder(val v : View) : RecyclerView.ViewHolder(v) {
        val imageCover : ImageView = v.findViewById(R.id.imageSetMusicCover)
        val textTitle : TextView = v.findViewById(R.id.textSetMusicTitle)
        val textPublisher : TextView = v.findViewById(R.id.textSetMusicPublisher)
        val textDuration : TextView = v.findViewById(R.id.textSetMusicDuration)
        val musicWrapper : ConstraintLayout = v.findViewById(R.id.constraintMusicWrapper)
    }

}