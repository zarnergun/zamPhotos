package com.zam.photos

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class toolbar() {
    fun getToolbar(view: Activity): AppCompatActivity {
        var toolbar: Toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setBackgroundResource(R.drawable.gradient_toolbar)

        (view as AppCompatActivity).setSupportActionBar(toolbar)
        (view as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false);
        (view as AppCompatActivity).supportActionBar?.setCustomView(R.layout.custom_action_bar_layout)
        return view
    }
}