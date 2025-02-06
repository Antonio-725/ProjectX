package com.onfonmobile.projectx

import android.app.Application
import androidx.lifecycle.ViewModelProvider

class ProjectX : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any application-wide dependencies or configurations here.
        ViewModelProvider.AndroidViewModelFactory.getInstance(this)
    }
}
