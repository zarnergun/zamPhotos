package com.zam.photos

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView

class photoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        var image: Bitmap? = InternalStorageProvider(this).loadBitmap("temp")

        val imageCardView = findViewById(R.id.photoFullScreen) as ImageView
        imageCardView.setImageBitmap(image)

    }

}
