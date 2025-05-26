// ktlint-disable
@file:Suppress("ALL")
package com.front_pes.features.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.front_pes.CurrentUser
import com.front_pes.features.screens.login.LoginRequest
import com.front_pes.features.screens.login.LoginResponse
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest

class  RegisterViewModel : ViewModel() {
    var email by mutableStateOf("")
    var username by mutableStateOf("")
    var password by mutableStateOf("")

    var errorMessage by mutableStateOf<String?>(null)

    fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun register(onSuccess: () -> Unit) {
        val hashedPassword = hashPassword(password)
        val call = RetrofitClient.apiService.register(RegisterRequest(correu = email, password = hashedPassword, nom = username ))
        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.code() == 201) {
                    response.body()?.let { userData ->
                        CurrentUser.correu = userData.correu
                        CurrentUser.nom = userData.nom
                        CurrentUser.about = userData.about
                        CurrentUser.estat = userData.estat
                        CurrentUser.punts = userData.punts
                    }
                    onSuccess();
                } else {
                    // Si el código no es 200, muestra el mensaje adecuado
                    errorMessage = when (response.code()) {
                        400 -> "Campo incorrecto"
                        else -> "Error desconocido: ${response.code()}"
                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                errorMessage = "Network error: ${t.message}"
            }
        })

    }
}