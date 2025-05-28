package com.sibewig.filmpremieres.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sibewig.filmpremieres.R
import com.sibewig.filmpremieres.databinding.ActivityMovieDetailBinding
import com.sibewig.filmpremieres.domain.Movie
import com.sibewig.filmpremieres.presentation.adapters.TrailerAdapter
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class MovieDetailActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val component by lazy {
        (application as FilmPremieresApp).component
    }

    private val binding by lazy {
        ActivityMovieDetailBinding.inflate(layoutInflater)
    }

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MovieDetailViewModel::class.java]
    }

    private val premiereDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yy")

    private val adapter = TrailerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val movieId = parseIntent()
        setUpRecyclerView()
        collectMovieInfo()
        viewModel.loadMovieInfo(movieId)
        adapter.onTrailerClickListener = {
            Intent(Intent.ACTION_VIEW).apply {
                setData(Uri.parse(it))
            }.also {
                startActivity(it)
            }
        }
    }

    private fun parseIntent() = intent.getIntExtra(EXTRA_MOVIE_ID, 0)

    private fun collectMovieInfo() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movieInfo.collect {
                    Log.d("MovieDetailActivity", "Trailers: ${it.trailers.toString()}")
                    bindMovieInfo(it)
                    adapter.submitList(it.trailers)
                }
            }
        }
    }

    private fun bindMovieInfo(movie: Movie) {
        with(movie) {
            with(binding) {
                if (poster != null) {
                    Glide.with(imageViewPoster)
                        .load(poster)
                        .into(imageViewPoster)
                } else {
                    ContextCompat.getDrawable(this@MovieDetailActivity, R.drawable.placeholder_kp)
                        .also {
                            imageViewPoster.setImageDrawable(it)
                        }
                }
                textViewMovieTitle.text = name
                textViewDescription.text = description
                premiere.format(premiereDateFormatter).also {
                    textViewYear.text = getString(R.string.premiere_date, it)
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        with(binding) {
            recyclerViewTrailer.adapter = adapter
            recyclerViewTrailer.layoutManager = LinearLayoutManager(this@MovieDetailActivity)
        }
    }

    companion object {

        private const val EXTRA_MOVIE_ID = "id"

        fun newIntent(context: Context, id: Int): Intent {
            return Intent(context, MovieDetailActivity::class.java).apply {
                putExtra(EXTRA_MOVIE_ID, id)
            }
        }
    }
}