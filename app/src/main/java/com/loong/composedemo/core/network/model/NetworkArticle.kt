package com.loong.composedemo.core.network.model

import kotlinx.serialization.Serializable

/**
 * Network representation of [Article]
 */
@Serializable
data class NetworkArticle(
    val id: Int,
    val originId: String = "",
    val author: String = "",
    val shareUser: String = "",
    val collect: String = "",
    val desc: String = "",
    val envelopePic: String = "",
    val title: String = "",
    val link: String = "",
    val niceDate: String = "",
)