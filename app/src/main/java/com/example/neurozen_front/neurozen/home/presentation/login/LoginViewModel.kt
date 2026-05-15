package com.example.neurozen_front.neurozen.home.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurozen_front.neurozen.data.network.NeurozenRepository
import com.example.neurozen_front.neurozen.data.session.UserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegisterMode: Boolean = false,
    val successMessage: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: NeurozenRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState(email = "demo@neurozen.app", password = "123456"))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value, errorMessage = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun toggleMode() {
        _uiState.update { it.copy(isRegisterMode = !it.isRegisterMode, errorMessage = null, successMessage = null) }
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

    fun register() {
        val current = _uiState.value
        if (current.name.isBlank() || current.email.isBlank() || current.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Completa todos los campos correctamente") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.register(current.name, current.email, current.password)
                .onSuccess {
                    _uiState.update { it.copy(
                        isLoading = false,
                        isRegisterMode = false,
                        successMessage = "¡Registro exitoso! Ahora puedes iniciar sesión"
                    ) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al registrar usuario"
                    ) }
                }
        }
    }
}

