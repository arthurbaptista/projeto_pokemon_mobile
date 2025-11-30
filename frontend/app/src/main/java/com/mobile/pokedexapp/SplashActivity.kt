package com.mobile.pokedexapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Espera 3 segundos e vai para o Login [cite: 9]
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            startActivity(android.content.Intent(this, LoginActivity::class.java))
            finish()
        }, 3000)
    }
}