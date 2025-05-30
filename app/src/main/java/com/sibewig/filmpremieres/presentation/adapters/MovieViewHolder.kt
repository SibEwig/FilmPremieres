package com.sibewig.filmpremieres.presentation.adapters

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sibewig.filmpremieres.R
import com.sibewig.filmpremieres.databinding.MovieItemBinding
import com.sibewig.filmpremieres.domain.Movie
import java.time.format.DateTimeFormatter

class MovieViewHolder(private val binding: MovieItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(movie: Movie) {
        with(binding) {
            if (movie.poster != null) {
                Glide.with(imageViewPoster)
                    .load(movie.poster)
                    .into(imageViewPoster)
            } else {
                ContextCompat.getDrawable(itemView.context, R.drawable.placeholder_kp)
                    .also {
                        imageViewPoster.setImageDrawable(it)
                    }
            }
            textViewTitle.text = movie.name
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yy")
            val formattedDate = movie.premiere.format(formatter)
            textViewPremiere.text =
                itemView.context.getString(R.string.premiere_date, formattedDate)
        }
    }
}