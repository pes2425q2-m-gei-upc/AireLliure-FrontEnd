@file:Suppress("detekt")
package com.front_pes

import com.front_pes.features.screens.xats.ChatDetailViewModel
import com.front_pes.features.screens.xats.GroupDetailResponse
import com.front_pes.features.screens.xats.SendMessageResponse
import com.front_pes.features.screens.xats.UpdateMessageResponse
import com.front_pes.network.ApiService
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ChatDetailViewModelTest {

    private lateinit var viewModel: ChatDetailViewModel

    @Mock
    private lateinit var mockApiService: ApiService

    @Mock
    private lateinit var mockSendCall: Call<SendMessageResponse>

    @Mock
    private lateinit var mockUpdateCall: Call<UpdateMessageResponse>

    @Mock
    private lateinit var mockDeleteCall: Call<Unit>

    @Mock
    private lateinit var mockGroupCall: Call<GroupDetailResponse>

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        RetrofitClient.apiService = mockApiService
        viewModel = ChatDetailViewModel()
    }

    @Test
    fun `esborrarMissatge success should invoke onSuccess`() = runTest {
        `when`(mockApiService.deleteMissatge(eq(1))).thenReturn(mockDeleteCall)
        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onResponse(mockDeleteCall, Response.success(Unit))
            null
        }.`when`(mockDeleteCall).enqueue(any())

        var deleted = false
        viewModel.esborrarMissatge(1, { deleted = true }, {})

        assertTrue(deleted)
    }

    @Test
    fun `detectarSiEsGrup should set isGroup true on 200`() = runTest {
        val response = GroupDetailResponse(
            id = 1,
            descripcio = "hola",
            creador = "a@example.com",
            membres = listOf("a@example.com", "b@example.com"),
            nom = "Grup"
        )
        `when`(mockApiService.getXatGrupalById(eq(1))).thenReturn(mockGroupCall)
        doAnswer {
            val callback = it.getArgument<Callback<GroupDetailResponse>>(0)
            callback.onResponse(mockGroupCall, Response.success(response))
            null
        }.`when`(mockGroupCall).enqueue(any())

        viewModel.detectarSiEsGrup(1)
        assertTrue(viewModel.isGroup)
    }
}
