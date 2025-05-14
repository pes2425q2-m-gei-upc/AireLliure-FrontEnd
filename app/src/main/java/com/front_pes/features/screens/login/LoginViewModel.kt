package com.front_pes.features.screens.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.front_pes.CurrentUser
import com.front_pes.features.screens.login.LoginRequest
import com.front_pes.features.screens.login.LoginResponse
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    fun signInWithGoogleCredential(credential: AuthCredential, home:() -> Unit)
    = viewModelScope.launch {
        try {
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task->
                    if (task.isSuccessful) {
                        Log.d("AireLliure", "Loguejat amb Google Exitòs!")
                        home()
                    }
                }
                .addOnFailureListener {
                    Log.d("AireLliure", "Errada en loguejar amb Google")
                }

        }
        catch (ex:Exception) {
            Log.d("AireLliure", "Excepció al loguejar amb Google" +
            "${ex.localizedMessage}")
        }
    }

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    var errorMessage by mutableStateOf<String?>(null)

    fun login(onSuccess: () -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            val call = RetrofitClient.apiService.login(LoginRequest(correu = email, password = password))
            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    _isLoading.value = false
                    if (response.code() == 200) {
                        response.body()?.let { userData ->
                            CurrentUser.correu = userData.correu
                            CurrentUser.password = userData.password
                            CurrentUser.nom = userData.nom
                            if (userData.about != null)CurrentUser.about = userData.about
                            else CurrentUser.about = ""
                            CurrentUser.estat = userData.estat
                            CurrentUser.punts = userData.punts
                            CurrentUser.administrador = userData.administrador
                        }
                        onSuccess();
                    } else {
                        // Si el código no es 200, muestra el mensaje adecuado
                        errorMessage = when (response.code()) {
                            404 -> "usuari no existeix"
                            401 -> "contrasenya incorrecta"
                            else -> "Error desconocido: ${response.code()}"
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    _isLoading.value = false
                    Log.e("LOGIN", "Error de red: ${t.message}", t)
                    errorMessage = "Network error: ${t.message}"
                }
            })
        }
    }
    fun exchangeAuthCodeForAccessToken(authCode: String, onTokenReceived: (String) -> Unit) {
        val client = OkHttpClient()

        val body = FormBody.Builder()
            .add("code", authCode)
            .add("client_id", "8587200690-n4o3qjmpcp8lemk9kgki9v8drpepmlb3.apps.googleusercontent.com")
            .add("client_secret", "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")
            .add("redirect_uri", "http://localhost")
            .add("grant_type", "authorization_code")
            .build()




        val request = Request.Builder()
            .url("https://oauth2.googleapis.com/token")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("TokenExchange", "Error: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val responseBody = response.body?.string() ?: ""
                Log.d("TokenExchange", "🟡 Respuesta cruda: $responseBody")

                val json = JSONObject(responseBody)
                val token = json.optString("access_token", "")
                if (token.isNotEmpty()) {
                    onTokenReceived(token)
                } else {
                    Log.e("TokenExchange", "No se recibió access_token")
                }
            }
        })
    }



}

