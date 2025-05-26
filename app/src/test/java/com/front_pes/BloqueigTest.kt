// ktlint-disable
@file:Suppress("ALL")
package com.front_pes

import com.front_pes.CurrentUser
import com.front_pes.features.screens.xamistat.BloqueigResponse
import com.front_pes.features.screens.xamistat.BloqueigViewModel
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

@ExperimentalCoroutinesApi
class BloqueigViewModelTest {

    @Mock
    private lateinit var mockApiService: ApiService

    private lateinit var viewModel: BloqueigViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        RetrofitClient.apiService = mockApiService
        CurrentUser.correu = "usuari@example.com"
        viewModel = BloqueigViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `get_all_bloquejats should populate list on success`() = runTest {
        val mockResponse = listOf(
            BloqueigResponse(id = 1, bloqueja = "a@example.com", bloquejat = "bloc1@example.com"),
            BloqueigResponse(id = 2,bloqueja = "a@example.com", bloquejat = "bloc2@example.com")
        )

        Mockito.`when`(mockApiService.get_all_bloqueigs_usuari(any())).thenReturn(mockResponse)

        viewModel.get_all_bloquejats()
        advanceUntilIdle()

        assertEquals(2, viewModel.usuaris_bloquejats.size)
        assertEquals("bloc1@example.com", viewModel.usuaris_bloquejats[0].id_correu_usuari)
    }

    @Test
    fun `get_all_bloquejats should handle exception`() = runTest {
        Mockito.`when`(mockApiService.get_all_bloqueigs_usuari(any())).thenThrow(RuntimeException("network error"))

        viewModel.get_all_bloquejats()
        advanceUntilIdle()

        assertTrue(viewModel.usuaris_bloquejats.isEmpty())
    }
}