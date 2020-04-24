package com.zam.photos

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.IBinder

class Notifs(messageBody: String, icon: Bitmap?) : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}