package com.onfonmobile.projectx.ui.user



import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.onfonmobile.projectx.data.AppDatabase
import com.onfonmobile.projectx.data.repositories.ContributionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ContributionRepository(AppDatabase.getDatabase(application))

    /**
     * Retrieves the total contribution for the given user ID.
     */
    fun getTotalContribution(userId: String, onResult: (Double) -> Unit) {
        Log.d("ProfileViewModel", "getTotalContribution called for userId: $userId")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("ProfileViewModel", "About to call repository.getTotalContributionByUser")
                val total = repository.getTotalContributionByUser(userId) ?: 0.0
                Log.d("ProfileViewModel", "Repository returned total: $total for userId: $userId")
                withContext(Dispatchers.Main) {
                    Log.d("ProfileViewModel", "Calling onResult callback with total: $total")
                    onResult(total)
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error fetching user contribution", e)
                withContext(Dispatchers.Main) {
                    onResult(0.0)
                }
            }
        }
    }
}
