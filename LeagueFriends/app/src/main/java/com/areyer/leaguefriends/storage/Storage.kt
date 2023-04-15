package com.areyer.leaguefriends.storage

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import com.areyer.leaguefriends.extensions.fromJson
import com.areyer.leaguefriends.extensions.fromJsonList
import com.areyer.leaguefriends.extensions.toJson
import com.areyer.leaguefriends.extensions.toJsonList

class Storage(context: Context) {
    val sharedPreferences: SharedPreferences = context
        .getSharedPreferences(context.packageName, MODE_PRIVATE)

    fun storeValue(key: String, value: String) =
        sharedPreferences.edit().putString(key, value).apply()

    fun store(key: String, value: Any) {
        sharedPreferences.edit().putString(key, value.toJson()).apply()
    }

    fun store(key: String, value: List<Any>) {
        Log.d(TAG, "store string: $key, value: ${value.toJsonList()}")
        sharedPreferences.edit().putString(key, value.toJsonList()).apply()
    }

    fun getValue(key: String): String? = sharedPreferences.getString(key, null)

    inline fun <reified T> get(key: String): T? {
        return sharedPreferences.getString(key, null)?.fromJson<T>()
    }

    inline fun <reified T> getList(key: String): List<T> {
        return sharedPreferences.getString(key, null)?.fromJsonList() ?: listOf()
    }

    companion object {
        private val TAG = Storage::class.java.canonicalName
    }
}