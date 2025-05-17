package com.front_pes.network

import com.front_pes.features.screens.ActivitatsEvents.ActivityPrivRequest
import com.front_pes.features.screens.ActivitatsEvents.ActivityRequest
import com.front_pes.features.screens.ActivitatsEvents.ActivityResponse
import com.front_pes.features.screens.ActivitatsEvents.ApuntarseRequest
import com.front_pes.features.screens.Ranking.RankingResponse
import com.front_pes.features.screens.administrador.HabResponse
import com.front_pes.features.screens.login.LoginRequest
import com.front_pes.features.screens.login.LoginResponse
import com.front_pes.features.screens.map.AccessibilitatResponse
import com.front_pes.features.screens.map.AssignacioAccessibilitatRequest
import com.front_pes.features.screens.map.AssignacioDificultatRequest
import com.front_pes.features.screens.map.DificultatResponse
import com.front_pes.features.screens.map.EstacioQualitatAireResponse
import com.front_pes.features.screens.map.PresenciaResponse
import com.front_pes.features.screens.map.PuntsResponse
import com.front_pes.features.screens.map.RutaAfegirValRequest
import com.front_pes.features.screens.map.RutaWrapperResponse
import com.front_pes.features.screens.map.RutasResponse
import com.front_pes.features.screens.map.UpdateValoracioRequest
import com.front_pes.features.screens.map.UpdateValoracioResponse
import com.front_pes.features.screens.register.RegisterRequest
import com.front_pes.features.screens.register.RegisterResponse
import com.front_pes.features.screens.user.UpdateProfileRequest
import com.front_pes.features.screens.user.UpdateProfileResponse
import com.front_pes.features.screens.xamistat.BloqueigRequest
import com.front_pes.features.screens.xamistat.BloqueigResponse
import com.front_pes.features.screens.xamistat.DetallUsuariResponse
import com.front_pes.features.screens.xamistat.LlistaAmistatResponse
import com.front_pes.features.screens.xamistat.SolicitarAmistatRequest
import com.front_pes.features.screens.xamistat.SolicitarAmistatResponse
import com.front_pes.features.screens.xats.ChatCreateRequest
import com.front_pes.features.screens.xats.ChatCreateResponse
import com.front_pes.features.screens.xats.ChatDetailResponse
import com.front_pes.features.screens.xats.GroupCreateRequest
import com.front_pes.features.screens.xats.GroupCreateResponse
import com.front_pes.features.screens.xats.GroupDetailResponse
import com.front_pes.features.screens.xats.GroupUpdateRequest
import com.front_pes.features.screens.xats.LlistaXatRequest
import com.front_pes.features.screens.xats.LlistaXatResponse
import com.front_pes.features.screens.xats.SendMessageRequest
import com.front_pes.features.screens.xats.SendMessageResponse
import com.front_pes.features.screens.xats.UpdateMessageRequest
import com.front_pes.features.screens.xats.UpdateMessageResponse
import retrofit2.Call
import retrofit2.Response
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

    @DELETE("xats-grupal/{id}/delete/")
    fun deleteXatGrupal(@Path("id") id: Int): Call<Unit>

    @GET("xats-grupal/{pk}/")
    fun getXatGrupalById(@Path("pk") id: Int): Call<GroupDetailResponse>

    @PATCH("xats-grupal/{pk}/update/")
    fun updateXatGrupal(
        @Path("pk") id: Int,
        @Body request: GroupUpdateRequest
    ): Call<Unit>

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
    suspend fun getdeshabilitats(): List<HabResponse>

    @GET("/habilitats/")
    suspend fun gethabilitats(): List<HabResponse>

    @PATCH("usuaris/{correu_deshabilitador}/deshabilitar/{correu_usuari}/")
    suspend fun deshabilitar(
        @Path("correu_deshabilitador") correudeshabilitador: String,
        @Path("correu_usuari") correuusuari: String
    ): Response<Unit>

    @PATCH("usuaris/{correu_usuari}/rehabilitar/")
    suspend fun rehabilitar(
        @Path("correu_usuari") correuusuari: String
    ): Response<Unit>

    @GET("rutas/{pk}/info/")
    suspend fun get_info_ruta(
        @Path("pk") pk:Int
    ): RutaWrapperResponse

    @POST("valoracions/create/")
    suspend fun afegir_valoracio(
        @Body request: RutaAfegirValRequest
    ): Response<Unit>

    @POST("assignacions-esportiva/create/")
    suspend fun postAssignacioEsportiva(
        @Body request: AssignacioDificultatRequest
    ): Response<Unit>

    @POST("assignacions-accesibilitat-respiratoria/create/")
    suspend fun postAssignacioAccessibilitat(
        @Body request: AssignacioAccessibilitatRequest
    ): Response<Unit>

    @GET("assig-esportiva/{ruta}/")
    suspend fun getAssignacioEsportiva(
        @Path("ruta") rutaId: Int
    ): List<DificultatResponse>

    @GET("assig-acc-resp/{ruta}/")
    suspend fun getAssignacioAccessibilitat(
        @Path("ruta") rutaId: Int
    ): List<AccessibilitatResponse>

    @PATCH("valoracions/{id}/update/")
    suspend fun updateValoracio(
        @Path("id") id: Int,
        @Body request: UpdateValoracioRequest
    ): Response<UpdateValoracioResponse>

    @DELETE("valoracions/{id}/delete/")
    suspend fun deleteValoracio(
        @Path("id") id: Int
    ): Response<Unit>

    @GET("usuaris/{pk}/")
    suspend fun get_user_by_id(
        @Path("pk") pk:String
    ): Response <LoginResponse>

    @DELETE("eliminar-participacio/{pk_usuari}/{pk_event}/")
    suspend fun eliminar_participacio(
        @Path("pk_usuari") pk_usuari:String,
        @Path("pk_event") pk_event: Int
    ): Response<Unit>

    @GET("events-pudels-usuari/{pk}/")
    suspend fun get_on_participo(
        @Path("pk") pk: String
    ): List<ActivityResponse>

    @PATCH("events-de-calendari-publics/{pk}/update/")
    suspend fun editar_event_public(
        @Path("pk") pk:Int,
        @Body body: ActivityRequest
    ): Response<Unit>

    @GET("events-de-calendari-publics/")
    suspend fun all_events(): List<ActivityResponse>

    @POST("events-de-calendari-publics/create/")
    suspend fun create_new_event(
        @Body body: ActivityRequest
    ): Response<Unit>

    @POST("events-de-calendari-privats/create/")
    suspend fun create_new_event_privat(
        @Body body: ActivityPrivRequest
    ): Response<Unit>

    @POST("apuntats/create/")
    suspend fun apuntarse(
        @Body body: ApuntarseRequest
    ): Response<Unit>

    @DELETE("events-de-calendari-publics/{pk}/delete/")
    suspend fun eliminar_event_public(
        @Path("pk") pk:Int
    ): Response<Unit>

    @GET("events-privats-xat/{pk}/")
    suspend fun get_activitats_by_xat(
        @Path("pk") pk: Int
    ): List<ActivityResponse>



}