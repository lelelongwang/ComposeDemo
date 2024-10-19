package com.loong.composedemo.core.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.loong.composedemo.core.network.model.NetworkArticle
import com.loong.composedemo.core.data.paging.ArticlePagingSource
import com.loong.composedemo.core.network.retrofit.RetrofitWanNetwork
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

class ArticleRepository @Inject constructor(
    private val network: RetrofitWanNetwork
) {
    companion object {
        private const val ARTICLE_PAGE_SIZE = 10
        private const val TAG = "ArticleRepository"
    }

    fun getHomeArticle(): Flow<PagingData<NetworkArticle>> {
        Log.d(TAG, "getHomeArticle: 1")
        return Pager(
            config = PagingConfig(
                initialLoadSize = 10,
                prefetchDistance = 5,
                enablePlaceholders = false,
                pageSize = ARTICLE_PAGE_SIZE
            ),
            pagingSourceFactory = {
                Log.d(TAG, "getHomeArticle: 2")
                ArticlePagingSource(network)
            }
        ).flow
    }
}