package com.pokedex

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
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

    private val initialOptionType = "Selecione um tipo"
    private val initialOptionAbility = "Selecione uma habilidade"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        searchMode = intent.getStringExtra("SEARCH_MODE") ?: "TYPE"
        title = if (searchMode == "TYPE") getString(R.string.search_type_title) else getString(R.string.search_ability_title)

        val repository = PokemonRepository(RetrofitClient.instance)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(SearchViewModel::class.java)

        setupRecyclerView()
        setupListeners()
        observeViewModel()

        // Start fetching data
        viewModel.fetchAllUniqueData()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupSpinner(data: List<String>) {
        val initialOption = if (searchMode == "TYPE") initialOptionType else initialOptionAbility
        val items = listOf(initialOption) + data.sorted()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        binding.spinnerSearch.adapter = adapter

        binding.spinnerSearch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) { // Skip the initial option
                    val selectedItem = items[position]
                    performSearch(selectedItem)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun setupRecyclerView() {
        pokemonAdapter = PokemonAdapter(showType = false) { pokemon ->
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
            val selectedPosition = binding.spinnerSearch.selectedItemPosition
            if (selectedPosition == 0) {
                val message = if (searchMode == "TYPE") {
                    "⚠️ Selecione um tipo para buscar"
                } else {
                    "⚠️ Selecione uma habilidade para buscar"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            } else {
                val selectedItem = binding.spinnerSearch.selectedItem.toString()
                performSearch(selectedItem)
            }
        }
    }

    private fun performSearch(query: String) {
        val initialOption = if (searchMode == "TYPE") initialOptionType else initialOptionAbility
        if (query.isNotEmpty() && query != initialOption) {
            viewModel.searchPokemons(query, searchMode)
        }
    }

    private fun observeViewModel() {
        viewModel.uniqueTypes.observe(this) { types ->
            if (searchMode == "TYPE") {
                setupSpinner(types)
            }
        }

        viewModel.uniqueAbilities.observe(this) { abilities ->
            if (searchMode == "ABILITY") {
                setupSpinner(abilities)
            }
        }

        viewModel.searchResult.observe(this) { listaPokemons ->
            if (!listaPokemons.isNullOrEmpty()) {
                pokemonAdapter.submitList(listaPokemons)
                binding.textEmptyState.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            } else {
                pokemonAdapter.submitList(emptyList())
                binding.textEmptyState.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE

                val selectedPosition = binding.spinnerSearch.selectedItemPosition
                if (selectedPosition > 0) {
                    Toast.makeText(this, "Nenhum Pokémon encontrado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.buttonSearch.isEnabled = !isLoading
            binding.spinnerSearch.isEnabled = !isLoading
        }

        viewModel.error.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(this, "❌ $errorMessage", Toast.LENGTH_LONG).show()
            }
        }
    }
}
