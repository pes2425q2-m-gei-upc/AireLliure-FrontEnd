package com.front_pes.network

import com.front_pes.features.screens.login.LoginRequest
import com.front_pes.features.screens.login.LoginResponse
import com.front_pes.features.screens.map.EstacioQualitatAireResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("login/")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("estacions-qualitat-aire/")
    fun getEstacionsQualitatAire(): Call<List<EstacioQualitatAireResponse>>
}