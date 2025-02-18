package com.onfonmobile.projectx.Firestore.Helpers.Worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
//import com.onfonmobile.projectx.Firestore.Helpers.FirestoreHelper
import com.onfonmobile.projectx.Firestore.Helpers.Utils.NetworkUtils
import com.onfonmobile.projectx.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val db = AppDatabase.getDatabase(appContext)
    private val firestore = FirebaseFirestore.getInstance()
    private val firestoreHelper = FirestoreHelper.getInstance()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d("SyncWorker", "Starting sync process...")

            // Fetch data from Room
            val users = db.userDao().getAllUsers()
            val contributions = db.contributionDao().getAllContributions()

            Log.d("SyncWorker", "Fetched ${users.size} users and ${contributions.size} contributions from Room.")

            // Check internet before syncing
            if (!NetworkUtils.isOnline(applicationContext)) {
                Log.w("SyncWorker", "No internet connection. Retrying sync later.")
                return@withContext Result.retry()
            }

            // Sync to Firestore
            if (users.isNotEmpty()) {
                firestoreHelper.syncUsers(users)
                Log.d("SyncWorker", "Users synced successfully.")
            }

            if (contributions.isNotEmpty()) {
                firestoreHelper.syncContributions(contributions)
                Log.d("SyncWorker", "Contributions synced successfully.")
            }

            // Fetch from Firestore to Room
            firestoreHelper.fetchUsers(db.userDao())
            firestoreHelper.fetchContributions(db.contributionDao())
            Log.d("SyncWorker", "Data fetched from Firestore and saved to Room.")

            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Sync failed", e)
            if (runAttemptCount < 3) {
                Log.w("SyncWorker", "Retrying sync (attempt $runAttemptCount)...")
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }


    companion object {
        // Work name for unique work
        const val WORK_NAME = "sync_worker"

        // Minimum interval between syncs (in minutes)
        //const val SYNC_INTERVAL = 15L
        const val SYNC_INTERVAL = 5L
    }
}