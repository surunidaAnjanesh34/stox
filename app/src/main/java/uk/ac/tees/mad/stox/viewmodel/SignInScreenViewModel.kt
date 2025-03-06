package uk.ac.tees.mad.stox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uk.ac.tees.mad.stox.model.dataclass.firebase.AuthResult
import uk.ac.tees.mad.stox.model.repository.AuthRepository

class SignInScreenViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _signInResult = MutableStateFlow<AuthResult<Boolean>>(AuthResult.Success(false))
    val signInResult: StateFlow<AuthResult<Boolean>> = _signInResult.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible = _isPasswordVisible.asStateFlow()

    private val _isSignInMode = MutableStateFlow(true)
    val isSignInMode = _isSignInMode.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun switchSignInMode() {
        _isSignInMode.value = !_isSignInMode.value
    }

    fun signIn(email: String, pass: String) {
        authRepository.signIn(email, pass).onEach { result ->
            _signInResult.value = result
        }.launchIn(viewModelScope)
    }
}