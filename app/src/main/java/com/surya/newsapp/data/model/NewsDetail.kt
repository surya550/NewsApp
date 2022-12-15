package com.surya.newsapp.data.model


data class NewsDetail(
    val articles: List<Article?>?,
    val status: String?,
    val totalResults: Int?,
    val code: String?,
    val message: String?,
)

data class Article(
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val url: String?,
    val urlToImage: String?
)

data class Source(
    val id: Any?,
    val name: String?
)