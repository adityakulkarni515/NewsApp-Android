package com.example.newsapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State

/**
 * ViewModel for the Login screen.
 * It handles the state and business logic for user authentication.
 *
 * @property email The current value of the email/mobile input field.
 * @property password The current value of the password input field.
 * @property loginError State to hold a potential error message from a failed login attempt.
 */
@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    // A private mutable state for the email/mobile input.
    private val _email = mutableStateOf("")
    // An immutable public State for the email/mobile input, to be observed by the UI.
    val email: State<String> = _email

    // A private mutable state for the password input.
    private val _password = mutableStateOf("")
    // An immutable public State for the password input.
    val password: State<String> = _password

    // A private mutable state for any login error messages.
    private val _loginError = mutableStateOf<String?>(null)
    // An immutable public State for login errors.
    val loginError: State<String?> = _loginError

    /**
     * A map of hardcoded dummy user credentials for demonstration purposes.
     * Keys are email addresses (lowercase for case-insensitive matching) and values are passwords.
     */
    private val dummyUsers = mapOf(
        "test@example.com" to "password",
        "parag@icici.com" to "password",
        "nilesh@icici.com" to "password",
        "sandeep@icici.com" to "password",
        "omkar@icici.com" to "password",
        "aditya@icici.com" to "password",
        "swati@icici.com" to "password"
    )

    /**
     * Updates the email/mobile state when the user types in the input field.
     * @param value The new value from the text field.
     */
    fun onEmailChange(value: String) {
        _email.value = value
    }

    /**
     * Updates the password state when the user types in the input field.
     * @param value The new value from the text field.
     */
    fun onPasswordChange(value: String) {
        _password.value = value
    }

    /**
     * Handles the login button click.
     * It validates the credentials and either navigates on success or shows an error.
     * @param onLoginSuccess A callback function to be invoked when login is successful.
     */
    fun onLoginClick(onLoginSuccess: () -> Unit) {
        // Simple validation to ensure email and password fields are not empty.
        if (_email.value.isNotBlank() && _password.value.isNotBlank()) {
            // Get the entered email and convert it to lowercase for case-insensitive comparison.
            val enteredEmail = _email.value.lowercase()
            // Check if the entered email exists in the dummyUsers map and if the password matches.
            if (dummyUsers.containsKey(enteredEmail) && dummyUsers[enteredEmail] == _password.value) {
                // If credentials are valid, clear any previous error.
                _loginError.value = null
                // Invoke the success callback.
                onLoginSuccess()
            } else {
                // If credentials are invalid, set an appropriate error message.
                _loginError.value = "Invalid credentials. Please try again."
            }
        } else {
            // If either field is empty, set an error message.
            _loginError.value = "Email and password cannot be empty."
        }
    }

    /**
     * Clears the current login error message.
     * This is typically called when the error dialog is dismissed.
     */
    fun clearError() {
        _loginError.value = null
    }
}
