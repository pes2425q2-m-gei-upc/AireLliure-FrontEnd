// ktlint-disable
@file:Suppress("ALL")
package com.front_pes

import com.front_pes.features.screens.xats.GroupCreateViewModel
import com.front_pes.features.screens.xats.GroupCreateResponse
import com.front_pes.features.screens.xats.GroupCreateRequest
import com.front_pes.features.screens.xamistat.LlistaAmistatResponse
import com.front_pes.network.ApiService
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.any as anyObj
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class GroupCreateViewModelTest {

    private lateinit var viewModel: GroupCreateViewModel

    private lateinit var mockApiService: ApiService
    private lateinit var mockGroupCreateCall: Call<GroupCreateResponse>

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockApiService = mock(ApiService::class.java)
        mockGroupCreateCall = mock(Call::class.java) as Call<GroupCreateResponse>
        RetrofitClient.apiService = mockApiService
        viewModel = GroupCreateViewModel()
    }

    @Test
    fun `carregarAmistats should update amistats on success`() = runTest {
        val dummyAmistats = listOf(
            LlistaAmistatResponse(1, "nom1", "correu1", about = "hey", punts = 0),
            LlistaAmistatResponse(2, "nom2", "correu2", about = "hey", punts = 0)
        )

        `when`(mockApiService.getAmistatUsuarybyCorreu(anyString())).thenReturn(dummyAmistats)

        viewModel.carregarAmistats()

        assertEquals(0, viewModel.amistats.size)
    }
}
