package com.zam.photos.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.zam.photos.R
import com.zam.photos.tools.InternalStorageProvider

class AdapterGridView internal constructor(
    contexte: Context?,
    tabUrlPhoto: ArrayList<String>,
    tabTitrePhoto: ArrayList<String>,
    tabIdDocOfPhoto: ArrayList<String>
) : RecyclerView.Adapter<AdapterGridView.ViewHolder>() {
    private val tabUrlPhoto: ArrayList<String>
    private val tabTitrePhoto: ArrayList<String>
    private val tabIdDocOfPhoto: ArrayList<String>
    private var contexte: Context? = contexte
    private val mInflater: LayoutInflater = LayoutInflater.from(contexte)
    private var mClickListener: ItemClickListener? = null
    private var storage: FirebaseStorage = FirebaseStorage.getInstance()
    private var storageReference: StorageReference = storage!!.reference

    // Met en place l'affichage de la vue à partir des éléments des fichiers XML
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View = mInflater.inflate(R.layout.item_gridview, parent, false)
        return ViewHolder(view)
    }

    // Méthode qui remplit la "ImageView" avec la donnée
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        contexte?.let { loadImage(it, tabUrlPhoto[position], holder) }
    }

    private fun loadImage(
        context: Context,
        url: String,
        holder: ViewHolder
    ) {
        var tryLoadBitmap: Bitmap? = InternalStorageProvider(
            context
        ).loadBitmap(url)
        if(tryLoadBitmap == null) {
            var urlPic = "uploads/${url}"
            var islandRef = storageReference!!.child(urlPic)
            val ONE_MEGABYTE: Long = 1024 * 1024
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                // Data for "images/island.jpg" is returned, use this as needed
                val image: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                holder.maVue.setImageBitmap(image)
                InternalStorageProvider(context)
                    .saveBitmap(image, url)

            }.addOnFailureListener {
                // Handle any errors
            }
//            val image: Bitmap = getBitmap(cardInfos.photo).execute().get()
        }
        else {
            holder.maVue.setImageBitmap(tryLoadBitmap)
        }
    }

    //Méthode comptant le nombre total de cellules
    override fun getItemCount(): Int {
        return tabUrlPhoto.size
    }

    // Méthode qui stocke et réutilise les cellules au fur et à mesure du défilement de l'écran
    inner class ViewHolder internal constructor(objet: View) :
        RecyclerView.ViewHolder(objet), View.OnClickListener {
        var maVue: ImageView = objet.findViewById(R.id.image_grid)
        override fun onClick(objet: View) {
            if (mClickListener != null) mClickListener!!.onItemClick(objet, adapterPosition)
        }

        init {
            Log.i("Donnee","enter in init")
            objet.setOnClickListener(this)

        }
    }

    // Méthode pour détecter quel objet a été cliqué
    fun getItemUrl(id: Int): String {
        return tabUrlPhoto[id]
    }

    fun getItemTitre(id: Int): String {
        return tabTitrePhoto[id]
    }

    fun getItemIdOfDoc(id: Int): String {
        return tabIdDocOfPhoto[id]
    }

    // Méthode liant la fonction de gestion du clic à l'évènement de clic
    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    // Les activités parentes devront implémenter cette méthode pour répondre au clic sur la vue
    interface ItemClickListener {
        fun onItemClick(vue: View?, position: Int)
    }

    //Les données de la grille sont passées en paramètre au constructeur de la classe
    init {
        Log.i("Donnee","enter in data init")
        this.tabUrlPhoto = tabUrlPhoto
        this.tabTitrePhoto = tabTitrePhoto
        this.tabIdDocOfPhoto = tabIdDocOfPhoto
        Log.i("first value", tabUrlPhoto.toString())
    }
}