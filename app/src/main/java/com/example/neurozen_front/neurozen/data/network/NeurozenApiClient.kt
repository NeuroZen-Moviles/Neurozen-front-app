package com.example.neurozen_front.neurozen.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NeurozenApiClient {

    val service: NeurozenApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NeurozenApiService::class.java)
    }
}

