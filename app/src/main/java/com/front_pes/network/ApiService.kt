package com.front_pes.network

import com.front_pes.features.screens.xamistat.BloqueigRequest
import com.front_pes.features.screens.xamistat.BloqueigResponse
import com.front_pes.features.screens.Ranking.RankingResponse
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
import com.front_pes.features.screens.xamistat.SolicitarAmistatRequest
import com.front_pes.features.screens.xamistat.SolicitarAmistatResponse
import com.front_pes.features.screens.xats.LlistaXatResponse
import retrofit2.Call
import retrofit2.Response
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

    @GET("amistats/usuari/{pk}")
    suspend fun getAmistatUsuarybyCorreu(
        @Path("pk") pk: String,
    ): List<LlistaAmistatResponse>

    @GET("usuaris/{pk}/")
    fun getDetallUsuariAmic(
        @Path("pk") pk: String,
    ): Call<DetallUsuariResponse>

    @GET("amistats/usuari/{pk}/basics/")
    suspend fun get_all_usuaris(
        @Path("pk") pk: String
    ): List<DetallUsuariResponse>

    @GET("amistats/usuari/{pk}/rebudes/")
    suspend fun get_all_rebudes(
        @Path("pk") pk: String
    ): List<SolicitarAmistatResponse>

    @GET("amistats/usuari/{pk}/enviades/")
    suspend fun get_all_envaides(
        @Path("pk") pk: String
    ): List<SolicitarAmistatResponse>

    @GET("ranking-usuaris-all/")
    fun get_all_ranking(): Call<List<RankingResponse>>

    @GET("ranking-usuari-amics/{pk}/")
    fun get_ranking_amistats(
        @Path("pk") pk:String
    ): Call<List<RankingResponse>>

    @POST("amistats/create/")
    suspend fun create_new_amistat(
        @Body request: SolicitarAmistatRequest
    ): SolicitarAmistatResponse

    @PATCH("amistats/{pk}/update/")
    suspend fun update_amistat(
        @Path("pk") pk:Int,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): SolicitarAmistatResponse

    @DELETE("amistats/{pk}/delete/")
    suspend fun delete_amistat(
        @Path("pk") pk:Int
    ) : Response<Unit>

    @GET("bloqueigs/usuari/{pk}")
    suspend fun get_all_bloqueigs_usuari(
        @Path("pk") pk:String
    ): List<BloqueigResponse>

    @POST("bloqueigs/create/")
    suspend fun crear_bloqueig(
       @Body body: BloqueigRequest
    ): BloqueigResponse

    @DELETE("bloqueigs/{pk}/delete/")
    suspend fun eliminar_bloqueig(
        @Path("pk") pk:Int
    ): Response<Unit>

    @GET("/deshabilitats/")
    suspend fun get_deshabilitats(): List<RankingResponse>

    @GET("/habilitats/")
    suspend fun get_habilitats(): List<RankingResponse>

    @PATCH("usuaris/{correudeshabilitador}/deshabilitar/{correuusuari}/")
    suspend fun deshabilitar(
        @Path("correudeshabilitador") correudeshabilitador: String,
        @Path("correuusuari") correuusuari: String
    ): Response<Unit>

    @PATCH("usuaris/{correuusuari}/rehabilitar/")
    suspend fun rehabilitar(
        @Path("correuusuari") correuusuari: String
    ): Response<Unit>





}