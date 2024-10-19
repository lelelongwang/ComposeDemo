package com.loong.composedemo.core.network.model

import kotlinx.serialization.Serializable

/**
 * Network representation of [page]
 */
@Serializable
data class NetworkPage<T>(
    val curPage: Int,
    val datas: List<T> = listOf(),
    val offset: Int,
    val pageCount: Int,
    val size: Int,
    val total: Int,
)