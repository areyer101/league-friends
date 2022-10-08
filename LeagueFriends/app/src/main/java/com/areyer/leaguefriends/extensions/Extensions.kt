package com.areyer.leaguefriends.extensions

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

inline fun <reified T> String.fromJson() = Moshi.Builder()
    .build()
    .adapter(T::class.java)
    .fromJson(this)

inline fun <reified T> String.fromJsonList(): List<T>? = Moshi.Builder()
    .build()
    .adapter<List<T>>(Types.newParameterizedType(List::class.java, T::class.java))
    .fromJson(this)

fun Any.toJson(): String = Moshi.Builder()
    .build()
    .adapter(this.javaClass)
    .toJson(this)

fun List<Any>.toJsonList(): String = Moshi.Builder()
    .build()
    .adapter<List<Any>>(Types.newParameterizedType(List::class.java, Any::class.java))
    .toJson(this)