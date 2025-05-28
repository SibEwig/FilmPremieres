package com.sibewig.filmpremieres.presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.sibewig.filmpremieres.domain.Trailer

object TrailerDiffCallback: DiffUtil.ItemCallback<Trailer>() {

    override fun areItemsTheSame(oldItem: Trailer, newItem: Trailer): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Trailer, newItem: Trailer): Boolean {
        return oldItem == newItem
    }
}