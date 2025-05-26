// ktlint-disable
@file:Suppress("ALL")
package com.front_pes

import com.front_pes.features.screens.xamistat.BloqueigRequest
import com.front_pes.features.screens.xamistat.DetallAmistatViewModel
import com.front_pes.features.screens.xamistat.DetallUsuari
import com.front_pes.features.screens.xamistat.DetallUsuariResponse
import com.front_pes.network.ApiService
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@ExperimentalCoroutinesApi
class DetallAmistatViewModelTest {

    @Mock
    private lateinit var mockApiService: ApiService

    @Mock
    private lateinit var mockCall: Call<DetallUsuariResponse>

    @Mock
    private lateinit var mockBloqueigCall: Call<Unit>

    private lateinit var viewModel: DetallAmistatViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        RetrofitClient.apiService = mockApiService
        viewModel = DetallAmistatViewModel()
        CurrentUser.correu = "jo@prova.com"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getDetallAmic should populate usuari on success`() = runTest {
        val dummyResponse = DetallUsuariResponse("amic@prova.com", about = "About Amic", deshabilitador = null, password = "Descripció", punts =50)
        whenever(mockApiService.getDetallUsuariAmic(any())).thenReturn(mockCall)

        Mockito.doAnswer {
            val callback = it.arguments[0] as Callback<DetallUsuariResponse>
            callback.onResponse(mockCall, Response.success(dummyResponse))
            null
        }.`when`(mockCall).enqueue(any())

        viewModel.getDetallAmic("amic@prova.com")
        advanceUntilIdle()

        val user = viewModel.usuari
        assertNotNull(user)
        assertEquals("amic@prova.com", user?.correu)
        assertEquals("About Amic", user?.about) // Se corrigió aquí para coincidir con el mock
    }

    @Test
    fun `getDetallAmic should set error message on 404`() = runTest {
        whenever(mockApiService.getDetallUsuariAmic(any())).thenReturn(mockCall)

        Mockito.doAnswer {
            val callback = it.arguments[0] as Callback<DetallUsuariResponse>
            callback.onResponse(mockCall, Response.error(404, okhttp3.ResponseBody.create(null, "")))
            null
        }.`when`(mockCall).enqueue(any())

        viewModel.getDetallAmic("amic@prova.com")
        advanceUntilIdle()

        assertEquals("usuari no existeix", viewModel.errorMessage)
    }

    @Test
    fun `getDetallAmic should set error message on failure`() = runTest {
        whenever(mockApiService.getDetallUsuariAmic(any())).thenReturn(mockCall)

        Mockito.doAnswer {
            val callback = it.arguments[0] as Callback<DetallUsuariResponse>
            callback.onFailure(mockCall, Throwable("Error de xarxa"))
            null
        }.`when`(mockCall).enqueue(any())

        viewModel.getDetallAmic("amic@prova.com")
        advanceUntilIdle()

        assertTrue(viewModel.errorMessage?.contains("Network error") == true)
    }

    @Test
    fun `bloquejar_usuari should call API if usuari is set`() = runTest {
        val dummyUser = DetallUsuari("amic@prova.com", "Nom Amic", "", 0)
        viewModel.usuari = dummyUser
        Mockito.doAnswer {
            val callback = it.arguments[0] as Callback<Unit>
            callback.onResponse(mockBloqueigCall, Response.success(Unit))
            null
        }.`when`(mockBloqueigCall).enqueue(any())

        viewModel.bloquejar_usuari()
        advanceUntilIdle()

        Mockito.verify(mockApiService).crear_bloqueig(BloqueigRequest("jo@prova.com", "amic@prova.com"))
    }
}
