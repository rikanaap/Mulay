package com.example.mulay.Fragment.MainPage

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mulay.Activity.PageSearch
import com.example.mulay.Adapter.RecycleForYouAdapter
import com.example.mulay.Adapter.RecycleHitsGenreAdapter
import com.example.mulay.R
import com.example.mulay.tEST.genreData
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentPageSearch.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentPageSearch : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recycleForYou : RecyclerView
    private lateinit var recycleHitsGenre : RecyclerView
    private lateinit var searchButton : ConstraintLayout
    private lateinit var textHintSearch : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_page_search, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentPageSearch.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentPageSearch().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //SETTING VIEW
        recycleForYou = view.findViewById(R.id.recycleForYou)
        recycleHitsGenre = view.findViewById(R.id.recycleHitsGenre)
        searchButton = view.findViewById(R.id.topSearchSection)
        textHintSearch = view.findViewById(R.id.textSetHintSearch)
        setRecycleData()
        animateSearchCharacter()

        //WHEN USER CLICK TO SEARCH
        searchButton.setOnClickListener{
            val intent = Intent(context, PageSearch::class.java)
            startActivity(intent)
        }

    }

    private fun setRecycleData() {
        //GETTING DATA TO AN ARRAY
        val genreList = genreData.getItemData()
        val hitsGenreList = genreData.getItemData()
        hitsGenreList.shuffle()

        //RECYCLE VIEW FOR FOR YOU GENRE
        recycleForYou.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recycleForYou.setHasFixedSize(true)
        val forYouGenreAdapter = context?.let { RecycleForYouAdapter(genreList, it) }
        recycleForYou.adapter = forYouGenreAdapter

        //RECYCLE VIEW FOR HITS GENRE
        recycleHitsGenre.layoutManager = GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
        recycleHitsGenre.setHasFixedSize(true)
        val hitsGenreAdapter = context?.let { RecycleHitsGenreAdapter(hitsGenreList, it) }
        recycleHitsGenre.adapter = hitsGenreAdapter
    }

    private fun animateSearchCharacter() {
        val arrayHint = resources.getStringArray(R.array.searchText)
        val randomInt = Random.nextInt(0, arrayHint.size)
        val currentTextHint = arrayHint[randomInt]
        textHintSearch.text = ""

        for (i in currentTextHint.indices) {
            val index = i
            Handler().postDelayed({
                textHintSearch.append(currentTextHint[index].toString())
            }, i * 50L)
        }
    }

}