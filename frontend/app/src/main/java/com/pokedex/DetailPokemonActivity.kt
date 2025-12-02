package com.pokedex

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.pokedex.data.api.RetrofitClient
import com.pokedex.data.model.PokemonUpdateRequest
import com.pokedex.data.repository.PokemonRepository
import com.pokedex.databinding.ActivityDetailPokemonBinding
import com.pokedex.viewmodel.PokemonViewModel
import com.pokedex.viewmodel.ViewModelFactory

class DetailPokemonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPokemonBinding
    private lateinit var viewModel: PokemonViewModel
    private var pokemonId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPokemonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detalhes do Pokémon"

        pokemonId = intent.getIntExtra("POKEMON_ID", -1)
        if (pokemonId == -1) {
            Toast.makeText(this, "❌ Erro: ID do Pokémon inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val repository = PokemonRepository(RetrofitClient.instance)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(PokemonViewModel::class.java)

        setupListeners()
        observeViewModel()

        viewModel.getPokemonDetails(pokemonId)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupListeners() {
        binding.buttonUpdate.setOnClickListener {
            val name = binding.editTextPokemonName.text.toString().trim()
            val type = binding.editTextPokemonType.text.toString().trim()
            val abilitiesText = binding.editTextPokemonAbilities.text.toString().trim()
            val abilities = abilitiesText
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

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
                else -> {
                    // Clear all errors
                    binding.inputPokemonName.error = null
                    binding.inputPokemonType.error = null
                    binding.inputPokemonAbilities.error = null
                    
                    val request = PokemonUpdateRequest(name, type, abilities)
                    viewModel.updatePokemon(pokemonId, request)
                }
            }
        }

        binding.buttonDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.pokemonDetails.observe(this) { result ->
            if (result.success && result.data != null) {
                val pokemon = result.data
                binding.editTextPokemonName.setText(pokemon.nome)
                binding.editTextPokemonType.setText(pokemon.tipo)
                binding.editTextPokemonAbilities.setText(pokemon.habilidades.joinToString(", "))
            } else {
                Toast.makeText(this, "❌ ${result.message}", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.operationResult.observe(this) { result ->
            if (result.success) {
                val message = when {
                    result.message.contains("atualizado", ignoreCase = true) -> "✅ ${getString(R.string.update_success_message)}"
                    result.message.contains("excluído", ignoreCase = true) -> "✅ ${getString(R.string.delete_success_message)}"
                    else -> "✅ ${result.message}"
                }
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "❌ ${result.message}", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.buttonUpdate.isEnabled = !isLoading
            binding.buttonDelete.isEnabled = !isLoading
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_confirm_title))
            .setMessage(getString(R.string.delete_confirm_message))
            .setPositiveButton(getString(R.string.dialog_yes)) { _, _ ->
                viewModel.deletePokemon(pokemonId)
            }
            .setNegativeButton(getString(R.string.dialog_no), null)
            .show()
    }
}
