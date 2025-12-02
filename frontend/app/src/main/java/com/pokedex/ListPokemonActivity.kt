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
import com.pokedex.databinding.ActivityListPokemonBinding
import com.pokedex.viewmodel.PokemonViewModel
import com.pokedex.viewmodel.ViewModelFactory

class ListPokemonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListPokemonBinding
    private lateinit var viewModel: PokemonViewModel
    private lateinit var pokemonAdapter: PokemonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListPokemonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Lista de Pokémons"

        val repository = PokemonRepository(RetrofitClient.instance)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(PokemonViewModel::class.java)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()
        viewModel.listPokemons()
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
            layoutManager = LinearLayoutManager(this@ListPokemonActivity)
        }
    }

    private fun setupListeners() {
        binding.fabAddPokemon.setOnClickListener {
            startActivity(Intent(this, CreatePokemonActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.pokemonList.observe(this) { result ->
            if (result.success && result.data != null) {
                pokemonAdapter.submitList(result.data)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}