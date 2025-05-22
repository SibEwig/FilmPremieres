package com.sibewig.filmpremieres.presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.sibewig.filmpremieres.domain.Movie

object MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem == newItem
    }
}