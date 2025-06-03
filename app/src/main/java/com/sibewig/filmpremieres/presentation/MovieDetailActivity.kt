package com.sibewig.filmpremieres.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sibewig.filmpremieres.R
import com.sibewig.filmpremieres.databinding.ActivityMovieDetailBinding
import com.sibewig.filmpremieres.domain.Movie
import com.sibewig.filmpremieres.domain.MovieDetailActivityState
import com.sibewig.filmpremieres.presentation.adapters.TrailerAdapter
import kotlinx.coroutines.delay
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

    private var currentMovie: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val movieId = parseIntent()
        val adapter = TrailerAdapter()
        setupTrailerRecyclerView(adapter)
        observeMovieInfo(adapter)
        setupClickListeners(movieId, adapter)
        viewModel.loadMovieInfo(movieId)
        viewModel.checkIsFavourite(movieId)
    }

    private fun setupClickListeners(movieId: Int, adapter: TrailerAdapter) {
        adapter.onTrailerClickListener = {
            Intent(Intent.ACTION_VIEW).apply {
                setData(Uri.parse(it))
            }.also {
                startActivity(it)
            }
        }
        binding.floatingButtonSync.setOnClickListener {
            viewModel.loadMovieInfo(movieId)
        }
        binding.imageViewStar.setOnClickListener {
            currentMovie?.let {
                viewModel.toggleFavourite(it)
            }
        }
    }

    private fun parseIntent() = intent.getIntExtra(EXTRA_MOVIE_ID, 0)

    private fun observeMovieInfo(adapter: TrailerAdapter) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect {
                        Log.d(TAG, "Collected in activity: $it")
                        when (it) {
                            is MovieDetailActivityState.Content -> {
                                currentMovie = it.content
                                bindMovieInfo(it.content)
                                adapter.submitList(it.content.trailers)
                                delay(500)
                                binding.progressBar.isVisible = false
                            }

                            is MovieDetailActivityState.Error -> {
                                binding.floatingButtonSync.visibility = View.VISIBLE
                                Toast.makeText(
                                    this@MovieDetailActivity,
                                    it.error,
                                    Toast.LENGTH_SHORT
                                ).show()
                                delay(500)
                                binding.progressBar.isVisible = false
                            }

                            MovieDetailActivityState.Loading -> {
                                binding.progressBar.isVisible = true
                            }
                        }
                    }
                }
                launch {
                    viewModel.isFavourite.collect { isFavourite ->
                        val drawableRes = if (isFavourite == true) {
                            android.R.drawable.star_big_on
                        } else {
                            android.R.drawable.star_big_off
                        }
                        binding.imageViewStar.setImageDrawable(
                            ContextCompat.getDrawable(this@MovieDetailActivity, drawableRes)
                        )
                    }
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
                val premiereDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yy")
                premiere.format(premiereDateFormatter).also {
                    textViewYear.text = getString(R.string.premiere_date, it)
                }
            }
        }
    }

    private fun setupTrailerRecyclerView(adapter: TrailerAdapter) {
        with(binding) {
            recyclerViewTrailer.adapter = adapter
            recyclerViewTrailer.layoutManager = LinearLayoutManager(this@MovieDetailActivity)
        }
    }

    companion object {

        private const val EXTRA_MOVIE_ID = "id"
        private const val TAG = "MovieDetailActivity"

        fun newIntent(context: Context, id: Int): Intent {
            return Intent(context, MovieDetailActivity::class.java).apply {
                putExtra(EXTRA_MOVIE_ID, id)
            }
        }
    }
}