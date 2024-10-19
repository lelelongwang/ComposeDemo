package com.loong.composedemo.core.network.retrofit

import com.loong.composedemo.core.network.model.NetworkArticle
import com.loong.composedemo.core.network.model.NetworkPage
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

@Serializable
data class NetworkResponse<T>(
    val data: T,
)

interface RetrofitWanNetwork {

    /**
     * 获取首页文章列表
     */
    @GET("/article/list/{page}/json")
    suspend fun getHomeArticle(
        @Path("page") page: Int
    ): NetworkResponse<NetworkPage<NetworkArticle>>

    companion object {
        private const val BASE_URL = "https://www.wanandroid.com/"

        fun create(): RetrofitWanNetwork {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitWanNetwork::class.java)
        }
    }
}
