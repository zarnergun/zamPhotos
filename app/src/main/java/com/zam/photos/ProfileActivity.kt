package com.zam.photos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_profil.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        Log.i("appel", "PROFIL")

        val database = Database(this)

        button2.setOnClickListener {
            database.cleanUser()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        }
    }