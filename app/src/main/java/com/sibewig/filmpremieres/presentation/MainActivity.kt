package com.sibewig.filmpremieres.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.sibewig.filmpremieres.databinding.ActivityMainBinding
import com.sibewig.filmpremieres.presentation.adapters.MovieAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    private val scope = CoroutineScope(Dispatchers.IO)

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
        scope.launch {
            viewModel.movieListFlow.collect {
                adapter.submitList(it)
                Log.d(TAG, it.joinToString())
            }
        }
    }

    companion object {

        private const val TAG = "MainActivity"
    }

}