package com.sibewig.filmpremieres.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.sibewig.filmpremieres.databinding.ActivityMainBinding
import com.sibewig.filmpremieres.presentation.adapters.MovieAdapter
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

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        val adapter = MovieAdapter()
        binding.recyclerViewMovie.adapter = adapter
        binding.recyclerViewMovie.layoutManager = GridLayoutManager(this, 2)
        adapter.onReachEndListener = {
            viewModel.loadData()
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movieListFlow.collect {
                    adapter.submitList(it)
                }
            }
        }
    }

    companion object {

        private const val TAG = "MainActivity"
    }

}