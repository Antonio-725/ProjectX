package com.onfonmobile.projectx.ui.user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onfonmobile.projectx.data.entities.Contribution
import com.onfonmobile.projectx.data.entities.MonthlyContributionSummary
import com.onfonmobile.projectx.data.entities.User
import com.onfonmobile.projectx.data.repositories.ContributionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminViewModel(private val repository: ContributionRepository) : ViewModel() {

    // This LiveData will hold a map of userId -> total contribution
    private val _userContributions = MutableLiveData<Map<String, Double>>()
    val userContributions: LiveData<Map<String, Double>> get() = _userContributions

    // LiveData for a single user’s total contribution using repository’s live query.
    // (Assumes repository.getTotalContributionByUserLive(userId: String) is implemented.)
    fun observeTotalContribution(userId: String): LiveData<Double?> {
        return repository.getTotalContributionByUserLive(userId)
    }

    // For one-time retrieval (if needed), we still offer a suspend function.
    fun getTotalContribution(userId: String, onResult: (Double) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val databaseTotal = repository.getTotalContributionByUser(userId) ?: 0.0
                // Update cache for later use if needed
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

                    // Update the LiveData cache; this may be redundant if you observe live DB updates.
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
    }

    fun loadUsers(onResult: (List<User>) -> Unit) {
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

    // Instead of manually fetching all totals, we can observe live updates.
    // This function is provided if you need a one-time callback.
    fun getTotalContributionsForAllUsers(onResult: (Map<String, Double>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val contributions = repository.getTotalContributionsPerUser()
                val totalContributions = contributions.associate { it.userId to it.total }
                _userContributions.postValue(totalContributions)
                withContext(Dispatchers.Main) {
                    onResult(totalContributions)
                }
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Error fetching total contributions for all users", e)
                withContext(Dispatchers.Main) {
                    onResult(emptyMap())
                }
            }
        }
    }


    fun getMonthlyContributionSummary(onResult: (List<MonthlyContributionSummary>) -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
        try {
            val monthlyContributions = repository.getTotalContributionsByMonth()

            // Define target amounts for each month
            val monthlyTargets = mapOf(
                "01" to  2976.0, "02" to 8190.0, "03" to 13680.0, "04" to 19170.0,
                "05" to 24570.0, "06" to 29790.0, "07" to 35100.0, "08" to  40410.0,
                "09" to 45720.0, "10" to 51030.0, "11" to 56340.0, "12" to  61650.0
            )

            // Process contributions
            val summaryList = monthlyContributions.map { contribution ->
                val target = monthlyTargets[contribution.month] ?: 0.0
                val deficit = target - contribution.total
                val remark = if (deficit <= 0) "Met" else "Unmet"

                // Calculate percentage contribution towards target
                val percentage = if (target > 0) (contribution.total / target) * 100 else 0.0

                MonthlyContributionSummary(
                    month = getMonthName(contribution.month),
                    totalAmount = contribution.total,
                    deficit = if (deficit > 0) deficit else 0.0,
                    remark = remark,
                    percentage = percentage
                )
            }

            withContext(Dispatchers.Main) {
                onResult(summaryList)
            }
        } catch (e: Exception) {
            Log.e("AdminViewModel", "Error fetching monthly contributions", e)
            withContext(Dispatchers.Main) {
                onResult(emptyList())
            }
        }
    }
}
    private fun getMonthName(monthNumber: String): String {
        return when (monthNumber) {
            "01" -> "January"
            "02" -> "February"
            "03" -> "March"
            "04" -> "April"
            "05" -> "May"
            "06" -> "June"
            "07" -> "July"
            "08" -> "August"
            "09" -> "September"
            "10" -> "October"
            "11" -> "November"
            "12" -> "December"
            else -> "Unknown"
        }
    }
//    fun updateUsers(users: List<User>) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                users.forEach { repository.updateUser(it) }
//            } catch (e: Exception) {
//                Log.e("AdminViewModel", "Failed to update users", e)
//            }
//        }
//    }
fun updateUsers(users: List<User>, onComplete: () -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
        try {
            users.forEach { user ->
                repository.updateUser(user) // ✅ Use repository instead of database
            }
            withContext(Dispatchers.Main) {
                onComplete() // Notify UI after completion
            }
        } catch (e: Exception) {
            Log.e("AdminViewModel", "Failed to update users", e)
        }
    }
}
}






