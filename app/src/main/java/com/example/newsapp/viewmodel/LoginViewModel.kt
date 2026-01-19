package com.example.newsapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _loginError = mutableStateOf<String?>(null)
    val loginError: State<String?> = _loginError

    private val dummyUsers = mapOf(
        "test@example.com" to "password",
        "parag@icici.com" to "password",
        "nilesh@icici.com" to "password",
        "sandeep@icici.com" to "password",
        "omkar@icici.com" to "password",
        "aditya@icici.com" to "password",
        "swati@icici.com" to "password"
    )

    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun onLoginClick(onLoginSuccess: () -> Unit) {
        if (_email.value.isNotBlank() && _password.value.isNotBlank()) {
            val enteredEmail = _email.value.lowercase()
            if (dummyUsers.containsKey(enteredEmail) && dummyUsers[enteredEmail] == _password.value) {
                _loginError.value = null
                onLoginSuccess()
            } else {
                _loginError.value = "Invalid credentials. Please try again."
            }
        } else {
            _loginError.value = "Email and password cannot be empty."
        }
    }

    fun clearError() {
        _loginError.value = null
    }
}
