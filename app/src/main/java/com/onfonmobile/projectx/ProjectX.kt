package com.onfonmobile.projectx

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.work.Configuration
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.initialize
import com.onfonmobile.projectx.Firestore.Helpers.Worker.SyncScheduler
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

class ProjectX : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseMessaging.getInstance().subscribeToTopic("all")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Subscribed to 'all' topic")
                } else {
                    Log.e("FCM", "Subscription failed", task.exception)
                }
            }


        try {
            // Initialize Firebase
            Firebase.initialize(this)
            Log.d("ProjectX", "Firebase initialized successfully")

            // Initialize ViewModelFactory
            ViewModelProvider.AndroidViewModelFactory.getInstance(this)
            Log.d("ProjectX", "ViewModelFactory initialized successfully")

            // Initialize Firestore with offline caching
            val firestore = FirebaseFirestore.getInstance()
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true) // Enables offline caching
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED) // Optional: Set unlimited cache size
                .build()
            firestore.firestoreSettings = settings
            Log.d("ProjectX", "Firestore initialized with offline persistence")

            // Initialize WorkManager with logging
            val workManagerConfig = Configuration.Builder()
                .setMinimumLoggingLevel(Log.DEBUG)
                .build()
            androidx.work.WorkManager.initialize(this, workManagerConfig)
            Log.d("ProjectX", "WorkManager initialized successfully")

            // Schedule periodic sync
            SyncScheduler.scheduleSync(this)
            Log.d("ProjectX", "Periodic sync scheduled successfully")

        } catch (e: Exception) {
            Log.e("ProjectX", "Error during application initialization", e)
        }
    }

    companion object {
        private const val TAG = "ProjectX"
    }
}

