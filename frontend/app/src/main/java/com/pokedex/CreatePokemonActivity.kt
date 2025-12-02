package com.pokedex

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.pokedex.data.api.RetrofitClient
import com.pokedex.data.model.PokemonRequest
import com.pokedex.data.repository.PokemonRepository
import com.pokedex.databinding.ActivityCreatePokemonBinding
import com.pokedex.viewmodel.PokemonViewModel
import com.pokedex.viewmodel.ViewModelFactory

class CreatePokemonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePokemonBinding
    private lateinit var viewModel: PokemonViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePokemonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Seta de voltar na toolbar própria da tela
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        val repository = PokemonRepository(RetrofitClient.instance)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(PokemonViewModel::class.java)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.buttonCreate.setOnClickListener {
            val name = binding.editTextPokemonName.text.toString().trim()
            val type = binding.editTextPokemonType.text.toString().trim()
            val abilities = binding.editTextPokemonAbilities.text
                .toString()
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            val userLogin = getUserLogin()

            if (name.isNotEmpty() && type.isNotEmpty() && abilities.isNotEmpty() && userLogin != null) {
                val request = PokemonRequest(name, type, abilities, userLogin)
                viewModel.createPokemon(request)
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.operationResult.observe(this) { result ->
            if (result.success) {
                Toast.makeText(this, getString(R.string.create_success_message), Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun getUserLogin(): String? {
        val sharedPref = getSharedPreferences("PokedexPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("USER_LOGIN", null)
    }
}
