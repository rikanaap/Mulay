package com.example.mulay.Activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mulay.R

class PageShowList : AppCompatActivity() {
    private lateinit var textListContent : TextView
    private lateinit var buttonBack : ImageView
    private lateinit var recycleList : RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_showlist)
        //Setting View
        textListContent = findViewById(R.id.textSetListContent)
        buttonBack = findViewById(R.id.imageSetBackShowlist)
        recycleList = findViewById(R.id.recycleShowList)

        //When button back clicked
        buttonBack.setOnClickListener{
            finish()
        }
        //Setting the recycle view
        recycleList.layoutManager = GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)
        setIntentData()
    }

    private fun setIntentData() {
        if(intent.extras != null){
            textListContent.setText(intent.getStringExtra("listContent"))
        }else{}
    }
}