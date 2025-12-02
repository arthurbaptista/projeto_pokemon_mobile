package com.pokedex

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.pokedex.data.api.RetrofitClient
import com.pokedex.data.model.LoginRequest
import com.pokedex.data.repository.PokemonRepository
import com.pokedex.databinding.ActivityLoginBinding
import com.pokedex.viewmodel.LoginViewModel
import com.pokedex.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = PokemonRepository(RetrofitClient.instance)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.buttonLogin.setOnClickListener {
            val username = binding.editTextUsername.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(LoginRequest(username, password))
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun observeViewModel() {
        // Observa o resultado do Login
        viewModel.loginResult.observe(this) { result ->
            if (result.token != null) {
                RetrofitClient.authToken = result.token

                saveUserSession(result.token, result.usuario ?: "")
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, result.erro ?: "Falha no login", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun saveUserSession(token: String, login: String) {
        val sharedPref = getSharedPreferences("PokedexPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("USER_TOKEN", token)
            putString("USER_LOGIN", login)
            apply()
        }
    }
}
