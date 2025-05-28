package com.sibewig.filmpremieres.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.sibewig.filmpremieres.databinding.TrailerItemBinding
import com.sibewig.filmpremieres.domain.Trailer

class TrailerAdapter: ListAdapter<Trailer, TrailerViewHolder>(TrailerDiffCallback) {

    var onTrailerClickListener: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailerViewHolder {
        val binding = TrailerItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrailerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrailerViewHolder, position: Int) {
        val trailer = currentList[position]
        with(holder) {
            binding.textViewTrailer.text = trailer.name
            binding.imageView.setOnClickListener {
                onTrailerClickListener?.invoke(trailer.url)
            }
        }
    }
}