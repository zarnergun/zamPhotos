package com.zam.photos

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class InternalStorageProvider(var context: Context) {

    fun saveBitmap(bitmap: Bitmap, imageName: String) {
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = context.openFileOutput(imageName, Context.MODE_PRIVATE)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fileOutputStream?.close()
        }
    }

    fun loadBitmap(picName: String): Bitmap? {
        var bitmap: Bitmap? = null
        var fileInputStream: FileInputStream? = null
        try {
            fileInputStream = context.openFileInput(picName)
            bitmap = BitmapFactory.decodeStream(fileInputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fileInputStream?.close()
        }

        return bitmap
    }
}