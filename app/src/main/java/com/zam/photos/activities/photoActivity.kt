package com.zam.photos.activities

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.zam.photos.R
import com.zam.photos.toolbar
import com.zam.photos.tools.InternalStorageProvider
import kotlinx.android.synthetic.main.activity_photo.*

class photoActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        val extras = intent.extras
        var photo:String
        photo = if (extras == null) {
            null.toString()
        } else {
            extras.getString("photo").toString()
        }

        // TOOLBAR -----------------------------------
        var myToolbar = toolbar().getToolbar(this)
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = extras?.getString("titre")
        myToolbar.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        myToolbar.supportActionBar?.setDisplayShowHomeEnabled(true)
        myToolbar.supportActionBar?.setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener { finish() }
        // ---------------------------------------------


        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        val imageCardView = findViewById<ImageView>(R.id.photoFullScreen)

       var image: Bitmap? = InternalStorageProvider(this).loadBitmap(photo)
        imageCardView.setImageBitmap(image)

        getCommentaires(photo)

    }

    fun getCommentaires(photo:String) {
        Log.d("comment", "Test : $photo")
        db.collection("commentaires").whereEqualTo("liaison", photo).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("comment", "${document.id} => ${document.data}")
                    val comment = TextView(this)
                    val textComment = " ${document.data["user"]} : \n  \t \t \t  ${document.data["comment"]}"
                    comment.setText(textComment)
                    comments.addView(comment)
                }

            }
    }

}
