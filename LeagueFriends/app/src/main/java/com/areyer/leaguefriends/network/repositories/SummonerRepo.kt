package com.areyer.leaguefriends.network.repositories

import android.content.Context
import android.util.Log
import com.areyer.leaguefriends.Constants.DBKEY_SUMMONERS
import com.areyer.leaguefriends.network.RiotClient
import com.areyer.leaguefriends.network.Summoner
import com.areyer.leaguefriends.network.SummonerInfo
import com.areyer.leaguefriends.storage.Storage

class SummonerRepo(private val context: Context) {

    suspend fun getSummonerInfo(): List<SummonerInfo> {
        val storage = Storage(context)
        val summonerModels = mutableListOf<SummonerInfo>()
        val storedSummoners = storage.getList<Summoner>(DBKEY_SUMMONERS)
        storedSummoners.forEach { summoner ->
            val isActive = RiotClient.isSummonerActive(context, summoner.name)
            summonerModels.add(SummonerInfo(summoner.name, isActive))
        }

        return summonerModels
    }

    // Read summoner info from storage
    fun readSummoner(context: Context, summonerName: String): Summoner? {
        val storage = Storage(context)
        val storedSummoners = storage.getList<Summoner>(DBKEY_SUMMONERS).toMutableList()
        return storedSummoners.firstOrNull { it.name == summonerName }
    }

    // Retrieve Summoner, store in Storage, and return constructed SummonerInfo
    suspend fun addSummoner(summonerName: String): SummonerInfo? {
        Log.d(TAG, "addSummoner summonerName: $summonerName")
        val summoner = RiotClient.querySummoner(context, summonerName)
        return if (summoner != null) {
            val isActive = RiotClient.isSummonerActive(context, summonerName)
            return SummonerInfo(summoner.name, isActive)
        } else {
            null
        }
    }

    // Remove Summoner from Storage
    fun removeSummoner(summonerName: String) {
        val storage = Storage(context)
        val storedSummoners = storage.getList<Summoner>(DBKEY_SUMMONERS).toMutableList()
        storedSummoners.removeIf { it.name == summonerName }
        storage.store(DBKEY_SUMMONERS, storedSummoners)
    }

    companion object {
        private val TAG = SummonerRepo::class.java.canonicalName
    }
}