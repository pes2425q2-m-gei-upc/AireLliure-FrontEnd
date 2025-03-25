package com.front_pes.features.screens.login

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
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

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
                            CurrentUser.about = userData.about
                            CurrentUser.estat = userData.estat
                            CurrentUser.punts = userData.punts
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
                    errorMessage = "Network error: ${t.message}"
                }
            })
        }
    }
}