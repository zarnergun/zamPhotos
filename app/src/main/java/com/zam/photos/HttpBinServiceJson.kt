package com.zam.photos

import retrofit2.Call
import retrofit2.http.POST

interface HttpBinServiceJson {

    @POST("post")
    fun getLoginInfo() : Call<GetDataLogin>

}