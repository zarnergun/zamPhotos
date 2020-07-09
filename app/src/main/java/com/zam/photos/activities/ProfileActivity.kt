package com.zam.photos.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.zam.photos.MainActivity
import com.zam.photos.R
import com.zam.photos.adapters.AdapterGridView
import kotlinx.android.synthetic.main.activity_profil.*
import java.io.File


class ProfileActivity : AppCompatActivity(), AdapterGridView.ItemClickListener {

    private val db = Firebase.firestore
    private var TAG = "profil :"
    private val REQUEST_CODE_DELETE = 2
    var listeIdDocumentOfPhotos = ArrayList<String>()
    var listeUrlPhotos = ArrayList<String>()
    var listeTitrePhotos = ArrayList<String>()
    private val user = FirebaseAuth.getInstance().currentUser
    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null
    private lateinit var adapter: AdapterGridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)
        Log.w("enter :", "profil")

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        //var islandRef = storageReference!!.child(photo)
        var islandRef = storageReference?.child("profils_images/${user?.email}.jpg")

        val ONE_MEGABYTE: Long = 1024 * 1024
        if (islandRef != null) {
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                val image: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                Log.e("it : ", it.size.toString())
                val profileImage = findViewById<ImageView>(R.id.profile_image)
                profileImage.setImageBitmap(image)
            }?.addOnFailureListener {
                Log.e("error : ", it.toString())
            }
        }

        if (user != null) {
            profile_pseudo.text = user.email
        }

        // DECLARATION DES PHOTOS DE L UTILISATEUR

        getImageFromDatabase()

        // ---------------------------------------

//


        // TOOLBAR -----------------------------------
        var myToolbar = com.zam.photos.toolbar().getToolbar(this)
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        myToolbar.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        myToolbar.supportActionBar?.setDisplayShowHomeEnabled(true)
        myToolbar.supportActionBar?.setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener { finish() }
        // ---------------------------------------------

// Create a new user with a first, middle, and last name
//        val User = hashMapOf(
//            "isAdmin" to "false",
//            "urlPic" to (user?.photoUrl.toString()),
//            "name" to (user?.displayName.toString())
//        )

        profile_image.setOnClickListener {
            com.github.dhaval2404.imagepicker.ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        logOutbutton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getImageFromDatabase()
        Log.i("request", requestCode.toString())
        if (resultCode == Activity.RESULT_OK && requestCode == 2404) {
            val bitmap: Bitmap = BitmapFactory.decodeFile(ImagePicker.getFile(data)?.path)
            var urlOfImage: String = ImagePicker.getFile(data)?.path.toString()
            val nameOfImage = ImagePicker.getFile(data)?.name.toString()
            findViewById<ImageView>(R.id.profile_image).setImageBitmap(bitmap)
            addAvatarAndUpload(nameOfImage, bitmap, urlOfImage)
        }

    }

    private fun addAvatarAndUpload(
        nameOfImage: String,
        bitmap: Bitmap,
        urlOfImage: String
    ) {
        // ADD IMAGE INFOS TO DATABASE

        val imageInfos = hashMapOf(
            "urlPic" to user
        )
        if (user != null) {
            user.email?.let {
                db.collection("Users").document(it)
                    .set(
                        hashMapOf("urlPic" to "profils_images/${user.email}.jpg", "name" to user.displayName)
                    )
                    .addOnSuccessListener {
                        Log.d(
                            MainActivity.TAG,
                            "DocumentSnapshot successfully written!"
                        )
                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            MainActivity.TAG,
                            "Error writing document",
                            e
                        )
                    }
            }
        }

        // ----------------------------------------

        // ADD IMAGE TO STORAGE

        storage = FirebaseStorage.getInstance()
        val storageRef = storage?.reference
        val riversRef = storageRef?.child("profils_images/${user?.email}.jpg")
        Log.e("riversRef : ", riversRef.toString())

        var file = Uri.fromFile(File(urlOfImage))
        var uploadTask = riversRef?.putFile(file)
        Log.e("it : ", "avant upload")
        if (uploadTask != null) {
            uploadTask.addOnFailureListener {
                Toast.makeText(this, "Erreur", Toast.LENGTH_LONG).show()
            }.addOnSuccessListener {
                Toast.makeText(this, "Photo ajoutée", Toast.LENGTH_LONG).show()
            }
        }
        Log.e("it : ", "après upload")

        // --------------------------------------
    }

    override fun onItemClick(vue: View?, position: Int) {

        val intent = Intent(vue?.context, photoManagerActivity::class.java)
        intent.putExtra("ListeTitrePhotos", listeTitrePhotos)
        intent.putExtra("ListeUrlPhotos", listeUrlPhotos)
        intent.putExtra("ListeIdDocOfphoto", listeIdDocumentOfPhotos)
        intent.putExtra("position", position)
        startActivityForResult(intent,REQUEST_CODE_DELETE);
        overridePendingTransition(R.anim.slide_up, R.anim.no_animation)
    }

    fun  provisionningArray() {
        listeUrlPhotos.clear()
        listeTitrePhotos.clear()
        listeIdDocumentOfPhotos.clear()
        for (i in 0 until adapter.itemCount) {
            listeTitrePhotos.add(adapter.getItemTitre(i))
            listeUrlPhotos.add(adapter.getItemUrl(i))
            listeIdDocumentOfPhotos.add(adapter.getItemIdOfDoc(i))
        }
    }

    fun getImageFromDatabase() {

        var tabUrlPhoto = ArrayList<String>()
        var tabTitrePhoto = ArrayList<String>()
        var tabIdDocOfPhoto = ArrayList<String>()


        db.collection("Photos").whereEqualTo("creator", user?.email)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    tabUrlPhoto.add(document.data["url"].toString())
                    tabTitrePhoto.add(document.data["title"].toString())
                    tabIdDocOfPhoto.add(document.id)

                    val vue: RecyclerView = findViewById(R.id.recyclerGrid)
                    adapter = AdapterGridView(this, tabUrlPhoto, tabTitrePhoto, tabIdDocOfPhoto)
                    val numberOfColumns = 4
                    vue.setLayoutManager(GridLayoutManager(this, numberOfColumns))
                    adapter.setClickListener(this)
                    vue.adapter = adapter
                    provisionningArray()
                    //Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                tabIdDocOfPhoto.clear()
                tabTitrePhoto.clear()
                tabUrlPhoto.clear()
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }


}