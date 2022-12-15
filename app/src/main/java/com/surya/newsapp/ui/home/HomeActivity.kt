package com.surya.newsapp.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.surya.newsapp.R
import com.surya.newsapp.databinding.ActivityHomeBinding
import com.surya.newsapp.ui.adapter.NewsAdapter
import com.surya.newsapp.util.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class HomeActivity : AppCompatActivity(), KodeinAware {

    companion object {
        const val ANIMATION_DURATION = 1000.toLong()
    }

    override val kodein by kodein()
    private lateinit var dataBind: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    private lateinit var newsAdapter: NewsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBind = DataBindingUtil.setContentView(this, R.layout.activity_home)
        setupViewModel()
        setupUI()
        initializeObserver()
        handleNetworkChanges()
        setupAPICall()
    }


    private fun setupUI() {
        newsAdapter = NewsAdapter()
        dataBind.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = newsAdapter
            addOnItemTouchListener(
                RecyclerItemClickListener(
                    applicationContext,
                    object : RecyclerItemClickListener.OnItemClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            if (newsAdapter.getData().isNotEmpty()) {
                                val searchItem = newsAdapter.getData()[position]
                                searchItem?.let {
                                    // Click Action
                                }

                            }
                        }

                    })
            )
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    val visibleItemCount = layoutManager!!.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    viewModel.checkForLoadMoreItems(
                        visibleItemCount,
                        totalItemCount,
                        firstVisibleItemPosition
                    )
                }

            })
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

    }

    private fun initializeObserver() {
        viewModel.newsNameLiveData.observe(this) {
        }
        viewModel.loadMoreListLiveData.observe(this) {
            if (it) {
                newsAdapter.setData(null)
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.loadMore()
                }, 1500)
            }
        }
    }

    private fun setupAPICall() {
        viewModel.getNews()
        viewModel.newsLiveData.observe(this) { state ->
            when (state) {
                is State.Loading -> {
                    dataBind.recyclerView.hide()
                    dataBind.linearLayoutSearch.hide()
                    dataBind.progressBar.show()
                }
                is State.Success -> {
                    dataBind.recyclerView.show()
                    dataBind.linearLayoutSearch.hide()
                    dataBind.progressBar.hide()
                    newsAdapter.setData(state.data)
                }
                is State.Error -> {
                    dataBind.progressBar.hide()
                    showToast(state.message)
                }

                else -> {}
            }
        }

    }

    private fun handleNetworkChanges() {
        NetworkUtils.getNetworkLiveData(applicationContext).observe(this) { isConnected ->
            if (!isConnected) {
                dataBind.textViewNetworkStatus.text = getString(R.string.text_no_connectivity)
                dataBind.networkStatusLayout.apply {
                    show()
                    setBackgroundColor(getColorRes(R.color.colorStatusNotConnected))
                }
            } else {
                if (viewModel.newsLiveData.value is State.Error || newsAdapter.itemCount == 0) {
                    viewModel.getNews()
                }
                dataBind.textViewNetworkStatus.text = getString(R.string.text_connectivity)
                dataBind.networkStatusLayout.apply {
                    setBackgroundColor(getColorRes(R.color.colorStatusConnected))

                    animate()
                        .alpha(1f)
                        .setStartDelay(ANIMATION_DURATION)
                        .setDuration(ANIMATION_DURATION)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                hide()
                            }
                        })
                }
            }
        }
    }
    

}