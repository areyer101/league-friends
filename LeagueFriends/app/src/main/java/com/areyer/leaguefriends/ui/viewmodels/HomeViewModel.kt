package com.areyer.leaguefriends.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.areyer.leaguefriends.network.SummonerInfo
import com.areyer.leaguefriends.network.repositories.SummonerRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val summonerRepo: SummonerRepo): ViewModel() {
    private val _state = MutableStateFlow(emptyList<SummonerInfo>())

    val state: StateFlow<List<SummonerInfo>>
        get() = _state

    init {
        viewModelScope.launch {
            val summoners = summonerRepo.getSummonerInfo()
            _state.value = summoners
        }
    }

    suspend fun addSummoner(summonerName: String): Boolean {
        val summoners = _state.value.toMutableList()
        if (summoners.firstOrNull { it.name == summonerName } != null || summonerName.isEmpty()) {
            Log.d(TAG, "addSummoner: Summoner already exists")
            return false
        }

        val summonerInfo = summonerRepo.addSummoner(summonerName)
        return if (summonerInfo != null) {
            summoners.add(summonerInfo)
            _state.value = summoners
            true
        } else {
            false
        }
    }

    fun removeSummoner(summonerName: String) {
        val summoners = _state.value.toMutableList()
        summoners.removeIf {it.name == summonerName}
        summonerRepo.removeSummoner(summonerName)
        _state.value = summoners
    }

    companion object {
        private val TAG = HomeViewModel::class.java.canonicalName
    }
}