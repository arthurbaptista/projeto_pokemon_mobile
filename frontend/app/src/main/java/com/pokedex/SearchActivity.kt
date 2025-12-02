package com.pokedex

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pokedex.adapter.PokemonAdapter
import com.pokedex.data.api.RetrofitClient
import com.pokedex.data.repository.PokemonRepository
import com.pokedex.databinding.ActivitySearchBinding
import com.pokedex.viewmodel.SearchViewModel
import com.pokedex.viewmodel.ViewModelFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var pokemonAdapter: PokemonAdapter
    private var searchMode: String = "TYPE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchMode = intent.getStringExtra("SEARCH_MODE") ?: "TYPE"
        title = if (searchMode == "TYPE") getString(R.string.search_type_title) else getString(R.string.search_ability_title)

        val repository = PokemonRepository(RetrofitClient.instance)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(SearchViewModel::class.java)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        pokemonAdapter = PokemonAdapter { pokemon ->
            val intent = Intent(this, DetailPokemonActivity::class.java).apply {
                putExtra("POKEMON_ID", pokemon.id)
            }
            startActivity(intent)
        }
        binding.recyclerView.apply {
            adapter = pokemonAdapter
            layoutManager = LinearLayoutManager(this@SearchActivity)
        }
    }

    private fun setupListeners() {
        binding.buttonSearch.setOnClickListener {
            val query = binding.editTextSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                viewModel.searchPokemons(query, searchMode)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.searchResult.observe(this) { result ->
            if (result.success && result.data != null) {
                pokemonAdapter.submitList(result.data)
            } else {
                // Tratar erro
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}
