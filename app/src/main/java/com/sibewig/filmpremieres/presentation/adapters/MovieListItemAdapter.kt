package com.sibewig.filmpremieres.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sibewig.filmpremieres.databinding.HeaderItemBinding
import com.sibewig.filmpremieres.databinding.MovieItemBinding
import com.sibewig.filmpremieres.domain.MovieListItem

class MovieListItemAdapter : ListAdapter<MovieListItem, RecyclerView.ViewHolder>(MovieListItemDiffCallback) {

    var onReachEndListener: (() -> Unit)? = null
    var onItemClickListener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding =
                    HeaderItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                HeaderViewHolder(binding)
            }

            VIEW_TYPE_MOVIE -> {
                val binding =
                    MovieItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                MovieViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is MovieListItem.Header -> (holder as HeaderViewHolder).bind(item)
            is MovieListItem.MovieItem -> {
                (holder as MovieViewHolder).bind(item.movie)
                holder.itemView.setOnClickListener {
                    onItemClickListener?.invoke(item.movie.id)
                }
            }
        }
        if (position >= currentList.size - 10) {
            onReachEndListener?.invoke()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MovieListItem.Header -> VIEW_TYPE_HEADER
            is MovieListItem.MovieItem -> VIEW_TYPE_MOVIE
        }
    }

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_MOVIE = 1
    }

}