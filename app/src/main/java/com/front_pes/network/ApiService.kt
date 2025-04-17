package com.front_pes.network

import com.front_pes.features.screens.login.LoginRequest
import com.front_pes.features.screens.login.LoginResponse
import com.front_pes.features.screens.map.EstacioQualitatAireResponse
import com.front_pes.features.screens.map.PresenciaResponse
import com.front_pes.features.screens.map.PuntsResponse
import com.front_pes.features.screens.map.RutasResponse
import com.front_pes.features.screens.register.RegisterRequest
import com.front_pes.features.screens.register.RegisterResponse
import com.front_pes.features.screens.user.UpdateProfileRequest
import com.front_pes.features.screens.user.UpdateProfileResponse
import com.front_pes.features.screens.xats.LlistaXatRequest
import com.front_pes.features.screens.xats.LlistaXatResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

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

    @POST("usuaris/create/")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @DELETE("usuaris/{pk}/delete/")
    fun deleteUser(@Path("pk") userId: String): Call<Void>

    @GET("rutas/")
    fun getRutas(): Call<List<RutasResponse>>

    @GET("punts/{pk}/")
    fun getPuntByID(
        @Path("pk") pk: Int
    ): Call<PuntsResponse>

    @GET("xats/usuari/{pk}")
    fun getXatsUsuaribyCorreu(
        @Path("pk") pk: String,
        @Body request : LlistaXatRequest
    ): Call<LlistaXatResponse>

    @GET("presencies/punt/{pk}/")
    fun getPresencia(
        @Path("pk") stationId: Int,
        @QueryMap filters: Map<String, String>
    ): Call<List<PresenciaResponse>>
}