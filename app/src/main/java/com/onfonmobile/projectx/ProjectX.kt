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

class ProjectX : Application() {
    override fun onCreate() {
        super.onCreate()

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

//package com.onfonmobile.projectx
//
//import android.app.Application
//import android.util.Log
//import androidx.lifecycle.ViewModelProvider
//import androidx.work.*
//import com.google.firebase.BuildConfig
//import com.google.firebase.Firebase
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.FirebaseFirestoreSettings
//import com.google.firebase.initialize
//import com.onfonmobile.projectx.Firestore.Helpers.Worker.SyncScheduler
//import com.onfonmobile.projectx.Firestore.Helpers.Worker.SyncWorker
//import java.util.concurrent.TimeUnit
//
//class ProjectX : Application(), Configuration.Provider {
//    override fun onCreate() {
//        super.onCreate()
//
//        initializeComponents()
//    }
//
//    private fun initializeComponents() {
//        initializeFirebase()
//        initializeViewModelFactory()
//        initializeFirestore()
//        schedulePeriodicSync()
//    }
//
//    private fun initializeFirebase() {
//        try {
//            Firebase.initialize(this)
//            Log.d(TAG, "Firebase initialized successfully")
//        } catch (e: Exception) {
//            Log.e(TAG, "Failed to initialize Firebase", e)
//            // Consider implementing a crash reporting mechanism here
//        }
//    }
//
//    private fun initializeViewModelFactory() {
//        try {
//            ViewModelProvider.AndroidViewModelFactory.getInstance(this)
//            Log.d(TAG, "ViewModelFactory initialized successfully")
//        } catch (e: Exception) {
//            Log.e(TAG, "Failed to initialize ViewModelFactory", e)
//        }
//    }
//
//    private fun initializeFirestore() {
//        try {
//            val firestore = FirebaseFirestore.getInstance()
//            val settings = FirebaseFirestoreSettings.Builder()
//                .setPersistenceEnabled(true)
//                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
//                .build()
//            firestore.firestoreSettings = settings
//            Log.d(TAG, "Firestore initialized with offline persistence")
//        } catch (e: Exception) {
//            Log.e(TAG, "Failed to initialize Firestore", e)
//        }
//    }
//
//    private fun schedulePeriodicSync() {
//        try {
//            val constraints = Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .setRequiresBatteryNotLow(true)
//                .build()
//
//            val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
//                SyncWorker.SYNC_INTERVAL,
//                TimeUnit.MINUTES
//            )
//                .setConstraints(constraints)
//                .setBackoffCriteria(
//                    BackoffPolicy.EXPONENTIAL,
//                    WorkRequest.MIN_BACKOFF_MILLIS,
//                    TimeUnit.MILLISECONDS
//                )
//                .build()
//
//            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
//                SyncWorker.WORK_NAME,
//                ExistingPeriodicWorkPolicy.KEEP,
//                syncRequest
//            )
//
//            Log.d(TAG, "Periodic sync scheduled successfully")
//        } catch (e: Exception) {
//            Log.e(TAG, "Failed to schedule periodic sync", e)
//        }
//    }
//
//    // Implement Configuration.Provider to provide custom WorkManager configuration
//    override fun getWorkManagerConfiguration(): Configuration {
//        return Configuration.Builder()
//            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) Log.DEBUG else Log.ERROR)
//            .setWorkerFactory(WorkerFactory.getDefaultWorkerFactory())
//            .build()
//    }
//
//    companion object {
//        private const val TAG = "ProjectX"
//    }
//}