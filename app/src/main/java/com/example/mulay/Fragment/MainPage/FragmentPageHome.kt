package com.example.mulay.Fragment.MainPage

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mulay.Activity.PageShowList
import com.example.mulay.Adapter.RecyclePlaylistAdapter
import com.example.mulay.Adapter.RecycleSongAdapter
import com.example.mulay.Data.Playlist
import com.example.mulay.Data.Song
import com.example.mulay.R
import com.example.mulay.tEST.playlistData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentPageHomes.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentPageHome : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var userRef : DatabaseReference
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var recyclePopularSong : RecyclerView
    private lateinit var recycleLatestSong : RecyclerView
    private lateinit var recycleUList : RecyclerView
    private lateinit var textUsername : TextView
    private lateinit var showAllPopular : TextView
    private lateinit var showAllLatest : TextView
    private lateinit var showAllUlist : TextView
    private lateinit var progressBar : ProgressBar
    private lateinit var scrollView : ScrollView
    private lateinit var popularSongData : ArrayList<Song>
    private lateinit var latestSongData : ArrayList<Song>
    private lateinit var ulistData : ArrayList<Playlist>

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
        return inflater.inflate(R.layout.fragment_page_home, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentPageHomes.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentPageHome().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Setting View
        recyclePopularSong = view.findViewById(R.id.recyclePopularHome)
        recycleLatestSong = view.findViewById(R.id.recycleLatestHome)
        recycleUList = view.findViewById(R.id.recycleUListHome)
        textUsername = view.findViewById(R.id.textSetUserHi)
        showAllPopular = view.findViewById(R.id.textShowAllPopularHome)
        showAllLatest = view.findViewById(R.id.textShowAllLatestHome)
        showAllUlist = view.findViewById(R.id.textShowAllUListHome)
        progressBar = view.findViewById(R.id.homeProgressBar)
        scrollView = view.findViewById(R.id.scrollHomePage)

        //Data to store
        popularSongData = arrayListOf(Song())
        latestSongData = arrayListOf(Song())
        ulistData = playlistData.getItemData()

        //Setting connection and username
        firebaseAuth = FirebaseAuth.getInstance()

        //Preparing All Data
        prepareData()

        //When All Clicked
        showAllPopular.setOnClickListener{
            val intent = Intent(context, PageShowList::class.java)
            intent.putExtra("listContent", "Popular")
            startActivity(intent)
        }
        showAllLatest.setOnClickListener{
            val intent = Intent(context, PageShowList::class.java)
            intent.putExtra("listContent", "Latest")
            startActivity(intent)
        }
        showAllUlist.setOnClickListener{
            val intent = Intent(context, PageShowList::class.java)
            intent.putExtra("listContent", "U-List")
            startActivity(intent)
        }
    }

    private fun prepareData() {
        progressBar.visibility = View.VISIBLE
        scrollView.visibility = View.GONE
        scrollView.isEnabled = false

        val uid = firebaseAuth.currentUser!!.uid
        userRef = FirebaseDatabase.getInstance().getReference("User").child(uid)
        userRef.child("userUsername").get().addOnSuccessListener {
            //Setting User Username
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val text = Html.fromHtml("Hi, <b>${it.value.toString()}</b>", Html.FROM_HTML_MODE_COMPACT)
                textUsername.setText(text)
            }
        }

        val popularQuery : Query = FirebaseDatabase.getInstance().getReference("Music").orderByChild("musicPublicity").equalTo(true)
        val latestQuery : Query = FirebaseDatabase.getInstance().getReference("Music").orderByChild("musicPublicity").equalTo(true)
        popularQuery.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                popularSongData.clear()
                if (snapshot.exists()) {
                    for (musicSnap in snapshot.children) {
                        val musicData = musicSnap.getValue(Song::class.java)
                        popularSongData.add(musicData!!)
                    }
                    popularSongData.shuffle()
                    //RECYCLE VIEW FOR POPULAR
                    recyclePopularSong.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    recyclePopularSong.setHasFixedSize(true)
                    val popularSongAdapter = context?.let { RecycleSongAdapter(popularSongData, it)}
                    recyclePopularSong.adapter = popularSongAdapter

                    //RECYCLE VIEW FOR LATEST
                    latestQuery.addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            latestSongData.clear()
                            if(snapshot.exists()){
                                for (musicSnap in snapshot.children) {
                                    val musicData = musicSnap.getValue(Song::class.java)
                                    latestSongData.add(musicData!!)
                                }
                                latestSongData.sortByDescending { it.musicReleaseDate }

                                recycleLatestSong.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                                recycleLatestSong.setHasFixedSize(true)
                                val latestSongAdapter = context?.let { RecycleSongAdapter(latestSongData, it)}
                                recycleLatestSong.adapter = latestSongAdapter
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                    progressBar.visibility = View.GONE
                    scrollView.visibility = View.VISIBLE
                    scrollView.isEnabled = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        //RECYCLE VIEW FOR UList
        recycleUList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycleUList.setHasFixedSize(true)
        val UListPlaylistAdapter = context?.let { RecyclePlaylistAdapter(ulistData, popularSongData, it)}
        recycleUList.adapter = UListPlaylistAdapter
    }
}