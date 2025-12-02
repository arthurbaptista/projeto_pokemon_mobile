package com.pokedex.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // URL do servidor oficial no Render
    private const val BASE_URL = "https://pokedex-api-kcck.onrender.com/"

    // Variável para guardar o Token (Crachá)
    var authToken: String? = null

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Interceptador para colocar o Token em todas as chamadas
    private val authInterceptor = okhttp3.Interceptor { chain ->
        var request = chain.request()
        authToken?.let { token ->
            request = request.newBuilder()
                .addHeader("Authorization", "Token $token")
                .build()
        }
        chain.proceed(request)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .build()

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}