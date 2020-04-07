package com.zam.photos
import com.zam.photos.GetDataLogin
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import retrofit2.converter.gson.GsonConverterFactory
import javax.security.auth.callback.Callback

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofitJson = Retrofit.Builder()
            .baseUrl("http://91.160.165.231")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val serviceJson = retroJson.create(HttpBinServiceJson::class.java)
        val callJson = serviceJson.getLoginInfo()
        callJson.enqueue(object: Callback<GetDataLogin>) {
            
        }
    }
}