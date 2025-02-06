package com.onfonmobile.projectx.ui.user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onfonmobile.projectx.data.entities.Contribution
import com.onfonmobile.projectx.data.entities.User
import com.onfonmobile.projectx.data.repositories.ContributionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminViewModel(private val repository: ContributionRepository) : ViewModel() {
    private val _userContributions = MutableLiveData<Map<Long, Double>>()
    val userContributions: LiveData<Map<Long, Double>> get() = _userContributions

    fun getTotalContribution(userId: Long, onResult: (Double) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Debug: Check if there's a cached value
                val cachedAmount = _userContributions.value?.get(userId)
                Log.d("AdminViewModel", "Cached amount for user $userId: $cachedAmount")

                // Get fresh total from database
                val databaseTotal = repository.getTotalContributionByUser(userId) ?: 0.0
                Log.d("AdminViewModel", "Database total for user $userId: $databaseTotal")

                // Get all contributions for verification
                val allContributions = repository.getContributionsByUser(userId)
                Log.d("AdminViewModel", "All contributions for user $userId: ${allContributions.joinToString { "${it.amount}" }}")

                val manualSum = allContributions.sumOf { it.amount }
                Log.d("AdminViewModel", "Manual sum for user $userId: $manualSum")

                // Update cache with verified amount
                val updatedContributions = _userContributions.value?.toMutableMap() ?: mutableMapOf()
                updatedContributions[userId] = databaseTotal
                _userContributions.postValue(updatedContributions)

                withContext(Dispatchers.Main) {
                    onResult(databaseTotal)
                }
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Error fetching contributions for userId: $userId", e)
                withContext(Dispatchers.Main) {
                    onResult(0.0)
                }
            }
        }
    }

    private var isSaving = false

    fun saveContribution(
        username: String,
        amount: Double,
        date: Long,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d("AdminViewModel", "saveContribution called - Username: $username, Amount: $amount")
        if (isSaving) {
            Log.d("AdminViewModel", "saveContribution already in progress, skipping")
            return
        }
        isSaving = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("AdminViewModel", "Fetching user by username: $username")
                val user = repository.getUserByUsername(username)
                if (user != null) {
                    Log.d("AdminViewModel", "User found - ID: ${user.id}")
                    val totalBefore = repository.getTotalContributionByUser(user.id) ?: 0.0
                    Log.d("AdminViewModel", "Total before saving: $totalBefore")

                    val contribution = Contribution(userId = user.id, amount = amount, date = date)
                    Log.d("AdminViewModel", "Inserting contribution: $contribution")
                    repository.insertContribution(contribution)

                    val totalAfter = repository.getTotalContributionByUser(user.id) ?: 0.0
                    Log.d("AdminViewModel", "Total after saving: $totalAfter")

                    val difference = totalAfter - totalBefore
                    Log.d("AdminViewModel", "Difference in total: $difference (expected: $amount)")

                    val updatedContributions = _userContributions.value?.toMutableMap() ?: mutableMapOf()
                    updatedContributions[user.id] = totalAfter
                    _userContributions.postValue(updatedContributions)

                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else {
                    Log.d("AdminViewModel", "User not found: $username")
                    withContext(Dispatchers.Main) {
                        onError("User not found")
                    }
                }
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Error saving contribution", e)
                withContext(Dispatchers.Main) {
                    onError("Failed to save contribution")
                }
            } finally {
                isSaving = false
                Log.d("AdminViewModel", "saveContribution completed")
            }
        }
    }    fun loadUsers(onResult: (List<User>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val users = repository.getAllUsers()
                withContext(Dispatchers.Main) {
                    onResult(users)
                }
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Error loading users", e)
                withContext(Dispatchers.Main) {
                    onResult(emptyList())
                }
            }
        }
    }
}
