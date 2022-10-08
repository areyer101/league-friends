package com.areyer.leaguefriends.network

import retrofit2.http.GET
import retrofit2.http.Path

interface RiotService {

    @GET("/lol/summoner/v4/summoners/by-name/{summonerName}")
    suspend fun getSummoner(
        @Path("summonerName") summonerName: String): Summoner

    @GET("/lol/spectator/v4/active-games/by-summoner/{encryptedSummonerId}")
    suspend fun getActiveMatch(
        @Path("encryptedSummonerId") encryptedSummonerId: String): CurrentGameInfo
}