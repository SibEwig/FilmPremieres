package com.sibewig.filmpremieres.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.sibewig.filmpremieres.domain.ScreenMode
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
        setUpUiListeners()
        observeViewModelState()
    }

    private fun toggleFabMenu() {
        isMenuOpen = !isMenuOpen
        val visibility = if (isMenuOpen) View.VISIBLE else View.GONE
        binding.fabSearch.visibility = visibility
        binding.fabFavourites.visibility = visibility
    }

    private fun setUpUiListeners() {
        with(binding) {
            fabFavourites.setOnClickListener {
                viewModel.setScreenMode(ScreenMode.FAVOURITES)
            }
            fabMain.setOnClickListener {
                toggleFabMenu()
            }
            fabSearch.setOnClickListener {
                toggleFabMenu()
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
            fabBack.setOnClickListener {
                viewModel.setScreenMode(ScreenMode.MAIN)
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
                        viewModel.setScreenMode(ScreenMode.SEARCH, query)
                    }
                    return true
                }
            })
        }
    }

    private fun updateFabVisibility(mode: ScreenMode) {
        with(binding) {
            when (mode) {
                ScreenMode.MAIN -> {
                    fabMain.visibility = View.VISIBLE
                    fabBack.visibility = View.GONE
                    searchView.visibility = View.GONE

                }

                else -> {
                    fabMain.visibility = View.GONE
                    fabFavourites.visibility = View.GONE
                    fabSearch.visibility = View.GONE
                    fabBack.visibility = View.VISIBLE
                    isMenuOpen = false
                }
            }
        }
    }

    private fun observeViewModelState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect {
                        Log.d(TAG, "Collected: $it")
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
                    viewModel.screenMode.collect { mode ->
                        when (mode) {
                            ScreenMode.MAIN -> {
                                adapter.onReachEndListener = {
                                    viewModel.loadNextPage()
                                }
                                delay(500)
                                updateFabVisibility(mode)
                            }

                            ScreenMode.FAVOURITES, ScreenMode.SEARCH -> {
                                adapter.onReachEndListener = null
                                delay(500)
                                updateFabVisibility(mode)

                            }
                        }
                    }
                }
                launch {
                    viewModel.fullListLoaded.collect { fullListLoaded ->
                        if (fullListLoaded) {
                            adapter.onReachEndListener = null
                            Log.d(TAG, "onReachEndListener was set as null")
                        }
                    }
                }
            }
        }
    }

    private fun setOnReachEndListener(mode: ScreenMode) {
        when(mode) {
            ScreenMode.MAIN -> {
                adapter.onReachEndListener = {
                    viewModel.loadNextPage()
                }
            }
            else -> adapter.onReachEndListener = null
        }
    }

    private fun setUpRecyclerView() {
        val layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewMovie.adapter = adapter
        binding.recyclerViewMovie.layoutManager = layoutManager
        adapter.onItemClickListener = {
            MovieDetailActivity.newIntent(this@MainActivity, it).also { intent ->
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