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
import com.sibewig.filmpremieres.presentation.adapters.ListItemAdapter
import com.sibewig.filmpremieres.presentation.adapters.ListItemAdapter.Companion.VIEW_TYPE_HEADER
import com.sibewig.filmpremieres.presentation.adapters.ListItemAdapter.Companion.VIEW_TYPE_MOVIE
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

    private val adapter = ListItemAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setUpAdapter()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movieListFlow.collect {
                    adapter.submitList(it)
                }
            }
        }
    }

    private fun setUpAdapter() {
        val layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewMovie.adapter = adapter
        binding.recyclerViewMovie.layoutManager = layoutManager
        adapter.onReachEndListener = {
            viewModel.loadData()
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