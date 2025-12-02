package com.pokedex

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.pokedex.data.api.RetrofitClient
import com.pokedex.data.model.UserRegistrationRequest
import com.pokedex.data.repository.PokemonRepository
import com.pokedex.databinding.ActivityRegisterBinding
import com.pokedex.viewmodel.LoginViewModel
import com.pokedex.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = PokemonRepository(RetrofitClient.instance)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.buttonRegister.setOnClickListener {
            val username = binding.editTextUsername.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                viewModel.register(UserRegistrationRequest(username, password))
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {

        viewModel.loginResult.observe(this) { result ->

            if (result.token != null || result.id != null) {
                Toast.makeText(this, getString(R.string.registration_success), Toast.LENGTH_LONG).show()
                finish() // Fecha a tela e volta para o Login
            } else {
                val msg = result.erro ?: getString(R.string.registration_fail)
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        }
    }
}