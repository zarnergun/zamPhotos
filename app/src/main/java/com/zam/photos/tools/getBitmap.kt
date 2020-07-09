package com.zam.photos.tools

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class getBitmap(image: String) : AsyncTask<String, Int, Bitmap>() {

    val url = URL(image)

    override fun doInBackground(vararg p0: String?): Bitmap {
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.getInputStream()
            return BitmapFactory.decodeStream(input)
    }
}