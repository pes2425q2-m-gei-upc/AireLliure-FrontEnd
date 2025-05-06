package com.front_pes.features.screens.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.front_pes.CurrentUser
import com.front_pes.network.RetrofitClient
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
}