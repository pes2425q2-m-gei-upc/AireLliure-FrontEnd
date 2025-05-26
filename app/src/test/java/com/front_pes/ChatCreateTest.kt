// ktlint-disable
@file:Suppress("ALL")
package com.front_pes



import com.front_pes.features.screens.xats.ChatCreateResponse
import com.front_pes.features.screens.xats.ChatCreateViewModel
import com.front_pes.features.screens.xats.LlistaXatResponse
import com.front_pes.features.screens.xamistat.LlistaAmistatResponse
import com.front_pes.network.ApiService
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@ExperimentalCoroutinesApi
class ChatCreateViewModelTest {


    @Mock
    lateinit var mockApiService: ApiService

    @Mock
    lateinit var mockCallXats: Call<List<LlistaXatResponse>>

    @Mock
    lateinit var mockCallAmistats: Call<List<LlistaAmistatResponse>>

    @Mock
    lateinit var mockCallCreate: Call<ChatCreateResponse>

    private lateinit var viewModel: ChatCreateViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        RetrofitClient.apiService = mockApiService
        viewModel = ChatCreateViewModel()
    }

    @Test
    fun `crearXatIndividual should call onSuccess when response is successful`() {
        val dummyResponse = ChatCreateResponse(99)

        Mockito.`when`(mockApiService.createXatIndividual(any())).thenReturn(mockCallCreate)
        Mockito.doAnswer {
            val callback = it.arguments[0] as Callback<ChatCreateResponse>
            callback.onResponse(mockCallCreate, Response.success(dummyResponse))
            null
        }.`when`(mockCallCreate).enqueue(any())

        var successCalled = false
        viewModel.crearXatIndividual("Xat1", "amic@prova.com", {
            successCalled = it == 99
        }, {
            Assert.fail("onError should not be called")
        })

        Assert.assertTrue(successCalled)
    }

    @Test
    fun `crearXatIndividual should call onError on failure`() {
        Mockito.`when`(mockApiService.createXatIndividual(any())).thenReturn(mockCallCreate)
        Mockito.doAnswer {
            val callback = it.arguments[0] as Callback<ChatCreateResponse>
            callback.onFailure(mockCallCreate, Throwable("error simulat"))
            null
        }.`when`(mockCallCreate).enqueue(any())

        var errorCalled = false
        viewModel.crearXatIndividual("Xat1", "amic@prova.com", {
            Assert.fail("onSuccess should not be called")
        }, {
            errorCalled = it.contains("error simulat")
        })

        Assert.assertTrue(errorCalled)
    }

    @Test
    fun `carregarXats should update xatsExistents`() {
        val dummyXats = listOf(LlistaXatResponse(id = 10, nom = "TestXat"))

        Mockito.`when`(mockApiService.getXatsUsuaribyCorreu(any())).thenReturn(mockCallXats)
        Mockito.doAnswer {
            val callback = it.arguments[0] as Callback<List<LlistaXatResponse>>
            callback.onResponse(mockCallXats, Response.success(dummyXats))
            null
        }.`when`(mockCallXats).enqueue(any())

        viewModel.carregarXats()

        Assert.assertEquals(1, viewModel.xatsExistents.size)
        Assert.assertEquals("TestXat", viewModel.xatsExistents.first().nom)
    }
}
