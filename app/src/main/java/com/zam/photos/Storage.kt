package com.zam.photos

import android.content.Context
import android.content.SharedPreferences

class Storage {
    private val sharedPreferences: SharedPreferences
    companion object {
        private val PREFS = "PREFS"
    }

    constructor(context: Context){
        sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    fun saveString(key: String, value: String) = sharedPreferences.edit().putString(key, value).apply()
    fun readString(key: String) = sharedPreferences.getString(key, null)
}