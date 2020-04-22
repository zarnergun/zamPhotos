package com.zam.photos

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        fun onCreateOptionsMenu(menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.toolbarmain, menu)
            return true
        }

        fun onOptionsItemSelected(item: MenuItem): Boolean {
            return super.onOptionsItemSelected(item)
        }

        val database = Database(this)

        val user = database.getUsersCount()

        if(user > 0) {
            button.text = "profil"
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        else {
            button.text = "login"
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

}