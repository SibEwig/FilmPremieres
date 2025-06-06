package com.sibewig.filmpremieres.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.sibewig.filmpremieres.R
import com.sibewig.filmpremieres.databinding.ActivityMainBinding
import com.sibewig.filmpremieres.domain.MainActivityState
import com.sibewig.filmpremieres.presentation.adapters.MovieListItemAdapter
import com.sibewig.filmpremieres.presentation.adapters.MovieListItemAdapter.Companion.VIEW_TYPE_HEADER
import com.sibewig.filmpremieres.presentation.adapters.MovieListItemAdapter.Companion.VIEW_TYPE_MOVIE
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val component by lazy {
        (application as FilmPremieresApp).component
    }

    private var isFavouriteMode = false

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private val adapter = MovieListItemAdapter()

    private var isMenuOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setUpRecyclerView()
        setUpClickListeners()
        observeViewModelState()
    }

    private fun toggleFabMenu() {
        isMenuOpen = !isMenuOpen
        val visibility = if (isMenuOpen) View.VISIBLE else View.GONE
        binding.fabSearch.visibility = visibility
        binding.fabFavourites.visibility = visibility
    }

    private fun setUpClickListeners() {
        with(binding) {
            fabSearch.setOnClickListener {
                viewModel.loadData()
                fabSearch.visibility = View.GONE
            }
            fabFavourites.setOnClickListener {
                isFavouriteMode = !isFavouriteMode
                viewModel.setFavouriteMode(isFavouriteMode)
                if (isFavouriteMode) {
                    viewModel.getFavouriteList()
                } else {
                    viewModel.loadData()
                }
            }
            fabMain.setOnClickListener {
                toggleFabMenu()
            }
            fabSearch.setOnClickListener {
                searchView.visibility = View.VISIBLE
                searchView.onActionViewExpanded()
                searchView.postDelayed({
                    val searchEditText = searchView.findViewById<EditText>(
                        androidx.appcompat.R.id.search_src_text
                    )
                    searchEditText.requestFocus()
                    searchEditText.setSelection(searchEditText.text.length)
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
                }, 150)
            }
            searchView.setOnQueryTextListener(object : OnQueryTextListener {

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query == null) {
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.empty_search_query),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.searchMovie(query)
                    }
                    return true
                }
            })
        }
    }

    private fun observeViewModelState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect {
                        when (it) {
                            is MainActivityState.Loading -> {
                                binding.progressBar.isVisible = true
                            }

                            is MainActivityState.Content -> {
                                adapter.submitList(it.content)
                                delay(500)
                                binding.progressBar.isVisible = false
                            }

                            is MainActivityState.Error -> {
                                binding.fabRetry.visibility = View.VISIBLE
                                Toast.makeText(
                                    this@MainActivity,
                                    it.error,
                                    Toast.LENGTH_SHORT
                                ).show()
                                delay(500)
                                binding.progressBar.isVisible = false
                            }
                        }
                    }
                }
                launch {
                    viewModel.fullListLoaded.collect {fullListLoaded ->
                        if (fullListLoaded) {
                            adapter.onReachEndListener = null
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

    override fun onResume() {
        super.onResume()
        if (isFavouriteMode) {
            viewModel.getFavouriteList()
        }
    }

    companion object {

        private const val TAG = "MainActivity"
    }

}