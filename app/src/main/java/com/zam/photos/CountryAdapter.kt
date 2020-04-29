package com.zam.photos

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class CountryAdapter(val countries: Array<String>)
    : RecyclerView.Adapter<CountryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(country: String) {
            val iconImageView = itemView.findViewById(R.id.icon) as ImageView
            val nameTextView = itemView.findViewById(R.id.name) as TextView

            iconImageView.setImageResource(R.mipmap.ic_launcher_round)
            nameTextView.text = country
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

