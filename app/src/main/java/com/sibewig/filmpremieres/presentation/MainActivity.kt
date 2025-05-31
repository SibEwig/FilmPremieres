package com.sibewig.filmpremieres.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.sibewig.filmpremieres.databinding.ActivityMainBinding
import com.sibewig.filmpremieres.domain.MainActivityState
import com.sibewig.filmpremieres.presentation.adapters.MovieListItemAdapter
import com.sibewig.filmpremieres.presentation.adapters.MovieListItemAdapter.Companion.VIEW_TYPE_HEADER
import com.sibewig.filmpremieres.presentation.adapters.MovieListItemAdapter.Companion.VIEW_TYPE_MOVIE
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val component by lazy {
        (application as FilmPremieresApp).component
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private val adapter = MovieListItemAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setUpRecyclerView()
        binding.floatingButtonSync.setOnClickListener {
            viewModel.loadData()
            binding.floatingButtonSync.visibility = View.GONE
        }
        observeViewModelState()
    }

    private fun observeViewModelState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    when (it) {
                        is MainActivityState.Loading -> {
                            binding.progressBar.isVisible = true
                        }

                        is MainActivityState.Content -> {
                            binding.progressBar.isVisible = false
                            adapter.submitList(it.content)
                        }

                        is MainActivityState.Error -> {
                            binding.floatingButtonSync.visibility = View.VISIBLE
                            binding.progressBar.isVisible = false
                            Toast.makeText(
                                this@MainActivity,
                                it.error,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        val layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewMovie.adapter = adapter
        binding.recyclerViewMovie.layoutManager = layoutManager
        adapter.onReachEndListener = {
            viewModel.loadData()
        }
        adapter.onItemClickListener = {
            MovieDetailActivity.newIntent(this@MainActivity, it).also {intent ->
                startActivity(intent)
            }
        }
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    VIEW_TYPE_HEADER -> 2
                    VIEW_TYPE_MOVIE -> 1
                    else -> 1
                }
            }
        }
    }

    companion object {

        private const val TAG = "MainActivity"
    }

}