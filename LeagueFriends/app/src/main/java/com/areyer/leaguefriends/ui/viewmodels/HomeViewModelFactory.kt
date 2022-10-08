package com.areyer.leaguefriends.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.areyer.leaguefriends.network.repositories.SummonerRepo

class HomeViewModelFactory(private val summonerRepo: SummonerRepo): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = HomeViewModel(summonerRepo) as T
}