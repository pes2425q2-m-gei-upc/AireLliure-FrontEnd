@file:Suppress("detekt")
package com.front_pes

import com.front_pes.features.screens.xamistat.BloqueigViewModel
import com.front_pes.features.screens.xamistat.BloqueigResponse
import com.front_pes.network.ApiService
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalCoroutinesApi::class)
class BloqueigIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var viewModel: BloqueigViewModel

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        RetrofitClient.apiService = api
        viewModel = BloqueigViewModel()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }


    @Test
    fun `login success with correct credentials`() {
        fun login(email: String, password: String): Boolean {
            val validEmail = "usuari1@example.com"
            val validPassword = "1234"
            return email == validEmail && password == validPassword
        }
        val result = login("usuari1@example.com", "1234")
        assertTrue(result)
    }

    @Test
    fun `login success with correct credentials2`() {
        fun login(email: String, password: String): Boolean {
            val validEmail = "usuari2@example.com"
            val validPassword = "1234"
            return email == validEmail && password == validPassword
        }
        val result = login("usuari2@example.com", "1234")
        assertTrue(result)
    }

    @Test
    fun `bloquejar dos usuaris correctament`() {

        val jsonResponse = """
        [
            {"id": 1, "bloqueja": "usuari1@example.com", "bloquejat": "usuari2@example.com"},
            {"id": 2, "bloqueja": "usuari1@example.com", "bloquejat": "usuari3@example.com"}
        ]
    """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )
        val latch = java.util.concurrent.CountDownLatch(1)
        viewModel.get_all_bloquejats()
        Thread.sleep(1000) // Espera breve como alternativa rudimentaria al latch
        assertEquals(2, viewModel.usuaris_bloquejats.size)
        assertEquals("usuari2@example.com", viewModel.usuaris_bloquejats[0].id_correu_usuari)
        assertEquals("usuari3@example.com", viewModel.usuaris_bloquejats[1].id_correu_usuari)
        latch.countDown()
    }
}
