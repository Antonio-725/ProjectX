package com.onfonmobile.projectx.ui.di

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _notificationCount = MutableLiveData<Int>(0)
    val notificationCount: LiveData<Int> get() = _notificationCount

    fun incrementNotificationCount() {
        _notificationCount.value = (_notificationCount.value ?: 0) + 1
    }

    fun resetNotificationCount() {
        _notificationCount.value = 0
    }
}