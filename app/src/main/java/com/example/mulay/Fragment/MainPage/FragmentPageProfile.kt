package com.example.mulay.Fragment.MainPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.example.mulay.Adapter.FragmentProfileAdapter
import com.example.mulay.R
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.shashank.sony.fancytoastlib.FancyToast
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentPageProfile.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentPageProfile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var tabLayout : TabLayout
    private lateinit var viewPager : ViewPager
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var userRef : DatabaseReference
    private lateinit var progress : ProgressBar
    private lateinit var musicRef : DatabaseReference
    private lateinit var constraintProfile : ConstraintLayout

    //Data to set
    private lateinit var textUsername : TextView
    private lateinit var imageUserProfile : ImageView
    private lateinit var textPronouns : TextView
    private lateinit var textMusicCount : TextView
    private lateinit var textPlaylistCount : TextView

    //Data to store
    private lateinit var userUsername : String
    private lateinit var userProfileId : String
    private lateinit var userPronouns : String
    private lateinit var userId : String

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
        return inflater.inflate(R.layout.fragment_page_profile, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentPageProfile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentPageProfile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Setting View
        tabLayout = view.findViewById(R.id.tabLayoutProfile)
        viewPager = view.findViewById(R.id.viewPageProfile)
        textUsername = view.findViewById(R.id.textSetUserUsername)
        textPronouns = view.findViewById(R.id.textSetUserPronouns)
        imageUserProfile = view.findViewById(R.id.setImageUserPofile)
        progress = view.findViewById(R.id.profileProgressBar)
        constraintProfile = view.findViewById(R.id.constrainProfileWrapper)
        textMusicCount = view.findViewById(R.id.textSetUserMusicCounter)
        textPlaylistCount = view.findViewById(R.id.textSetUserPlaylistCounter)

        //Hiding
        constraintProfile.visibility == View.GONE
        constraintProfile.isEnabled = false
        progress.visibility = View.VISIBLE

        //Setting connection auth
        firebaseAuth = FirebaseAuth.getInstance()
        userId = firebaseAuth.currentUser!!.uid
        getAndSetUserData()

        //Setting for table layout
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        tabLayout.addTab(tabLayout.newTab().setText("Music"))
        tabLayout.addTab(tabLayout.newTab().setText("Playlist"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = context?.let { FragmentProfileAdapter(it, fragmentManager, tabLayout.tabCount) }
        viewPager.adapter = adapter

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object  : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }

    private fun getAndSetUserData() {
        userRef = FirebaseDatabase.getInstance().getReference("User").child(userId)
        musicRef = FirebaseDatabase.getInstance().getReference("Music")
        val query = musicRef.orderByChild("MusicPublisherId").equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userMusic = snapshot.childrenCount
                textMusicCount.text = userMusic.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userUsername = snapshot.child("userUsername").getValue().toString()
                userPronouns = snapshot.child("userPronouns").getValue().toString()
                userProfileId = snapshot.child("userProfileId").getValue().toString()
                if(userUsername.isNotEmpty()) textUsername.setText(userUsername)
                if(userPronouns == "") textPronouns.setText("Unspecified") else textPronouns.setText(userPronouns)
                if(userProfileId != ""){
                    val link = "https://firebasestorage.googleapis.com/v0/b/mulay-8734a.appspot.com/o/avatars%2F${userProfileId}?alt=media&token=7785279e-56f8-4d58-aa28-e3b51ff65ad1"
                    Picasso.get().load(link).placeholder(R.drawable.image_profile).into(imageUserProfile)
                }else{
                    imageUserProfile.setImageResource(R.drawable.image_profile)
                }
                progress.visibility = View.GONE
                constraintProfile.visibility == View.VISIBLE
                constraintProfile.isEnabled = true
            }

            override fun onCancelled(error: DatabaseError) {
                context?.let { FancyToast.makeText(it, "An Error Occured", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show() }
            }
        })
    }
}