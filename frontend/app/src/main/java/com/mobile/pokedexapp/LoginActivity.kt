package com.mobile.pokedexapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val etLogin = findViewById<EditText>(R.id.etLogin)
        val etSenha = findViewById<EditText>(R.id.etSenha)

        btnLogin.setOnClickListener {
            val login = etLogin.text.toString()
            val senha = etSenha.text.toString()

            // Chama a API
            val call = RetrofitClient.instance.login(LoginRequest(login, senha))
            call.enqueue(object : retrofit2.Callback<Map<String, Boolean>> {
                override fun onResponse(call: Call<Map<String, Boolean>>, response: retrofit2.Response<Map<String, Boolean>>) {
                    // Lógica: Se backend retornar sucesso = true
                    if (response.isSuccessful) {
                        // Salva o usuário para usar no cadastro depois
                        getSharedPreferences("PREFS", MODE_PRIVATE).edit().putString("USER", login).apply()
                        startActivity(android.content.Intent(this@LoginActivity, DashboardActivity::class.java))
                        finish()
                    } else {
                        alerta("Login ou Senha incorretos") [cite: 14]
                    }
                }
                override fun onFailure(call: Call<Map<String, Boolean>>, t: Throwable) {
                    alerta("Erro: " + t.message)
                }
            })
        }
    }

    fun alerta(msg: String) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setMessage(msg)
            .setPositiveButton("OK", null)
            .show()
    }
}