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

            // Enhanced validation
            when {
                username.isEmpty() && password.isEmpty() -> {
                    binding.inputUsername.error = "Campo obrigatório"
                    binding.inputPassword.error = "Campo obrigatório"
                    Toast.makeText(this, "⚠️ Preencha todos os campos", Toast.LENGTH_SHORT).show()
                }
                username.isEmpty() -> {
                    binding.inputUsername.error = "Campo obrigatório"
                    Toast.makeText(this, "⚠️ Digite seu usuário", Toast.LENGTH_SHORT).show()
                }
                password.isEmpty() -> {
                    binding.inputPassword.error = "Campo obrigatório"
                    Toast.makeText(this, "⚠️ Digite sua senha", Toast.LENGTH_SHORT).show()
                }
                password.length < 3 -> {
                    binding.inputPassword.error = "Senha muito curta"
                    Toast.makeText(this, "⚠️ Senha deve ter pelo menos 3 caracteres", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Clear errors
                    binding.inputUsername.error = null
                    binding.inputPassword.error = null
                    viewModel.login(LoginRequest(username, password))
                }
            }
        }

        binding.buttonRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.buttonCloseApp.setOnClickListener {
            finishAffinity() // Fecha todas as activities e o app
        }
    }

    private fun observeViewModel() {
        // Observa o resultado do Login
        viewModel.loginResult.observe(this) { result ->
            if (result.token != null) {
                RetrofitClient.authToken = result.token

                saveUserSession(result.token, result.usuario ?: "")
                Toast.makeText(this, "✅ Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "❌ ${result.erro ?: "Falha no login"}", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.buttonLogin.isEnabled = !isLoading
            binding.buttonRegister.isEnabled = !isLoading
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
