package com.areyer.leaguefriends.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Summoner(
    val accountId: String,
    val id: String,
    val name: String,
    val profileIconId: Int,
    val puuid: String,
    val revisionDate: Long,
    val summonerLevel: Int
)

data class SummonerInfo(
    val name: String,
    val online: Boolean,
)

@JsonClass(generateAdapter = true)
data class CurrentGameInfo(
    val bannedChampions: List<BannedChampion>,
    val gameId: Long,
    val gameLength: Int,
    val gameMode: String,
    val gameQueueConfigId: Int,
    val gameStartTime: Long,
    val gameType: String,
    val mapId: Int,
    val observers: Observers,
    val participants: List<Participant>,
    val platformId: String
)

@JsonClass(generateAdapter = true)
data class BannedChampion(
    val championId: Int,
    val pickTurn: Int,
    val teamId: Int
)

@JsonClass(generateAdapter = true)
data class Observers(
    val encryptionKey: String
)

@JsonClass(generateAdapter = true)
data class Participant(
    val bot: Boolean,
    val championId: Int,
    val gameCustomizationObjects: List<Any>,
    val perks: Perks,
    val profileIconId: Int,
    val spell1Id: Int,
    val spell2Id: Int,
    val summonerId: String,
    val summonerName: String,
    val teamId: Int
)

@JsonClass(generateAdapter = true)
data class Perks(
    val perkIds: List<Int>,
    val perkStyle: Int,
    val perkSubStyle: Int
)