package com.front_pes.network

import com.front_pes.features.screens.login.LoginRequest
import com.front_pes.features.screens.login.LoginResponse
import com.front_pes.features.screens.map.EstacioQualitatAireResponse
import com.front_pes.features.screens.user.UpdateProfileRequest
import com.front_pes.features.screens.user.UpdateProfileResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("login/")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @PATCH("usuaris/{pk}/update/")
    fun updateProfile(
        @Path("pk") userId: String,
        @Body request: UpdateProfileRequest
    ): Call<UpdateProfileResponse>

    @GET("estacions-qualitat-aire/")
    fun getEstacionsQualitatAire(): Call<List<EstacioQualitatAireResponse>>
}