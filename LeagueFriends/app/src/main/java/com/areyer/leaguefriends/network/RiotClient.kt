package com.areyer.leaguefriends.network

import android.content.Context
import android.util.Log
import com.areyer.leaguefriends.BuildConfig.RIOT_API_KEY
import com.areyer.leaguefriends.Constants.DBKEY_SUMMONERS
import com.areyer.leaguefriends.storage.Storage
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.Exception

object RiotClient {

    private val TAG = RiotClient::class.java.canonicalName

    const val NA_BASE_URL = "https://na1.api.riotgames.com"
    const val AMERICAS_BASE_URL = "https://americas.api.riotgames.com"

    const val AUTH_HEADER = "X-Riot-Token"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    suspend fun getSummoner(context: Context, summonerName: String): Summoner? {
        // Read summoner info from storage
        val storage = Storage(context)
        val storedSummoners = storage.getList<Summoner>(DBKEY_SUMMONERS).toMutableList()
        var summoner: Summoner? = storedSummoners.firstOrNull { it.name == summonerName }

        // If no stored summoner, query from server
        if (summoner == null) {
            summoner = try {
                summoner = RiotCommandFactory().createCommand().getSummoner(summonerName)
                storedSummoners.add(summoner)
                storage.store(DBKEY_SUMMONERS, storedSummoners)
                summoner
            } catch (e: Exception) {
                Log.d(TAG, "getSummoner error finding summoner with provided name")
                null
            }
        }

        return summoner
    }

    suspend fun isSummonerActive(context: Context, summonerName: String): Boolean {
        return try {
            val summonerId = getSummoner(context, summonerName)?.id ?: return false
            RiotCommandFactory().createCommand().getActiveMatch(summonerId)
        } catch (e: Exception) {
            Log.d(TAG, "getActivateMatch no matches found")
            null
        } != null
    }

    class RiotCommandFactory {

        var sOkHttpClient: OkHttpClient? = null

        private fun getDefaultOkHttpClient(): OkHttpClient {
            if (sOkHttpClient == null) {
                sOkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
                    val newRequest = chain
                        .request()
                        .newBuilder()
                        .header(AUTH_HEADER, RIOT_API_KEY)
                        .build()
                    chain.proceed(newRequest)
                }.build()
           }

            return sOkHttpClient!!
        }

        fun createCommand(useRegionalHost: Boolean = false): RiotService =
            Retrofit.Builder().baseUrl(if (useRegionalHost) AMERICAS_BASE_URL else NA_BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(getDefaultOkHttpClient())
                .build()
                .create(RiotService::class.java)
    }
}