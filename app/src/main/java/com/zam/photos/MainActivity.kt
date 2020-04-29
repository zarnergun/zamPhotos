package com.zam.photos

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.RemoteViews
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

class MainActivity : AppCompatActivity(),OnItemClickListener {

    private var name = ""
    var country_list = ArrayList<Model>()

    override fun onItemClicked(infos: Model) {
        Toast.makeText(this,"User name ${infos.texte} \n Phone:${infos.photo}",Toast.LENGTH_LONG)
            .show()
        Log.i("CLick", infos.texte)
    }

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false);

        country_list.add(Model("jaune", "https://images.pexels.com/photos/1363876/pexels-photo-1363876.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940"))
        country_list.add(Model("rouge", "https://www.declenchermalin.com/wp-content/uploads/2018/03/stage-photo-paris-texte-image.jpg"))
        country_list.add(Model("vert", "https://images.pexels.com/photos/1363876/pexels-photo-1363876.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940"))
        country_list.add(Model("orange", "https://images.pexels.com/photos/1363876/pexels-photo-1363876.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940"))


        val adapter = CountryAdapter(country_list, this)

        val recyclerView = findViewById(R.id.countriesRecyclerView) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        recyclerView.adapter = adapter


//        logTokenButton.setOnClickListener {
//            FirebaseInstanceId.getInstance().instanceId
//                .addOnCompleteListener(OnCompleteListener { task ->
//                    if (!task.isSuccessful) {
//                        Log.w(TAG, "getInstanceId failed", task.exception)
//                        return@OnCompleteListener
//                    }
//
//                    // Get new Instance ID token
//                    val token = task.result?.token
//
//                    // Log and toast
//                    val msg = getString(R.string.msg_token_fmt, token)
//                    Log.i("BLABLA", token)
//                    Log.d(TAG, msg)
//                    Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
//                })
//
//        }

    }

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
            val toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar)
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

    private fun sendNotif(messageBody: String, icon: Bitmap?) {

        // 1 - Create an Intent that will be shown when user will click on the Notification
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)


        // 2 - Create a Style for the Notification
        val inboxStyle =
            NotificationCompat.InboxStyle()
        inboxStyle.setBigContentTitle(getString(R.string.notification_title))

        // 3 - Create a Channel (Android 8)
        val channelId = getString(R.string.default_notification_channel_id)

        // 4 - Build a Notification object
//        Notification notificationBuilder =
//                new NotificationCompat.Builder(this, channelId)
//                        .setSmallIcon(R.drawable.ic_launcher_foreground)
//                        .setLargeIcon(doInBackground(icon))
//                        .setContentTitle(getString(R.string.app_name))
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                        .setContentIntent(pendingIntent)
//                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
//                        .build();
        val collapseView = RemoteViews(packageName, R.layout.notification_style)
        var expandView = RemoteViews(packageName, R.layout.notification_expend_short)
        if (messageBody.length > 40) {
            expandView = RemoteViews(packageName, R.layout.notification_expend_long)
        }

        collapseView.setTextViewText(R.id.text_1, messageBody)
        expandView.setTextViewText(R.id.text_expand, messageBody)
        expandView.setImageViewBitmap(R.id.image_expand, icon)
        val notificationBuilder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setCustomContentView(collapseView)
                .setCustomBigContentView(expandView)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .build()

        // 5 - Add the Notification to the Notification Manager and show it.
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 6 - Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName: CharSequence = "Message provenant de Firebase"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel =
                NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(mChannel)
        }

        // 7 - Show notification
        notificationManager.notify(2, notificationBuilder)
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
            R.id.notif -> {
                val messageLong = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
                val messageCourt = "Lorem Ipsum is simply dummy "
                val image = "https://www.site-libertin.com/wp-content/uploads/2017/08/gorge-profonde-suce.jpg"
                sendNotif(messageCourt, getBitmap(image).execute().get())
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
