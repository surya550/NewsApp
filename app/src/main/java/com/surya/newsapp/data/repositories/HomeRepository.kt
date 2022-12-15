package com.surya.newsapp.data.repositories

import com.surya.newsapp.data.model.NewsDetail
import com.surya.newsapp.data.network.ApiInterface
import com.surya.newsapp.data.network.SafeApiRequest

class HomeRepository(
    private val api: ApiInterface
) : SafeApiRequest() {

    suspend fun getNews(
        countryCode: String,
        pageNumber: Int
    ): NewsDetail {

        return apiRequest { api.getNewsDetailData(countryCode,pageNumber) }
    }
    suspend fun searchNews(
        searchQuery: String,
        pageNumber: Int
    ): NewsDetail {

        return apiRequest { api.getSearchResultData(searchQuery,pageNumber) }
    }

}