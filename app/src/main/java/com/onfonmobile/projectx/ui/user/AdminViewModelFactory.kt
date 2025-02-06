package com.onfonmobile.projectx.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.onfonmobile.projectx.data.AppDatabase
import com.onfonmobile.projectx.data.repositories.ContributionRepository

class AdminViewModelFactory(private val db: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminViewModel(ContributionRepository(db)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}