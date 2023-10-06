package com.example.mulay.Fragment.MainPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mulay.Adapter.RecycleAddMusicAdapter
import com.example.mulay.Data.Song
import com.example.mulay.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentPageAdd.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentPageAdd : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var userSongData = ArrayList<Song>()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var recycleAdd : RecyclerView

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
        return inflater.inflate(R.layout.fragment_page_add, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentPageAdd.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentPageAdd().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Setting view
        recycleAdd = view.findViewById(R.id.recycleAddMusicUser)

        //Setting connection
        getUserSongData()

        //For Recycle
        recycleAdd.setHasFixedSize(true)
        recycleAdd.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


    }

    private fun getUserSongData() {
        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser!!.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("Music").orderByChild("musicPublisherId").equalTo(uid)
        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userSongData.clear()
                if(snapshot.exists()){
                    for(musicSnap in snapshot.children){
                        val musicData = musicSnap.getValue(Song::class.java)
                        userSongData.add(musicData!!)
                    }
                    val userMusicAdapter = context?.let { RecycleAddMusicAdapter(userSongData, it) }
                    recycleAdd.adapter = userMusicAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}