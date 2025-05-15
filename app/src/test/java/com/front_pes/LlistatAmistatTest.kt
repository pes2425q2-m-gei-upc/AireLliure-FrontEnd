package com.front_pes
import com.front_pes.CurrentUser
import com.front_pes.features.screens.login.LoginResponse
import com.front_pes.features.screens.xamistat.LlistatAmistatViewModel
import com.front_pes.features.screens.xamistat.LlistaAmistatResponse
import com.front_pes.network.ApiService
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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

@ExperimentalCoroutinesApi
class LlistatAmistatViewModelTest {

    @Mock
    private lateinit var mockApiService: ApiService

    private lateinit var viewModel: LlistatAmistatViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        RetrofitClient.apiService = mockApiService
        viewModel = LlistatAmistatViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `seguir_usuari should call API and refresh data`() = runTest {
        Mockito.`when`(mockApiService.getAmistatUsuarybyCorreu(any())).thenReturn(emptyList())
        Mockito.`when`(mockApiService.get_all_rebudes(any())).thenReturn(emptyList())
        Mockito.`when`(mockApiService.get_all_usuaris(any())).thenReturn(emptyList())
        Mockito.`when`(mockApiService.get_all_envaides(any())).thenReturn(emptyList())

        viewModel.seguir_usuari("amic@example.com")

        assertTrue(viewModel.llista_amics.isEmpty())
    }

    @Test
    fun `cancelar_solicitud_enviada should call delete API`() = runTest {
        Mockito.`when`(mockApiService.getAmistatUsuarybyCorreu(any())).thenReturn(emptyList())
        Mockito.`when`(mockApiService.get_all_rebudes(any())).thenReturn(emptyList())
        Mockito.`when`(mockApiService.get_all_usuaris(any())).thenReturn(emptyList())
        Mockito.`when`(mockApiService.get_all_envaides(any())).thenReturn(emptyList())

        viewModel.cancelar_solicitud_enviada(123)

        assertTrue(viewModel.all_enviades.isEmpty())
    }

    @Test
    fun `aceptar_solicitud_rebuda should update amistat`() = runTest {
        Mockito.`when`(mockApiService.getAmistatUsuarybyCorreu(any())).thenReturn(emptyList())
        Mockito.`when`(mockApiService.get_all_rebudes(any())).thenReturn(emptyList())
        Mockito.`when`(mockApiService.get_all_usuaris(any())).thenReturn(emptyList())
        Mockito.`when`(mockApiService.get_all_envaides(any())).thenReturn(emptyList())

        viewModel.aceptar_solicitud_rebuda(456)

        assertTrue(viewModel.all_rebudes.isEmpty())
    }
}

