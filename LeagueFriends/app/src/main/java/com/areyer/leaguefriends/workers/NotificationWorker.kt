package com.areyer.leaguefriends.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.areyer.leaguefriends.Constants.DBKEY_SUMMONERS
import com.areyer.leaguefriends.network.RiotClient
import com.areyer.leaguefriends.network.Summoner
import com.areyer.leaguefriends.network.SummonerInfo
import com.areyer.leaguefriends.storage.Storage
import com.areyer.leaguefriends.utils.NotificationUtils
import kotlinx.coroutines.*
import java.time.Duration

class NotificationWorker(private val context: Context, workerParams: WorkerParameters):
        Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d(TAG, "doWork")
        querySummoners()
        return Result.success()
    }

    private fun querySummoners() {
        Log.d(TAG, "querySummoners")
        val storedSummoners = Storage(context).getList<Summoner>(DBKEY_SUMMONERS)
        storedSummoners.forEach { summoner ->
            val isActive = runBlocking(Dispatchers.IO) {
                RiotClient.isSummonerActive(context, summoner.name)
            }

            Log.d(TAG, "querySummoners summonerInfo: ${SummonerInfo(summoner.name, isActive)}")
            if (isActive) {
                NotificationUtils.showOnlineNotification(summoner.name, context)
            }
        }

        scheduleSummonerQuery(context)
    }

    companion object {
        private val TAG = NotificationWorker::class.java.canonicalName
        private const val WORKER_NAME = "NOTIFICATION_WORKER"

        fun scheduleSummonerQuery(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.enqueueUniqueWork(WORKER_NAME, ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(Duration.ofMinutes(5))
                    .build())
        }
    }
}