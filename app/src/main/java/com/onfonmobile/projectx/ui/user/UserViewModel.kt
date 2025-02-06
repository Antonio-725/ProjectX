package com.onfonmobile.projectx.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onfonmobile.projectx.data.AppDatabase
import com.onfonmobile.projectx.data.entities.User
import kotlinx.coroutines.launch

class UserViewModel(private val appDatabase: AppDatabase) : ViewModel() {
    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    // Register a new user
    fun registerUser(username: String, password: String, role: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = User(username = username, password = password, role = role)
            try {
                appDatabase.userDao().insert(user)
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    // Login user by username and password
    fun loginUser(username: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = appDatabase.userDao().getUserByUsername(username)
            if (user != null && user.password == password) {
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }
    fun fetchUsername(username: String) {
        viewModelScope.launch {
            val fetchedUsername = appDatabase.userDao().getUsername(username)
            _username.postValue(fetchedUsername ?: "Unknown User")
        }
    }



    // Check if the user exists by username
    suspend fun getUserByUsername(username: String) = appDatabase.userDao().getUserByUsername(username)
}
