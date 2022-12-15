package com.surya.newsapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surya.newsapp.data.model.Article
import com.surya.newsapp.data.model.NewsDetail
import com.surya.newsapp.data.repositories.HomeRepository
import com.surya.newsapp.util.ApiException
import com.surya.newsapp.util.AppConstant
import com.surya.newsapp.util.NoInternetException
import com.surya.newsapp.util.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {


    private var pageIndex = 0
    private var totalNews = 0
    private var newsList = ArrayList<Article?>()

    private val _newsLiveData = MutableLiveData<State<ArrayList<Article?>>>()
    val newsLiveData: LiveData<State<ArrayList<Article?>>>
        get() = _newsLiveData

    private val _newsNameLiveData = MutableLiveData<String>()
    val newsNameLiveData: LiveData<String>
        get() = _newsNameLiveData

    private val _loadMoreListLiveData = MutableLiveData<Boolean>()
    val loadMoreListLiveData: LiveData<Boolean>
        get() = _loadMoreListLiveData

    private lateinit var newsResponse: NewsDetail

    init {
        _loadMoreListLiveData.value = false
        _newsNameLiveData.value = ""
    }

    fun getNews() {
        if (pageIndex == 1) {
            newsList.clear()
            _newsLiveData.postValue(State.loading())
        } else {
            if (newsList.isNotEmpty() && newsList.last() == null)
                newsList.removeAt(newsList.size - 1)
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                newsResponse = repository.getNews(
                    AppConstant.CountryCode,
                    pageIndex
                )
                withContext(Dispatchers.Main) {

                    if (newsResponse.status == AppConstant.SUCCESS) {

                        newsResponse.articles?.let { newsList.addAll(it) }
                        totalNews = newsResponse.totalResults!!.toInt()
                        _newsLiveData.postValue(State.success(newsList))
                        _loadMoreListLiveData.value = false
                    } else
                        _newsLiveData.postValue(newsResponse.message?.let { State.error(it) })
                }
            } catch (e: ApiException) {
                withContext(Dispatchers.Main) {
                    _newsLiveData.postValue(State.error(e.message!!))
                    _loadMoreListLiveData.value = false
                }
            } catch (e: NoInternetException) {
                withContext(Dispatchers.Main) {
                    _newsLiveData.postValue(State.error(e.message!!))
                    _loadMoreListLiveData.value = false
                }
            }


        }
    }

    fun loadMore() {
        pageIndex++
        getNews()
    }

    fun checkForLoadMoreItems(
        visibleItemCount: Int,
        totalItemCount: Int,
        firstVisibleItemPosition: Int
    ) {
        if (!_loadMoreListLiveData.value!! && (totalItemCount < totalNews)) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                _loadMoreListLiveData.value = true
            }
        }


    }
}