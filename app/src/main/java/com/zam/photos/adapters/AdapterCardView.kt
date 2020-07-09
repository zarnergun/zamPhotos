package com.zam.photos.adapters

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.zam.photos.R
import com.zam.photos.activities.photoActivity
import com.zam.photos.models.Model_cardView
import com.zam.photos.tools.InternalStorageProvider
import org.w3c.dom.Text


class AdapterCardView(
    val cards: ArrayList<Model_cardView>,
    val thisActivity: Activity
)
    : RecyclerView.Adapter<AdapterCardView.ViewHolder>() {

    var positionInAdapter = 0

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(
            cardInfos: Model_cardView,
            thisActivity: Activity,
            db: FirebaseFirestore = Firebase.firestore
        ) {
            val imageCardView = itemView.findViewById(R.id.fond_cardview_image) as ImageView
            val storage: FirebaseStorage = FirebaseStorage.getInstance()
            val storageReference: StorageReference = storage!!.reference
            val nameTextView = itemView.findViewById(R.id.texte_cardview) as TextView
            val main: androidx.constraintlayout.widget.ConstraintLayout = itemView.findViewById(
                R.id.main
            )

            var countComms = 0

            db.collection("commentaires").whereEqualTo("liaison", cardInfos.photo).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        countComms++
                        Log.d("comment", "$countComms : ${document.id} => ${document.data}")
                    }

                    val badge = itemView.findViewById(R.id.badge_notification) as TextView
                    badge.text = countComms.toString()
                }


            var tryLoadBitmap: Bitmap? = InternalStorageProvider(
                itemView.context
            ).loadBitmap(cardInfos.photo)
            if(tryLoadBitmap == null) {
                var urlPic = "uploads/${cardInfos.photo}"
                var islandRef = storageReference!!.child(urlPic)
                val ONE_MEGABYTE: Long = 1024 * 1024
                islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                    // Data for "images/island.jpg" is returned, use this as needed
                    val image: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    imageCardView.setImageBitmap(image)
                    InternalStorageProvider(itemView.context)
                        .saveBitmap(image, cardInfos.photo)

                }.addOnFailureListener {
                    // Handle any errors
                }
//            val image: Bitmap = getBitmap(cardInfos.photo).execute().get()
            }
            else {
                imageCardView.setImageBitmap(tryLoadBitmap)
            }

            main.setOnClickListener {
                    val intent = Intent(itemView.context, photoActivity::class.java)
                  intent.putExtra("photo", cardInfos.photo)
                intent.putExtra("titre", cardInfos.texte)

                itemView.context.startActivity(intent)

            }

            nameTextView.text = cardInfos.texte
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewItem = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_card, parent, false)

        return ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val startTime = System.currentTimeMillis()
        val card = cards[position]
        holder.bindItem(card, thisActivity)
        positionInAdapter = holder.layoutPosition
        Log.i("position", positionInAdapter.toString());
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    fun getPosition(holder: ViewHolder): Int {
        return holder.layoutPosition
    }

}

interface OnItemClickListener{
    fun onItemClicked(infos: Model_cardView)
}