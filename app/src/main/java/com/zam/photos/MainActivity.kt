package com.zam.photos

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azoft.carousellayoutmanager.CarouselLayoutManager
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.zam.photos.activities.LoginFirebaseActivity
import com.zam.photos.activities.ProfileActivity
import com.zam.photos.adapters.AdapterCardView
import com.zam.photos.models.Model_cardView
import com.zam.photos.tools.InternalStorageProvider
import com.zam.photos.tools.getBitmap
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var ListCardView = ArrayList<Model_cardView>()
    private var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val db = Firebase.firestore
    private val adapter = AdapterCardView(ListCardView, this)
    private var storage: FirebaseStorage? = null
//    var lastFirstVisiblePosition = 0
    private lateinit var recyclerView: RecyclerView
    private var recylerViewState: Parcelable? = null
    private val CAMERA_PERMISSION_CODE = 100
    private val STORAGE_PERMISSION_CODE = 101


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (user == null) {
            val intent = Intent(this, LoginFirebaseActivity::class.java)
            Toast.makeText(this, "Merci de bien vouloir vous connectez pour utiliser l'application", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }
        if (resultCode == Activity.RESULT_OK) {
            // CONVERT FILE FROM IMAGEPICKER TO BITMAP
            val bitmap: Bitmap = BitmapFactory.decodeFile(ImagePicker.getFile(data)?.path)
            val urlOfImage: String = ImagePicker.getFile(data)?.path.toString()
            val nameOfImage = ImagePicker.getFile(data)?.name.toString()
            InternalStorageProvider(this).saveBitmap(bitmap, nameOfImage)

            // DIALOG POUR DEMANDER LE NOM DE LA PHOTO
            val taskEditText = EditText(this)
            val dialog: AlertDialog = AlertDialog.Builder(this)
                .setTitle("Titre de la photo")
                .setView(taskEditText)
                .setPositiveButton("Add"
                ) { _, _ ->  addItemInListAndUpload(
                    taskEditText.text.toString(),
                    nameOfImage,
                    urlOfImage
                )
                }
                .create()
            dialog.show()
            // ----------------------------------------
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addItemInListAndUpload(
        textImage: String,
        nameOfImage: String,
        urlOfImage: String
    ) {
        ListCardView.add(Model_cardView(textImage, nameOfImage))
        // ADD IMAGE INFOS TO DATABASE

        val imageInfos = hashMapOf(
            "creator" to user?.email,
            "autoriseUser" to "Los Angeles",
            "dateCreated" to LocalDate.now().toString(),
            "message" to textImage,
            "title" to textImage,
            "url" to nameOfImage
        )

        db.collection("Photos").document(nameOfImage)
            .set(imageInfos)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

        // ----------------------------------------

        // ADD IMAGE TO STORAGE

        storage = FirebaseStorage.getInstance()
        val storageRef = storage?.reference
        val fileRef = storageRef?.child("uploads/$nameOfImage")
        Log.e("riversRef : ", fileRef.toString())

        val file = Uri.fromFile(File(urlOfImage))
        val uploadTask = fileRef?.putFile(file)
        Log.e("it : ", "avant upload")
        if (uploadTask != null) {
            uploadTask.addOnFailureListener {
                Toast.makeText(this, "Erreur", Toast.LENGTH_LONG).show()
            }.addOnSuccessListener {
                Toast.makeText(this, "Photo ajoutée", Toast.LENGTH_LONG).show()
                refreshData()
            }
        }
        Log.e("it : ", "après upload")

        // --------------------------------------
    }

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (user == null) {
            val intent = Intent(this, LoginFirebaseActivity::class.java)
            Toast.makeText(this, "Merci de bien vouloir vous connectez pour utiliser l'application", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }

        toolbar().getToolbar(this)

//        var toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar)
//        toolbar.setBackgroundResource(R.drawable.gradient_toolbar)
//
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayShowTitleEnabled(false);
//        supportActionBar?.setCustomView(R.layout.custom_action_bar_layout)


        swipeRefresh.setOnRefreshListener {
            getPhotosInDatabase()
        }


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


    override fun onResume() {
        super.onResume()
        getPhotosInDatabase()
        user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            val intent = Intent(this, LoginFirebaseActivity::class.java)
            Toast.makeText(this, "Merci de bien vouloir vous connectez pour utiliser l'application", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        recylerViewState = recyclerView.layoutManager!!.onSaveInstanceState()!!
        Log.i("info",  recylerViewState.toString())
    }

    fun refreshData() {
        Log.i("ListCardView", "Refresh "+ListCardView.toString() )
        val layoutManager = CarouselLayoutManager(CarouselLayoutManager.VERTICAL, false)
        layoutManager.setPostLayoutListener(CarouselZoomPostLayoutListener())
        recyclerView = findViewById(R.id.main_photosRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.hasFixedSize()
        recyclerView.adapter = adapter
        if(recylerViewState != null) {
            recyclerView.getLayoutManager()?.onRestoreInstanceState(recylerViewState);
        }
        else {
            recylerViewState = recyclerView.layoutManager!!.onSaveInstanceState()!!
        }
        // Hide swipe to refresh icon animation
        swipeRefresh.isRefreshing = false
    }

    fun getPhotosInDatabase() {
        ListCardView.clear()
        Log.i("ListCardView", ListCardView.toString() )
        db.collection("Photos").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("DOCUMENT", "${document.id} => ${document.data["url"]}")
                    ListCardView.add(Model_cardView(document.data["title"].toString(), document.data["url"].toString()))
                }
                Log.i("ListCardView", ListCardView.toString() )
                refreshData()
            }
            .addOnFailureListener { exception ->
                Log.w("DOCUMENT", "Error getting documents: ", exception)
                refreshData()
            }

    }

    companion object {
        val TAG = "MainActivity"
        fun getLaunchIntent(from: Context) = Intent(from, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
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

    fun checkPermission(permission: String, requestCode: Int) {

        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                permission
            )
            == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat
                .requestPermissions(
                    this@MainActivity, arrayOf(permission),
                    requestCode
                )
        } else {
            Toast
                .makeText(
                    this@MainActivity,
                    "Permission already granted",
                    Toast.LENGTH_SHORT
                )
                .show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.goToLogin -> {
                if (user != null) {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, LoginFirebaseActivity::class.java)
                    startActivity(intent)
                }
            }
            R.id.notif -> {
                val messageLong = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
                val messageCourt = "Lorem Ipsum is simply dummy "
                val image = "https://helpx.adobe.com/content/dam/help/en/stock/how-to/visual-reverse-image-search/jcr_content/main-pars/image/visual-reverse-image-search-v2_intro.jpg"
                sendNotif(messageCourt, getBitmap(image).execute().get())
            }
            R.id.addPic -> {
//                val intent = Intent(this, pickImageGallery::class.java)
//                startActivityForResult(intent,2);
                checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE)
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)
                ImagePicker.with(this)
                    .crop()	    			//Crop image(Optional), Check Customization for more option
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
