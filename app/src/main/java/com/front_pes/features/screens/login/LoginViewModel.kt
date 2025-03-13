package com.front_pes.features.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.front_pes.features.screens.login.LoginRequest
import com.front_pes.features.screens.login.LoginResponse
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var loginSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    var username by mutableStateOf("")
        private set

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val call = RetrofitClient.apiService.login(LoginRequest(correu = email, password = password))
            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.code() == 200) {
                        // Si el código de respuesta es 200, se considera un éxito
                        loginSuccess = true
                        username = "canviat"
                        //onSuccess()  // Llamamos a onSuccess() cuando recibimos un 200 OK
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
                    errorMessage = "Network error: ${t.message}"
                }
            })
        }
    }
}