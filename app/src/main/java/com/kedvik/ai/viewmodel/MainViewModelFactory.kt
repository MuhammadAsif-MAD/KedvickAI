package com.kedvik.ai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kedvik.ai.repositories.ApiRepository


@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(private val repository: ApiRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}