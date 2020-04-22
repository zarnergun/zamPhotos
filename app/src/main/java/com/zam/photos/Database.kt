package com.zam.photos

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Database(context: Context) :
SQLiteOpenHelper(context, "user.db", null, 1){
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY, pseudo TEXT, email TEXT, pic TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, olvVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun createUser(user : User) {
        val values = ContentValues()
        values.put("pseudo", user.pseudo)
        values.put("email", user.email)
        values.put("pic", user.pic)

        writableDatabase.insert("users", null, values)
    }

    fun cleanUser() {
        writableDatabase.delete("users", null,null)
    }


    fun getAllUsers() : MutableList<User> {
        val users = mutableListOf<User>()
       readableDatabase.rawQuery("SELECT * FROM users", null).use {cursor ->
           cursor.moveToFirst()
           while(cursor.moveToNext()) {
               val user = User(
                   cursor.getString(cursor.getColumnIndex("pseudo")),
                   cursor.getString(cursor.getColumnIndex("email")),
                   cursor.getString(cursor.getColumnIndex("pic"))
               )
               users.add((user))
           }
       }
        return users
    }

    fun getUsersCount() : Int = DatabaseUtils.queryNumEntries(readableDatabase, "users", null).toInt()
}