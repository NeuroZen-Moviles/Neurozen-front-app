package com.example.neurozen_front.neurozen.home.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurozen_front.neurozen.data.network.NeurozenRepository
import com.example.neurozen_front.neurozen.data.session.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class LoginViewModel(
    private val repository: NeurozenRepository = NeurozenRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState(email = "demo@neurozen.app", password = "123456"))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun login(onSuccess: () -> Unit) {
        val current = _uiState.value
        val validEmail = current.email.contains("@") && current.email.contains(".")
        val validPassword = current.password.length >= 6

        if (!validEmail || !validPassword) {
            _uiState.update { it.copy(errorMessage = "Revisa correo y contrasena (minimo 6 caracteres)") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.login(email = current.email, password = current.password)
                .onSuccess { session ->
                    UserSession.save(session)
                    _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "No se pudo iniciar sesion"
                        )
                    }
                }
        }
    }
}

