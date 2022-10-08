package com.areyer.leaguefriends.utils

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.areyer.leaguefriends.Constants.DBKEY_NOTIFIED
import com.areyer.leaguefriends.Constants.NOTIFICATION_DELAY
import com.areyer.leaguefriends.Constants.NOTIFICATION_ID_GENERAL
import com.areyer.leaguefriends.R
import com.areyer.leaguefriends.storage.Storage
import java.util.*

object NotificationUtils {
    val TAG: String? = NotificationUtils::class.java.canonicalName

    fun showOnlineNotification(summonerName: String, context: Context) {
        if (!shouldNotify(summonerName, context)) {
            return
        }

        val builder = NotificationCompat.Builder(context, NOTIFICATION_ID_GENERAL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText("$summonerName is online now.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationId = (Date().time / 1000L % Integer.MAX_VALUE).toInt()

        with(NotificationManagerCompat.from(context)) {
            Storage(context).storeValue(getNotificationKey(summonerName),
                System.currentTimeMillis().toString())
            notify(notificationId, builder.build())
        }
    }

    private fun shouldNotify(summonerName: String, context: Context): Boolean {
        val storage = Storage(context)
        val key = getNotificationKey(summonerName)
        val lastNotificationTime = storage.getValue(key)?.toLong() ?: 0
        val delta = System.currentTimeMillis() - lastNotificationTime
        return if (delta > NOTIFICATION_DELAY) {
            true
        } else {
            Log.d(TAG, "shouldNotify: Too soon to notify about $summonerName," +
                " minutes left: ${((NOTIFICATION_DELAY - delta) / 1000L) / 60L}")
            false
        }
    }

    private fun getNotificationKey(summonerName: String): String = "$summonerName$DBKEY_NOTIFIED"
}