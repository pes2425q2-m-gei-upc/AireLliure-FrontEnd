// ktlint-disable
@file:Suppress("ALL")
package com.front_pes

import com.front_pes.features.screens.Ranking.RankingViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*
import com.front_pes.features.screens.Ranking.RankingResponse
import com.front_pes.network.ApiService
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@ExperimentalCoroutinesApi
class RankingViewModelTest {

    @Mock
    lateinit var mockApiService: ApiService

    @Mock
    lateinit var mockCall: Call<List<RankingResponse>>

    private lateinit var viewModel: RankingViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        RetrofitClient.apiService = mockApiService
        viewModel = RankingViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be empty`() {
        assertTrue(viewModel.ranking_all_users.isEmpty())
        assertTrue(viewModel.ranking_amics.isEmpty())
        assertEquals("", viewModel.errorMessage)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun ranking_tt_users_should_populate_ranking_all_users_on_success() = runTest {
        val responseList = listOf(
            RankingResponse(nom = "Anna", correu ="anna@gmail.com", password="a", estat= "actiu", deshabilitador = null, punts = 100),
            RankingResponse(nom = "Marc",correu ="marc@gmail.com", password="a", estat= "actiu", deshabilitador = null, punts = 80)
        )
        Mockito.`when`(mockApiService.get_all_ranking()).thenReturn(mockCall)
        Mockito.doAnswer { invocation ->
            val callback = invocation.arguments[0] as Callback<List<RankingResponse>>
            callback.onResponse(mockCall, Response.success(responseList))
            null
        }.`when`(mockCall).enqueue(Mockito.any())
        viewModel.ranking_tt_users()
        advanceUntilIdle()
        Assert.assertEquals(2, viewModel.ranking_all_users.size)
        Assert.assertEquals("Anna", viewModel.ranking_all_users[0].name)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `ranking_tt_users should set error message on 404`() = runTest {
        Mockito.`when`(mockApiService.get_all_ranking()).thenReturn(mockCall)

        Mockito.doAnswer {
            val callback = it.arguments[0] as Callback<List<RankingResponse>>
            callback.onResponse(
                mockCall,
                Response.error(404, okhttp3.ResponseBody.create(null, ""))
            )
            null
        }.`when`(mockCall).enqueue(Mockito.any())

        viewModel.ranking_tt_users()

        advanceUntilIdle()

        Assert.assertEquals("usuari no existeix", viewModel.errorMessage)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `ranking_tt_users should set error message on failure`() = runTest {
        Mockito.`when`(mockApiService.get_all_ranking()).thenReturn(mockCall)

        Mockito.doAnswer {
            val callback = it.arguments[0] as Callback<List<RankingResponse>>
            callback.onFailure(mockCall, Throwable("network error"))
            null
        }.`when`(mockCall).enqueue(Mockito.any())

        viewModel.ranking_tt_users()

        advanceUntilIdle() // Espera a que termine la coroutine

        Assert.assertTrue(viewModel.errorMessage!!.contains("Error al carregar el Ranking"))
    }

}

