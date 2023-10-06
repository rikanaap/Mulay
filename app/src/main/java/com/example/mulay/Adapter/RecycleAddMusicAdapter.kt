package com.example.mulay.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.mulay.Activity.PageEditMusic
import com.example.mulay.Data.Song
import com.example.mulay.R
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shashank.sony.fancytoastlib.FancyToast
import com.squareup.picasso.Picasso

class RecycleAddMusicAdapter(private val userMusic : ArrayList<Song>, private val context : Context):RecyclerView.Adapter<RecycleAddMusicAdapter.ViewHolder>(){
    private var clickCount = 0
    private var clickTime = 0L
    private val doubleClickThreshold = 300 // milliseconds
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_add_music, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemView = userMusic[position]
        val firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser!!.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("Music").child(uid)
        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        val link = "https://firebasestorage.googleapis.com/v0/b/mulay-8734a.appspot.com/o/images%2F${itemView.musicCoverId}?alt=media&token=7785279e-56f8-4d58-aa28-e3b51ff65ad1"
        Picasso.get().load(link).placeholder(R.color.black).into(holder.imageCover)
        holder.musicTitle.setText(itemView.musicTitle)
        holder.musicReleaseDate.setText(itemView.musicReleaseDate)
        holder.musicPublicity.isEnabled = false
        if(itemView.musicPublicity == true){
            holder.musicPublicity.isChecked = true
        }else{
            holder.musicPublicity.isChecked = false
        }
        if(uid == itemView.musicPublisherId){
            holder.addWrapper.setOnClickListener{
                if(System.currentTimeMillis() - clickTime < doubleClickThreshold){
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("You will delete this music")
                    builder.setMessage("Are you sure, want to remove\n${itemView.musicTitle}?")
                    builder.setCancelable(true)
                    builder.setPositiveButton("Yes"){ _, _, ->
                        val dbRef = FirebaseDatabase.getInstance().getReference("Music").child(itemView.musicId.toString())
                        val mTask = dbRef.removeValue()
                        mTask.addOnSuccessListener {
                            FancyToast.makeText(context, "${itemView.musicTitle} has been deleted", FancyToast.LENGTH_LONG, FancyToast.WARNING, false).show()
                        }.addOnFailureListener{
                                error -> FancyToast.makeText(context, error.message, FancyToast.LENGTH_LONG, FancyToast.WARNING, false).show()
                        }
                    }
                    builder.setNegativeButton("No"){
                            dialog,_, -> dialog.dismiss()
                    }
                    val dialog = builder.create()
                    dialog.show()
                }else{
                    val intent = Intent(context, PageEditMusic::class.java)
                    intent.putExtra("musicTitle", itemView.musicTitle)
                    intent.putExtra("musicLyrics", itemView.musicLyrics)
                    intent.putExtra("musicStorageId", itemView.musicStorageId)
                    intent.putExtra("musicPublicity", itemView.musicPublicity)
                    intent.putExtra("musicId", itemView.musicId)
                    intent.putExtra("musicReleaseDate", itemView.musicReleaseDate)
                    intent.putExtra("musicTranslateLyrics", itemView.musicTranslatedLyrics)
                    intent.putExtra("musicDuration", itemView.musicDuration)
                    intent.putExtra("musicPublisherId", itemView.musicPublisherId)
                    context.startActivity(intent)
                }
                clickCount++
                clickTime = System.currentTimeMillis()
            }
        }else{
            FancyToast.makeText(context, "Not The Owner of this music", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show()
        }

    }

    private fun resetClickCount() {
        clickCount = 0
        clickTime = 0
    }

    override fun getItemCount(): Int {
        return userMusic.size
    }

    class ViewHolder(v : View):RecyclerView.ViewHolder(v) {
        val imageCover : ImageView = v.findViewById(R.id.imageSetMusicCoverAdd)
        val musicTitle : TextView = v.findViewById(R.id.textSetMusicTitleAdd)
        val musicReleaseDate : TextView = v.findViewById(R.id.textSetMusicReleaseDateAdd)
        val musicPublicity : MaterialSwitch = v.findViewById(R.id.switchPublicityAdd)
        val addWrapper : ConstraintLayout = v.findViewById(R.id.constrainAddWrapper)
    }
}