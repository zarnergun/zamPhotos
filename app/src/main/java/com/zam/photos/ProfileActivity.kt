package com.zam.photos

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profil.*
import java.io.IOException


class ProfileActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private var filePath: Uri? = null
    private var firebaseStorage: StorageReference? = null
    private var pic: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        // get the Firebase  storage reference
        firebaseStorage = FirebaseStorage.getInstance().getReference()

        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener { finish() }

        val database = Database(this)
        val store = Storage(this)
        var email = store.readString("email")
        pic = store.readString("pic").toString()

        Log.i("appel", pic)
        profile_pseudo.text = email
        Picasso.get().load("http://91.160.165.231/images/profil/" + pic + ".jpg")
            .into(profile_image)

        button2.setOnClickListener {
            database.cleanUser()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // CALL GALLERY PICK
        profile_image.setOnClickListener {
            ImagePicker(pic);
        }
        // -------

    }

    private fun ImagePicker(pic: String) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                profile_image?.setImageBitmap(bitmap)
                uploadImage(pic)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage(pic: String) {
//        val file = Uri.fromFile(File(filePath)
        val riversRef: StorageReference = firebaseStorage!!.child("profils_images/" + pic + ".jpg")

        var uploadTask = filePath?.let { riversRef.putFile(it) }

        if (uploadTask != null) {
            uploadTask.addOnFailureListener {
                Log.e("Upload_Error", it.toString())
            }.addOnSuccessListener {
                Log.e("Upload_OK", it.toString())
            }
        }

    }

}