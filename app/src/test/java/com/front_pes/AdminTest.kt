package com.front_pes

import com.front_pes.features.screens.administrador.HabResponse
import com.front_pes.features.screens.administrador.HabilitacionsViewModel
import kotlinx.coroutines.test.advanceUntilIdle
import com.front_pes.network.ApiService
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Call
import retrofit2.Response

@ExperimentalCoroutinesApi
class HabilitacionsViewModelTest {

    @Mock
    lateinit var mockApiService: ApiService

    private lateinit var viewModel: HabilitacionsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        RetrofitClient.apiService = mockApiService
        viewModel = HabilitacionsViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `get_all_usuaris_habilitats sets habilitats correctly`() = runTest {
        val mockResponse = listOf(
            HabResponse("anna@gmail.com", nom = "Anna", punts = 0, password="a", deshabilitador = null, administrador = false, estat= "actiu"),
            HabResponse("marc@gmail.com", nom = "Marc", punts = 0, password="a", deshabilitador = null, administrador = false, estat= "actiu")
        )
        Mockito.`when`(mockApiService.gethabilitats()).thenReturn(mockResponse)

        viewModel.get_all_usuaris_habilitats()
        advanceUntilIdle()

        assertEquals(2, viewModel.habilitats.size)
        assertEquals("Anna", viewModel.habilitats[0].nom)
    }

    @Test
    fun `get_all_usuaris_deshabilitats sets deshabilitats correctly`() = runTest {
        val mockResponse = listOf(
            HabResponse("john@gmail.com", nom = "John", punts = 0, password="a", deshabilitador = null, administrador = false, estat= "actiu")
        )
        Mockito.`when`(mockApiService.getdeshabilitats()).thenReturn(mockResponse)

        viewModel.get_all_usuaris_deshabilitats()
        advanceUntilIdle()

        assertEquals(1, viewModel.deshabilitats.size)
        assertEquals("John", viewModel.deshabilitats[0].nom)
    }
}
