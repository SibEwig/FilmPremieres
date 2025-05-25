package com.sibewig.filmpremieres.presentation.adapters

import androidx.recyclerview.widget.RecyclerView
import com.sibewig.filmpremieres.databinding.HeaderItemBinding
import com.sibewig.filmpremieres.presentation.MovieListItem

class HeaderViewHolder(private val binding: HeaderItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(header: MovieListItem.Header) {
        binding.textViewHeader.text = header.month
    }
}