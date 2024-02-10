package com.example.myapplication.data.model

import retrofit2.Response
import retrofit2.http.GET


interface ApiService {
    @GET("EUR")
    suspend fun obtenerDatos(): Response<Divisa>
}


