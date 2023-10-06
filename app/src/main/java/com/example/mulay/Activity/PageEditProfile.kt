package com.example.mulay.Activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mulay.Data.Users
import com.example.mulay.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.shashank.sony.fancytoastlib.FancyToast
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*

class PageEditProfile : AppCompatActivity() {
    private lateinit var buttonSave : Button
    private lateinit var buttonChangePassword : Button
    private lateinit var buttonLogOut : Button
    private lateinit var editInputUsername : EditText
    private lateinit var buttonBack : ImageView
    private lateinit var userRef : DatabaseReference
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var setImageProfile : CircleImageView
    private lateinit var spinnerPronouns : Spinner

    //Data to set
    private lateinit var userUsername : String
    private lateinit var userUid : String
    private var userUri : Uri? = null
    private lateinit var userProfileAvatar : String
    private lateinit var userEmail : String
    private lateinit var userPassword : String

    //Pick date, uses for Update
    private val formatter = SimpleDateFormat("dd-MM-yyyy_HH:mm:ss", Locale.getDefault())
    private val now = Date()
    private val date = formatter.format(now)


    //For picking image launcher
    private lateinit var cropActivityResultLauncher : ActivityResultLauncher<Any?>
    private val cropActivityResultContext = object : ActivityResultContract<Any?, Uri?>(){
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Select Image")
                .setAspectRatio(1,1)
                .setActivityMenuIconColor(R.color.white)
                .setAllowFlipping(true)
                .setAllowRotation(true)
                .setCropMenuCropButtonTitle("Select")
                .getIntent(this@PageEditProfile)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_edit_profile)
        //Setting view
        buttonSave = findViewById(R.id.buttonSaveEditProfile)
        buttonChangePassword = findViewById(R.id.buttonChangePasswordEdit)
        buttonLogOut = findViewById(R.id.buttonLogOutEdit)
        buttonBack = findViewById(R.id.imageBackProfileEdit)
        editInputUsername = findViewById(R.id.textInputUsernameProfileEdit)
        setImageProfile = findViewById(R.id.setImageUserProfileEdit)
        spinnerPronouns = findViewById(R.id.spinerInputPronounsUpload)

