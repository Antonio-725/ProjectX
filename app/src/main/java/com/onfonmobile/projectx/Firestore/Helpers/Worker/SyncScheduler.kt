package com.onfonmobile.projectx.Firestore.Helpers.Worker;

import java.util.concurrent.TimeUnit;
import android.content.Context
import androidx.work.Constraints

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager

//import androidx.work.*


object SyncScheduler {
//    fun scheduleSync(context: android.content.Context) {
//        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(5, TimeUnit.SECONDS)
//            .setConstraints(
//                Constraints.Builder()
//                    .setRequiredNetworkType(NetworkType.CONNECTED)
//                    .build()
//            )
//            .build()
//
//        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
//            "SyncWorker",
//            ExistingPeriodicWorkPolicy.UPDATE,  // <-- Use UPDATE instead of KEEP
//            syncRequest
//        )
//
//    }
fun scheduleSync(context: Context) {
    val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
        .setInitialDelay(3, TimeUnit.SECONDS)  // <-- 3 seconds delay for testing
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .build()

    WorkManager.getInstance(context).enqueue(syncRequest)
}

    // Add this to SyncScheduler
    fun triggerImmediateSync(context: Context) {
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueue(syncRequest)
    }

}
