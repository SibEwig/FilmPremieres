package com.sibewig.filmpremieres.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.sibewig.filmpremieres.R
import com.sibewig.filmpremieres.databinding.MovieItemBinding
import com.sibewig.filmpremieres.domain.Movie

class MovieAdapter: ListAdapter<Movie, MovieViewHolder>(MovieDiffCallback) {

    var onReachEndListener: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = MovieItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = currentList[position]
        with(holder) {
            with(movie) {
                if (movie.poster != null) {
                    Glide.with(binding.imageViewPoster)
                        .load(poster)
                        .into(binding.imageViewPoster)
                } else {
                    ContextCompat.getDrawable(holder.itemView.context, R.drawable.placeholder_kp)
                        .also {
                            binding.imageViewPoster.setImageDrawable(it)
                        }
                }
            }
        }
        if (position >= currentList.size - 10) {
            onReachEndListener?.invoke()
        }
    }

}