        //For setting the uri
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContext) {
            it?.let { uri ->
                userUri = uri
                setImageProfile.setImageURI(uri)
            }
        }

        //Setting connection
        firebaseAuth = FirebaseAuth.getInstance()
        userRef = FirebaseDatabase.getInstance().getReference("User")
        userUid = firebaseAuth.currentUser!!.uid
        val query = userRef.child(userUid)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userUsername = snapshot.child("userUsername").getValue().toString()
                userProfileAvatar = snapshot.child("userProfileId").getValue().toString()
                userEmail = snapshot.child("userEmail").getValue().toString()
                userPassword = snapshot.child("userPassword").getValue().toString()
                checkingData()
            }

            override fun onCancelled(error: DatabaseError) {
                FancyToast.makeText(this@PageEditProfile, error.message, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
            }

        })

        setImageProfile.setOnClickListener{
            cropActivityResultLauncher.launch(null)
        }

        //JUST BACk
        buttonBack.setOnClickListener{
            finish()
        }

        //When user click Save
        buttonSave.setOnClickListener{
            //When user update profile
            val username = editInputUsername.text.toString()
            val pronouns = spinnerPronouns.selectedItem.toString()
            val uid = userUid
            val profileUri  = userUri

            //Checking null
            if(username == null) editInputUsername.setError("Please Fill")

            //When user only change the imageProfile
            if(userProfileAvatar != "" && profileUri != null && userUsername == username){
                val currentProfile = userProfileAvatar
                updateProfilePhoto(currentProfile, profileUri)
            }
            if(userUsername != username) {
                if(profileUri == null) FancyToast.makeText(this, "Please change your profile", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show() else saveUpdate(username, pronouns, uid, profileUri!!)
            } else finish()
        }
        //When user click Change Password
        buttonChangePassword.setOnClickListener{
            openDialog()
        }
        //When user click Logout
        buttonLogOut.setOnClickListener{
            openLogoutDialog()
        }
    }

    private fun updateProfilePhoto(currentProfile : String, newUri : Uri) {

    }

    private fun checkingData() {
        if(userUsername != null) editInputUsername.setText(userUsername)
        if(userProfileAvatar != ""){
            val link = "https://firebasestorage.googleapis.com/v0/b/mulay-8734a.appspot.com/o/avatars%2F${userProfileAvatar}?alt=media&token=7785279e-56f8-4d58-aa28-e3b51ff65ad1"
            Picasso.get().load(link).placeholder(R.drawable.image_profile).into(setImageProfile)
        } else{
            setImageProfile.setImageResource(R.drawable.image_profile)
        }
    }

    private fun saveUpdate(username : String, pronouns : String, uid : String, uri : Uri) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading data...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        //THIS ONE IS FOR UNIQUE VARIABLE
        val imgId = "$userUid-$date"

        //Uploading image
        fun uploadImage(onComplete: () -> Unit) {
            val coverStorageReference = FirebaseStorage.getInstance().getReference("avatars/$imgId")
            if (uri != null) {
                coverStorageReference.putFile(uri!!)
                    .addOnSuccessListener {
                        userUri = null
                        setImageProfile.setImageResource(R.color.black)
                        onComplete()

                    }.addOnFailureListener {
                        Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Mohon masukkan foto", Toast.LENGTH_SHORT).show()
            }
        }

        val updateData = Users(uid, username, userEmail, userPassword, pronouns, imgId)
        if (userUri != null && uid.isNotEmpty() && username.isNotEmpty() && userEmail.isNotEmpty() && userPassword.isNotEmpty()) {
            userRef.child(uid).setValue(updateData).addOnCompleteListener {
                uploadImage {
                    FancyToast.makeText(this, "Profile photo updated", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
                }
                FancyToast.makeText(this, "Profile Updated", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
                finishAffinity()
                val intent = Intent(this, PageMain::class.java)
                startActivity(intent)
            }
        }
    }

    private fun openLogoutDialog() {
        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.dialog_confirmation_logout, null)
        mDialog.setView(mDialogView)
        val alertDialog = mDialog.show()
        val imageProfile : CircleImageView = mDialogView.findViewById(R.id.imageSetProfilePhotoLogout)
        val link = "https://firebasestorage.googleapis.com/v0/b/mulay-8734a.appspot.com/o/avatars%2F${userProfileAvatar}?alt=media&token=7785279e-56f8-4d58-aa28-e3b51ff65ad1"
        Picasso.get().load(link).placeholder(R.drawable.image_profile).into(imageProfile)
        val textLogout : TextView = mDialogView.findViewById(R.id.textSetDialogLogout)
        textLogout.setText("Are you sure want to logout from \n $userUsername?")
        val cancelDialog : ImageView= mDialogView.findViewById(R.id.imageCancelLogout)
        cancelDialog.setOnClickListener{
            alertDialog.dismiss()
        }

        val confirmDialog : ImageView = mDialogView.findViewById(R.id.imageConfirmLogOut)
        confirmDialog.setOnClickListener{
            //When user logout
            alertDialog.dismiss()
            firebaseAuth.signOut()
            finishAffinity()
            val intent = Intent(this, PageDecider::class.java)
            startActivity(intent)
        }
    }

    private fun openDialog() {
        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.dialog_edit_password, null)
        mDialog.setView(mDialogView)
        val alertDialog = mDialog.show()
        val textNewPassword : TextInputEditText = mDialogView.findViewById(R.id.editInputNewPassword)
        val confimationPassword : TextInputEditText = mDialogView.findViewById(R.id.editInputConfirmPassword)
        val currentPassword : TextView = mDialogView.findViewById(R.id.textSetCurrentPassword)
        currentPassword.setText("$userPassword")
        val cancelDialog : ImageView= mDialogView.findViewById(R.id.imageCancelPasswordChange)
        cancelDialog.setOnClickListener{
            alertDialog.dismiss()
        }

        val confirmDialog : ImageView = mDialogView.findViewById(R.id.imageAgreePasswordChange)
        confirmDialog.setOnClickListener{
            val newPassword = textNewPassword.text.toString()
            val confirmPassword = confimationPassword.text.toString()
            if(newPassword.isEmpty()) FancyToast.makeText(this, "Empty Form is Not Allowed", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show()
            if(newPassword.isEmpty()) FancyToast.makeText(this, "Empty Form is Not Allowed", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show()

            if(newPassword == confirmPassword){
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Changing Password...")
                progressDialog.setCancelable(false)
                progressDialog.show()
                val updatePassword = FirebaseAuth.getInstance().currentUser!!
                val credential = EmailAuthProvider.getCredential(userEmail, userPassword)
                updatePassword.reauthenticate(credential).addOnCompleteListener{ reauthTask ->
                    if(reauthTask.isSuccessful){
                        updatePassword.updatePassword(newPassword).addOnCompleteListener{ passwordUpdateTask ->
                            if(passwordUpdateTask.isSuccessful){
                                progressDialog.dismiss()
                                FancyToast.makeText(this, "Password Updated", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
                                finishAffinity()
                                val intent = Intent(this, PageDecider::class.java)
                                startActivity(intent)
                            }else{
                                FancyToast.makeText(this, passwordUpdateTask.exception.toString(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show()
                            }
                        }
                    }else{
                        FancyToast.makeText(this, reauthTask.exception.toString(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show()
                    }
                }
            }else{
                FancyToast.makeText(this, "Password didn't match", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show()
            }
        }
    }
}
