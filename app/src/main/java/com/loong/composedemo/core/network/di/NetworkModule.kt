package com.loong.composedemo.core.network.di

import com.loong.composedemo.core.network.retrofit.RetrofitWanNetwork
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofitWanNetwork(): RetrofitWanNetwork {
        return RetrofitWanNetwork.create()
    }
}
