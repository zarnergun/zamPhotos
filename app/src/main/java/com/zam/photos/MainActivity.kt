package com.zam.photos

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 9001
    private var name = ""

    companion object {
        private val TAG = "MainActivity"
        fun getLaunchIntent(from: Context) = Intent(from, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    val mTarget: Target = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap?, loadedFrom: Picasso.LoadedFrom?) {
            Log.d("DEBUG", "onBitmapLoaded")
            val mBitmapDrawable = BitmapDrawable(resources, bitmap)
            //                                mBitmapDrawable.setBounds(0,0,24,24);
            // setting icon of Menu Item or Navigation View's Menu Item
            var toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar)
            var menu = toolbar.menu
            var item = menu.findItem(R.id.goToLogin)
            item?.setIcon(mBitmapDrawable)
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false);
        }

        override fun onPrepareLoad(drawable: Drawable?) {
            Log.d("DEBUG", "onPrepareLoad")
        }

        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            TODO("Not yet implemented")
        }
    }

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar)

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            Log.i("DATA_USER : ", user.photoUrl.toString())



            val pic = Picasso.get().load(user.photoUrl).into(mTarget)
            name = user.email.toString()
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false);

        logTokenButton.setOnClickListener {
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token

                    // Log and toast
                    val msg = getString(R.string.msg_token_fmt, token)
                    Log.i("BLABLA", token)
                    Log.d(TAG, msg)
                    Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
                })

        }

    }

    private fun signOut() {
        startActivity(LoginFirebaseActivity.getLaunchIntent(this))
        FirebaseAuth.getInstance().signOut();
    }

    public fun ToastMessage() {

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.goToLogin -> {
               if (name != "") {
                   val intent = Intent(this, ProfileActivity::class.java)
                   startActivity(intent)
                }
                else {
                    val intent = Intent(this, LoginFirebaseActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
