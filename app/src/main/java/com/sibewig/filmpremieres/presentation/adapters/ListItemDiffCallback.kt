package com.sibewig.filmpremieres.presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.sibewig.filmpremieres.presentation.MovieListItem

object ListItemDiffCallback : DiffUtil.ItemCallback<MovieListItem>() {

    override fun areItemsTheSame(oldItem: MovieListItem, newItem: MovieListItem): Boolean {
        return if (oldItem is MovieListItem.Header && newItem is MovieListItem.Header) {
            oldItem.month == newItem.month
        } else if (oldItem is MovieListItem.MovieItem && newItem is MovieListItem.MovieItem) {
            oldItem.movie.id == newItem.movie.id
        } else false
    }

    override fun areContentsTheSame(oldItem: MovieListItem, newItem: MovieListItem): Boolean {
        return oldItem == newItem
    }
}