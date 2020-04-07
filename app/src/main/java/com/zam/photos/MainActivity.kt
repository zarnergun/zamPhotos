package com.zam.photos
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofitJson = Retrofit.Builder()
            .baseUrl("http://91.160.165.231")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val serviceJson = retrofitJson.create(HttpBinServiceJson::class.java)
        val callJson: Call<GetDataLogin> = serviceJson.getLoginInfo()

        callJson.enqueue(object: Callback<GetDataLogin> {
            override fun onResponse(call: Call<GetDataLogin>, response: Response<GetDataLogin>) {
                Log.i("test", "Contenu : ${response.body()}");
            }

            override fun onFailure(call: Call<GetDataLogin>, t: Throwable) {
                Log.i("erreur", "Contenu : ${t}");
            }

        })
    }
}