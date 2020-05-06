package com.zam.photos

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.zam.photos.InternalStorageProvider
import java.io.*


class CountryAdapter(val countries: ArrayList<Model>)
    : RecyclerView.Adapter<CountryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(
            cardInfos: Model
        ) {
            val imageCardView = itemView.findViewById(R.id.fond_cardview_image) as ImageView
            val nameTextView = itemView.findViewById(R.id.texte_cardview) as TextView
            val main: android.support.constraint.ConstraintLayout = itemView.findViewById(R.id.main)
            val image: Bitmap = getBitmap(cardInfos.photo).execute().get()
            imageCardView.setImageBitmap(image)
            InternalStorageProvider(itemView.context).saveBitmap(image, "temp")
            main.setOnClickListener {
                Log.i("click","click")
                    Toast.makeText(itemView.getContext(), "Position:" + Integer.toString(getPosition()), Toast.LENGTH_SHORT).show();
                    val intent = Intent(itemView.context, photoActivity::class.java)
//                    intent.putExtra("photo", image)
                    itemView.context.startActivity(intent)
            }

            nameTextView.text = cardInfos.texte
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewItem = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_country, parent, false)

        return ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country = countries[position]
        holder.bindItem(country)
    }

    override fun getItemCount(): Int {
        return countries.size
    }

}

interface OnItemClickListener{
    fun onItemClicked(infos: Model)
}