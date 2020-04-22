package com.zam.photos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.btn
import kotlinx.android.synthetic.main.activity_main.textView
import org.json.JSONObject

class LoginActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_login)

        btn.setOnClickListener {
            val url = "http://91.160.165.231/userLogin.php"
            textView.text = ""

            // Post parameters
            // Form fields and values
            val params = HashMap<String,String>()
            params["pseudo"] = pseudoInput.text.toString()
            params["pass"] = passwordInput.text.toString()
            val jsonObject = JSONObject(params as Map<*, *>)

            // Volley post request with parameters
            val request = JsonObjectRequest(Request.Method.POST,url,jsonObject,
                Response.Listener { response ->
                    // Process the json
                    try {
                        val pseudo = response

                        textView.text = "Response: $response"
                    }catch (e:Exception){
                        textView.text = "Exception: $e"
                    }

                }, Response.ErrorListener{
                    // Error in request
                    textView.text = "Volley error: $it"
                })


            // Volley request policy, only one time request to avoid duplicate transaction
            request.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                // 0 means no retry
                0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
                1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )

            // Add the volley post request to the request queue
            VolleySingleton.getInstance(this).addToRequestQueue(request)
        }

    }
}