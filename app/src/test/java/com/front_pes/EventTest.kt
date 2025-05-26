// ktlint-disable
@file:Suppress("ALL")
package com.front_pes

import com.front_pes.features.screens.ActivitatsEvents.ActivityResponse
import com.front_pes.features.screens.ActivitatsEvents.eventViewModel
import kotlinx.coroutines.test.advanceUntilIdle
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
import retrofit2.Response

@ExperimentalCoroutinesApi
class EventViewModelTest {

    @Mock
    lateinit var mockApiService: ApiService

    private lateinit var viewModel: eventViewModel
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(dispatcher)
        RetrofitClient.apiService = mockApiService
        viewModel = eventViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `get_all_publiques should populate events`() = runTest {
        val dummyEvents = listOf(
            ActivityResponse(1, "Event 1", "desc", "2025-01-01", "2025-01-02", "admin"),
            ActivityResponse(2, "Event 2", "desc", "2025-02-01", "2025-02-02", "admin")
        )
        Mockito.`when`(mockApiService.all_events()).thenReturn(dummyEvents)

        viewModel.get_all_publiques()
        advanceUntilIdle()

        assertEquals(2, viewModel.events.size)
        assertEquals("Event 1", viewModel.events[0].nom)
    }

    @Test
    fun `get_participacions should populate participacions`() = runTest {
        val dummy = listOf(
            ActivityResponse(3, "My Event", "desc", "2025-03-01", "2025-03-02", "user")
        )
        Mockito.`when`(mockApiService.get_on_participo(Mockito.anyString())).thenReturn(dummy)

        viewModel.get_participacions()
        advanceUntilIdle()

        assertEquals(1, viewModel.participacions.size)
        assertEquals("My Event", viewModel.participacions[0].nom)
    }


    @Test
    fun `eliminar_event should refresh lists`() = runTest {
        Mockito.`when`(mockApiService.all_events()).thenReturn(emptyList())
        Mockito.`when`(mockApiService.get_on_participo(Mockito.anyString())).thenReturn(emptyList())

        viewModel.eliminar_event(1)
        advanceUntilIdle()

        assertEquals(0, viewModel.events.size)
    }

    @Test
    fun `abandonar should refresh and call success`() = runTest {
        val dummyResponse = Response.success(Unit)
        Mockito.`when`(mockApiService.eliminar_participacio(Mockito.anyString(), Mockito.anyInt())).thenReturn(dummyResponse)
        Mockito.`when`(mockApiService.all_events()).thenReturn(emptyList())
        Mockito.`when`(mockApiService.get_on_participo(Mockito.anyString())).thenReturn(emptyList())

        var successCalled = false
        viewModel.abandonar(2) { successCalled = true }
        advanceUntilIdle()

        assertTrue(successCalled)
    }
}
