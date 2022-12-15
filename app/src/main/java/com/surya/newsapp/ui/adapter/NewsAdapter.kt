package com.surya.newsapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.surya.newsapp.R
import com.surya.newsapp.data.model.Article
import com.surya.newsapp.util.TimeStamp.Companion.getMilliToDate
import com.surya.newsapp.util.show

class NewsAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }

    private var newsList = ArrayList<Article?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == VIEW_TYPE_ITEM) {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_news, parent, false)
            MovieViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_lazy_loading, parent, false)
            LoadingViewHolder(view)
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is MovieViewHolder) {
            if (newsList[position] != null) {
                holder.bindItems(newsList[position]!!)
            }
        } else if (holder is LoadingViewHolder) {
            holder.showLoadingView()
        }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (newsList[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    fun setData(listOfNews: ArrayList<Article?>?) {
        if (listOfNews != null) {
            if (newsList.isNotEmpty())
                newsList.removeAt(newsList.size - 1)
            newsList.clear()
            newsList.addAll(listOfNews)
        } else {
            newsList.add(listOfNews)
        }
        notifyDataSetChanged()
    }

    fun getData() = newsList

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imagePoster: ImageView = itemView.findViewById(R.id.ivArticleImage)
        private val textTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val textDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val textSource: TextView = itemView.findViewById(R.id.tvSource)
        private val textPublishedAt: TextView = itemView.findViewById(R.id.tvPublishedAt)

        @SuppressLint("SetTextI18n")
        fun bindItems(news: Article) {
            textTitle.text = news.title
            textDescription.text = news.description
            textSource.text = news.source!!.name
            textPublishedAt.text = getMilliToDate(news.publishedAt)
            Glide.with(imagePoster.context).load(news.urlToImage)
                .placeholder(R.drawable.ic_launcher_foreground)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imagePoster)
        }

    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)

        fun showLoadingView() {
            progressBar.show()
        }
    }

}