package com.pokedex

import android.content.Context
import android.os.Bundle
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

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Cadastrar Pokémon"


        val repository = PokemonRepository(RetrofitClient.instance)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(PokemonViewModel::class.java)

        setupListeners()
        observeViewModel()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupListeners() {
        binding.buttonCreate.setOnClickListener {
            val name = binding.editTextPokemonName.text.toString().trim()
            val type = binding.editTextPokemonType.text.toString().trim()
            val abilitiesText = binding.editTextPokemonAbilities.text.toString().trim()
            val abilities = abilitiesText
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            val userLogin = getUserLogin()

            // Comprehensive validation
            when {
                name.isEmpty() && type.isEmpty() && abilitiesText.isEmpty() -> {
                    binding.inputPokemonName.error = "Campo obrigatório"
                    binding.inputPokemonType.error = "Campo obrigatório"
                    binding.inputPokemonAbilities.error = "Campo obrigatório"
                    Toast.makeText(this, "⚠️ Preencha todos os campos", Toast.LENGTH_SHORT).show()
                }
                name.isEmpty() -> {
                    binding.inputPokemonName.error = "Campo obrigatório"
                    Toast.makeText(this, "⚠️ Digite o nome do Pokémon", Toast.LENGTH_SHORT).show()
                }
                name.length < 2 -> {
                    binding.inputPokemonName.error = "Nome muito curto"
                    Toast.makeText(this, "⚠️ Nome deve ter pelo menos 2 caracteres", Toast.LENGTH_SHORT).show()
                }
                type.isEmpty() -> {
                    binding.inputPokemonType.error = "Campo obrigatório"
                    Toast.makeText(this, "⚠️ Digite o tipo do Pokémon", Toast.LENGTH_SHORT).show()
                }
                abilitiesText.isEmpty() -> {
                    binding.inputPokemonAbilities.error = "Campo obrigatório"
                    Toast.makeText(this, "⚠️ Digite pelo menos uma habilidade", Toast.LENGTH_SHORT).show()
                }
                abilities.isEmpty() -> {
                    binding.inputPokemonAbilities.error = "Formato inválido"
                    Toast.makeText(this, "⚠️ Digite habilidades válidas separadas por vírgula", Toast.LENGTH_SHORT).show()
                }
                userLogin == null -> {
                    Toast.makeText(this, "❌ Erro: Usuário não identificado", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Clear all errors
                    binding.inputPokemonName.error = null
                    binding.inputPokemonType.error = null
                    binding.inputPokemonAbilities.error = null
                    
                    val request = PokemonRequest(name, type, abilities, userLogin)
                    viewModel.createPokemon(request)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.operationResult.observe(this) { result ->
            if (result.success) {
                Toast.makeText(this, "✅ ${getString(R.string.create_success_message)}", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "❌ ${result.message}", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
            binding.buttonCreate.isEnabled = !isLoading
        }
    }

    private fun getUserLogin(): String? {
        val sharedPref = getSharedPreferences("PokedexPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("USER_LOGIN", null)
    }
}
