package com.front_pes.network

import com.front_pes.features.screens.login.LoginRequest
import com.front_pes.features.screens.login.LoginResponse
import com.front_pes.features.screens.map.EstacioQualitatAireResponse
import com.front_pes.features.screens.map.PuntsResponse
import com.front_pes.features.screens.map.RutasResponse
import com.front_pes.features.screens.register.RegisterRequest
import com.front_pes.features.screens.register.RegisterResponse
import com.front_pes.features.screens.user.UpdateProfileRequest
import com.front_pes.features.screens.user.UpdateProfileResponse
import com.front_pes.features.screens.xamistat.DetallUsuariResponse
import com.front_pes.features.screens.xamistat.LlistaAmistatResponse
import com.front_pes.features.screens.xats.ChatCreateRequest
import com.front_pes.features.screens.xats.ChatCreateResponse
import com.front_pes.features.screens.xats.LlistaXatResponse
import com.front_pes.features.screens.xats.ChatDetailResponse
import com.front_pes.features.screens.xats.GroupCreateRequest
import com.front_pes.features.screens.xats.GroupCreateResponse
import com.front_pes.features.screens.xats.SendMessageRequest
import com.front_pes.features.screens.xats.SendMessageResponse
import com.front_pes.features.screens.xats.UpdateMessageRequest
import com.front_pes.features.screens.xats.UpdateMessageResponse

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
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
    ): Call<List<LlistaXatResponse>>

    @GET("xats/{id}/")
    fun getChatDetail(
        @Path("id") id: Int
    ): Call<ChatDetailResponse>

    @POST("xats-individual/create/")
    fun createXatIndividual(@Body request: ChatCreateRequest): Call<ChatCreateResponse>

    @POST("xats-grupal/create/")
    fun createXatGrupal(@Body request: GroupCreateRequest): Call<GroupCreateResponse>

    @POST("missatges/create/")
    fun enviarMissatge(@Body request: SendMessageRequest): Call<SendMessageResponse>

    @PATCH("missatges/{pk}/update/")
    fun updateMissatge(
        @Path("pk") id: Int,
        @Body request: UpdateMessageRequest
    ): Call<UpdateMessageResponse>

    @DELETE("missatges/{pk}/delete/")
    fun deleteMissatge(@Path("pk") id: Int): Call<Unit>

    @GET("amistats/usuari/{pk}")
    fun getAmistatUsuarybyCorreu(
        @Path("pk") pk: String,
    ): Call<List<LlistaAmistatResponse>>

    @GET("usuaris/{pk}/")
    fun getDetallUsuariAmic(
        @Path("pk") pk: String,
    ): Call<DetallUsuariResponse>

    @GET("usuaris")
    fun get_all_usuaris(): Call<List<DetallUsuariResponse>>
}