package com.zam.photos

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class CountryAdapter(val countries: ArrayList<Model>, val itemClickListener: OnItemClickListener)
    : RecyclerView.Adapter<CountryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(
            cardInfos: Model,
            clickListener: OnItemClickListener
        ) {
            val imageCardView = itemView.findViewById(R.id.fond_cardview_image) as ImageView
            val nameTextView = itemView.findViewById(R.id.texte_cardview) as TextView

            imageCardView.setImageBitmap(getBitmap(cardInfos.photo).execute().get())

            itemView.setOnClickListener {
                clickListener.onItemClicked(cardInfos)
                Log.w("click", "click listeer on holder")
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
        holder.bindItem(country, itemClickListener)
    }

    override fun getItemCount(): Int {
        return countries.size
    }

}

interface OnItemClickListener{
    fun onItemClicked(infos: Model)
}