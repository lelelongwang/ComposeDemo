package com.loong.composedemo.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.loong.composedemo.core.network.model.NetworkArticle
import com.loong.composedemo.core.data.repository.ArticleRepository


import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val articleRepository: ArticleRepository
) : ViewModel() {

    private val _article = MutableStateFlow<PagingData<NetworkArticle>?>(null)
    val article: Flow<PagingData<NetworkArticle>> get() = _article.filterNotNull()

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "refreshData:")
                _article.value =
                    articleRepository.getHomeArticle().cachedIn(viewModelScope).first()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}