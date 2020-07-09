package com.zam.photos.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.zam.photos.R
import com.zam.photos.tools.InternalStorageProvider
import com.zam.photos.tools.OnSwipeTouchListener
import kotlinx.android.synthetic.main.activity_photo_manager.*


class photoManagerActivity : AppCompatActivity() {

    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null
    var db_delete_status = false
    private val db = Firebase.firestore

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_manager)

        Log.i("data", "Enter in photoManagerActivity")

        // TOOLBAR -----------------------------------
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false);
        // ---------------------------------------------

        val extras = intent.extras
        var position: Int = extras?.getInt("position") as Int
        var listeIdDocumentOfPhoto: ArrayList<String> = extras.getStringArrayList("ListeIdDocOfphoto") as ArrayList<String>
        val listeTitrePhoto: ArrayList<String> = extras.getStringArrayList("ListeTitrePhotos") as ArrayList<String>
        val listeUrlPhoto: ArrayList<String> = extras.getStringArrayList("ListeUrlPhotos") as ArrayList<String>

        Log.i("tableau", "Id : "+listeIdDocumentOfPhoto)
        Log.i("tableau", "titre : "+listeTitrePhoto)
        Log.i("tableau", "url : "+listeUrlPhoto)


        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        //titre_photo_manager.text = listeTitrePhoto[position]

        val imageManager = findViewById<ImageView>(R.id.photoViewManager)
        Log.i("data", listeUrlPhoto[position])
        val image: Bitmap? = InternalStorageProvider(this).loadBitmap(listeUrlPhoto[position])
        imageManager.setImageBitmap(image)

        photo_manager_back_button.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.no_animation, R.anim.slide_down)
        }

        photo_manager_delete_button.setOnClickListener {

            val factory = LayoutInflater.from(this)
            var view: View = factory.inflate(R.layout.confirmation_delete_dialog, null);
            var preview: ImageView = view.findViewById(R.id.photo_preview_delete_manager)
            val image: Bitmap? = InternalStorageProvider(this).loadBitmap(listeUrlPhoto[position])
            preview.setImageBitmap(image)
            // DIALOG POUR DEMANDER CONFIRMATION DE SUPPRESION
            val dialog: AlertDialog = AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("Supprimer"
                ) { _, _ ->
                    Log.i("data", "Id a supprimer : "+ listeIdDocumentOfPhoto[position])

                    // DELETE IN DATABSE -----------------------------------------------------------------------
                    db.collection("Photos").document(listeIdDocumentOfPhoto[position]).delete()
                        .addOnSuccessListener {
                            db_delete_status = true

                            intent.putExtra("delete_key_position", position);
                            setResult(Activity.RESULT_OK, intent);
                        }
                        .addOnFailureListener { e -> Log.w("error", "Error deleting document", e) } // DELETE

                    // ------------------------------------------------------------------------------------------
                    // DELETE FILES STORAGE -----------------------------------------------------------------------

                    val fileRef = storageReference?.child("uploads/"+listeUrlPhoto[position])
                    fileRef?.delete()?.addOnSuccessListener {
                        if(db_delete_status) {   Toast.makeText(this@photoManagerActivity, "Photo supprimée", Toast.LENGTH_LONG).show() }
                        db_delete_status =false
                    }?.addOnFailureListener {
                        if(db_delete_status) {
                            Toast.makeText(
                                this@photoManagerActivity,
                                "Erreur de suppression de la photo mais supprimée de la database",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else {
                            Toast.makeText(
                                this@photoManagerActivity,
                                "Erreur de suppression de la photo",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    // ------------------------------------------------------------------------------------------

                    finish()
                }
                .setNegativeButton("Annuler"
                ) { _, _ ->  // QUEDAL
                }
                .create()
            dialog.show()
            // ----------------------------------------
        }

        photoViewManager.setOnTouchListener(object: OnSwipeTouchListener(this@photoManagerActivity) {
            override fun onSwipeTop() {
                Toast.makeText(this@photoManagerActivity, "Pense a faire une putain de transition effet vers le haut", Toast.LENGTH_SHORT).show()
                finish()
                overridePendingTransition(R.anim.no_animation, R.anim.slide_down)
            }
            override fun onSwipeRight() {
                if(position > 0) {
                    position -= 1
                    titre_photo_manager.text = listeTitrePhoto[position]
                    val image: Bitmap? = InternalStorageProvider(this@photoManagerActivity).loadBitmap(listeUrlPhoto[position])
                    imageManager.setImageBitmap(image)
                }

            }
            override fun onSwipeLeft() {
                if(position < listeUrlPhoto.size) {
                    position += 1
                    titre_photo_manager.text = listeTitrePhoto[position]
                    val image: Bitmap? = InternalStorageProvider(this@photoManagerActivity).loadBitmap(listeUrlPhoto[position])
                    imageManager.setImageBitmap(image)
                }
            }
            override fun onSwipeBottom() {
//                Toast.makeText(this@photoManagerActivity, "bottom", Toast.LENGTH_SHORT).show()
            }
        })


    }

}