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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    private fun registerUserGoogle(correu: String, nom: String, password: String, home: () -> Unit) {
        val call = RetrofitClient.apiService.register(
            com.front_pes.features.screens.register.RegisterRequest(
                correu = correu,
                nom = nom,
                password = password
            )
        )
        call.enqueue(object : Callback<com.front_pes.features.screens.register.RegisterResponse> {
            override fun onResponse(call: Call<com.front_pes.features.screens.register.RegisterResponse>, response: Response<com.front_pes.features.screens.register.RegisterResponse>) {
                if (response.isSuccessful || response.code() == 201) {
                    response.body()?.let { userData ->
                        CurrentUser.correu = userData.correu
                        CurrentUser.password = userData.password
                        CurrentUser.nom = userData.nom
                        CurrentUser.about = userData.about
                        CurrentUser.estat = userData.estat
                        CurrentUser.punts = userData.punts
                    }
                    _isLoading.value = false
                    home()
                } else {
                    _isLoading.value = false
                    errorMessage = "No s'ha pogut registrar l'usuari amb Google"
                }
            }

            override fun onFailure(call: Call<com.front_pes.features.screens.register.RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                errorMessage = "Error de registre amb Google: ${t.message}"
            }
        })
    }

    fun signInWithGoogleCredential(credential: AuthCredential, home:() -> Unit)
    = viewModelScope.launch {
        try {
            _isLoading.value = true
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("AireLliure", "Loguejat amb Google Exitòs!")
                        val user = auth.currentUser
                        val correu = user?.email ?: ""
                        val nom = user?.displayName ?: correu.substringBefore("@")
                        val password = "google_default_password"

                        val loginCall =
                            RetrofitClient.apiService.login(LoginRequest(correu, password))
                        loginCall.enqueue(object : Callback<LoginResponse> {
                            override fun onResponse(
                                call: Call<LoginResponse>,
                                response: Response<LoginResponse>
                            ) {
                                if (response.isSuccessful) {
                                    // Ya existe → guarda y entra
                                    response.body()?.let { userData ->
                                        CurrentUser.correu = userData.correu
                                        CurrentUser.password = userData.password
                                        CurrentUser.nom = userData.nom
                                        CurrentUser.about = userData.about
                                        CurrentUser.estat = userData.estat
                                        CurrentUser.punts = userData.punts
                                        CurrentUser.administrador = userData.administrador
                                    }
                                    _isLoading.value = false
                                    home()
                                } else {
                                    // Si no existe → intenta registrarlo
                                    registerUserGoogle(correu, nom, password, home)
                                }
                            }

                            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                // Error de red → manejar si quieres
                                _isLoading.value = false
                                errorMessage = "Error de red al comprobar login"
                            }
                        })
                    } else {
                        _isLoading.value = false
                        errorMessage = "Error autenticant amb Google"
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
}