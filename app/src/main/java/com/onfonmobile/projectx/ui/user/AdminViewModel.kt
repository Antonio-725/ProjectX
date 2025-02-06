package com.onfonmobile.projectx.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onfonmobile.projectx.data.AppDatabase
import com.onfonmobile.projectx.data.entities.Contribution
import com.onfonmobile.projectx.data.entities.User
import com.onfonmobile.projectx.data.entities.UserWithContributions
import com.onfonmobile.projectx.data.repositories.ContributionRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminViewModel(private val repository: ContributionRepository) : ViewModel() {

    fun loadUsers(onResult: (List<com.onfonmobile.projectx.data.entities.User>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val users = repository.getAllUsers()
            withContext(Dispatchers.Main) {
                onResult(users)
            }
        }
    }

    fun saveContribution(
        username: String,
        amount: Double,
        date: Long,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getUserByUsername(username)
            if (user != null) {
                val contribution = Contribution(userId = user.id, amount = amount, date = date)
                repository.insertContribution(contribution)
                withContext(Dispatchers.Main) { onSuccess() }
            } else {
                withContext(Dispatchers.Main) { onError("User not found") }
            }
        }
    }
}

////
//class AdminViewModel(private val db: AppDatabase) : ViewModel() {
//
//    private val _usersWithContributions = MutableLiveData<List<UserWithContributions>>()
//    val usersWithContributions: LiveData<List<UserWithContributions>> get() = _usersWithContributions
//
//    // Method to load all users with their total contributions dynamically
//    fun loadUsersWithContributions(callback: (List<UserWithContributions>) -> Unit) {
//        viewModelScope.launch {
//            // Fetch all users
//            val users = db.userDao().getAllUsers()
//
//            // Fetch each user's contributions and calculate total dynamically
//            val usersWithContributions = users.map { user ->
//                val contributions = db.contributionDao().getContributionsForUser(user.id)
//                val total = contributions.sumOf { it.amount }
//                UserWithContributions(user, total)
//            }
//
//            _usersWithContributions.postValue(usersWithContributions)
//            callback(usersWithContributions)
//        }
//    }
//}
//
//// A data class to hold user and their total contribution
////data class UserWithContributions(val user: User, val totalContribution: Float)
////
////}