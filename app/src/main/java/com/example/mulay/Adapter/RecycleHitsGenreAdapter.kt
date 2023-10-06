package com.example.mulay.Adapter

import android.content.Context
import android.content.Intent
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.mulay.Activity.PageShowList
import com.example.mulay.Data.Genre
import com.example.mulay.R

class RecycleHitsGenreAdapter(private val genre : ArrayList<Genre>, private val context: Context):RecyclerView.Adapter<RecycleHitsGenreAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_search_hitsgenre, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemView = genre[position]
        holder.genreImage.setImageResource(itemView.genreImage)
        holder.genreTitle.setText(itemView.genreTitle)
        holder.genreWrapper.setOnClickListener{
            val intent = Intent(context, PageShowList::class.java)
            intent.putExtra("listContent", itemView.genreTitle)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return genre.size
    }

    class ViewHolder(v : View):RecyclerView.ViewHolder(v) {
        val genreImage : ImageView = v.findViewById(R.id.imageSetGenreImage)
        val genreTitle : TextView = v.findViewById(R.id.textSetHitsGenreTitle)
        val genreWrapper : ConstraintLayout = v.findViewById(R.id.constraintHitsGenreWrapper)
    }
}