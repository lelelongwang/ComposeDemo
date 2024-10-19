package com.loong.composedemo.core.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.loong.composedemo.core.network.model.NetworkArticle
import com.loong.composedemo.core.network.retrofit.RetrofitWanNetwork
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException

private const val ARTICLE_STARTING_PAGE_INDEX = 0
private const val TAG = "ArticlePagingSource"

class ArticlePagingSource(
    private val network: RetrofitWanNetwork
) : PagingSource<Int, NetworkArticle>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NetworkArticle> {
        val page = params.key ?: ARTICLE_STARTING_PAGE_INDEX
        return try {
            Log.d(TAG, "load: page=$page ，params.key=${params.key}")
            val response = network.getHomeArticle(page)
            delay(3000)//模拟网络延迟
            Log.d(TAG, "load: page=$page, pageCount=${response.data.pageCount}, curPage=${response.data.curPage},size=${response.data.size}")
            LoadResult.Page(
                data = response.data.datas,
                prevKey = if (page == ARTICLE_STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (page == response.data.pageCount) null else page + 1,
//                nextKey = null,
            )
        } catch (e: IOException) {
            // IOException for network failures.)
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            return LoadResult.Error(e)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, NetworkArticle>): Int? {
        /*return state.anchorPosition?.let { anchorPosition ->
            // This loads starting from previous page, but since PagingConfig.initialLoadSize spans
            // multiple pages, the initial load will still load items centered around
            // anchorPosition. This also prevents needing to immediately launch prepend due to
            // prefetchDistance.
            state.closestPageToPosition(anchorPosition)?.prevKey
        }*/
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}