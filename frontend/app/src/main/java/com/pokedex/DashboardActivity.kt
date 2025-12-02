package com.pokedex

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.pokedex.data.api.RetrofitClient
import com.pokedex.data.repository.PokemonRepository
import com.pokedex.databinding.ActivityDashboardBinding
import com.pokedex.viewmodel.DashboardViewModel
import com.pokedex.viewmodel.ViewModelFactory

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var viewModel: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = PokemonRepository(RetrofitClient.instance)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(DashboardViewModel::class.java)

        setupListeners()
        observeViewModel()

        viewModel.fetchDashboardData()
    }

    private fun setupListeners() {
        binding.buttonCreatePokemon.setOnClickListener { 
            startActivity(Intent(this, CreatePokemonActivity::class.java))
        }
        binding.buttonListAll.setOnClickListener { 
            startActivity(Intent(this, ListPokemonActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.dashboardData.observe(this) { data ->
            data?.let {
                binding.textViewPokemonCount.text = getString(R.string.dashboard_pokemon_count, it.totalPokemon)
                binding.textViewTopTypes.text = getString(R.string.dashboard_top_types, it.top3Tipos.joinToString(", "))
                binding.textViewTopAbilities.text = getString(R.string.dashboard_top_abilities, it.top3Habilidades.joinToString(", "))
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search_type -> {
                startActivity(Intent(this, SearchActivity::class.java).apply { putExtra("SEARCH_MODE", "TYPE") })
                true
            }
            R.id.action_search_ability -> {
                startActivity(Intent(this, SearchActivity::class.java).apply { putExtra("SEARCH_MODE", "ABILITY") })
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        val sharedPref = getSharedPreferences("PokedexPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("USER_TOKEN")
            remove("USER_LOGIN")
            apply()
        }
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
