
package com.onfonmobile.projectx.Firestore.Helpers;
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.onfonmobile.projectx.data.dao.ContributionDao
import com.onfonmobile.projectx.data.dao.UserDao
import com.onfonmobile.projectx.data.entities.Contribution
import com.onfonmobile.projectx.data.entities.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
//
//import android.util.Log
//import com.google.firebase.firestore.FirebaseFirestore
//import com.onfonmobile.projectx.data.dao.ContributionDao
//import com.onfonmobile.projectx.data.dao.UserDao
//import com.onfonmobile.projectx.data.entities.Contribution
//import com.onfonmobile.projectx.data.entities.User
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.launch
//
//class FirestoreHelper {
//    private val db = FirebaseFirestore.getInstance()
//    private val scope = CoroutineScope(Dispatchers.IO + Job())
//
//    fun syncUsers(users: List<User>) {
//        for (user in users) {
//            val docRef = db.collection("users").document(user.id.toString())
//            docRef.get()
//                .addOnSuccessListener { document ->
//                    if (!document.exists()) {
//                        docRef.set(user)
//                    }
//                }
//                .addOnFailureListener { e ->
//                    Log.e("FirestoreHelper", "Error syncing user: ${e.message}")
//                }
//        }
//    }
//
//    fun syncContributions(contributions: List<Contribution>) {
//        for (contribution in contributions) {
//            val docRef = db.collection("contributions").document(contribution.id.toString())
//            docRef.get()
//                .addOnSuccessListener { document ->
//                    if (!document.exists()) {
//                        docRef.set(contribution)
//                    }
//                }
//                .addOnFailureListener { e ->
//                    Log.e("FirestoreHelper", "Error syncing contribution: ${e.message}")
//                }
//        }
//    }
//
//    fun fetchUsers(userDao: UserDao) {
//        db.collection("users").get()
//            .addOnSuccessListener { querySnapshot ->
//                val users = querySnapshot.documents.mapNotNull {
//                    it.toObject(User::class.java)
//                }
//
//                if (users.isNotEmpty()) {
//                    scope.launch {
//                        try {
//                            userDao.insertAll(users)
//                        } catch (e: Exception) {
//                            Log.e("FirestoreHelper", "Error inserting users to Room: ${e.message}")
//                        }
//                    }
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("FirestoreHelper", "Error fetching users: ${e.message}")
//            }
//    }
//
//    fun fetchContributions(contributionDao: ContributionDao) {
//        db.collection("contributions").get()
//            .addOnSuccessListener { querySnapshot ->
//                val contributions = querySnapshot.documents.mapNotNull {
//                    it.toObject(Contribution::class.java)
//                }
//
//                if (contributions.isNotEmpty()) {
//                    scope.launch {
//                        try {
//                            contributionDao.insertAll(contributions)
//                        } catch (e: Exception) {
//                            Log.e("FirestoreHelper", "Error inserting contributions to Room: ${e.message}")
//                        }
//                    }
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("FirestoreHelper", "Error fetching contributions: ${e.message}")
//            }
//    }
//
//    companion object {
//        @Volatile
//        private var instance: FirestoreHelper? = null
//
//        fun getInstance(): FirestoreHelper {
//            return instance ?: synchronized(this) {
//                instance ?: FirestoreHelper().also { instance = it }
//            }
//        }
//    }
//}

class FirestoreHelper {
    private val db = FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    fun syncUsers(users: List<User>) {
        Log.d("FirestoreHelper", "Starting user sync with ${users.size} users")
        for (user in users) {
            val userData = hashMapOf(
                "id" to user.id,
                "username" to user.username,
                "password" to user.password,
                "role" to user.role,
                "timestamp" to FieldValue.serverTimestamp()
            )
            // Use the UUID string as the document ID
            val docRef = db.collection("users").document(user.id)
            docRef.set(userData)
                .addOnSuccessListener {
                    Log.d("FirestoreHelper", "Successfully synced user ${user.id}")
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreHelper", "Error syncing user ${user.id}: ${e.message}")
                }
        }
    }


    fun syncContributions(contributions: List<Contribution>) {
        Log.d("FirestoreHelper", "Starting contribution sync with ${contributions.size} contributions")
        for (contribution in contributions) {
            val contributionData = hashMapOf(
                "id" to contribution.id,
                "userId" to contribution.userId,
                "amount" to contribution.amount,
                "date" to contribution.date,
                "timestamp" to FieldValue.serverTimestamp()
            )

            val docRef = db.collection("contributions").document(contribution.id.toString())
            docRef.set(contributionData)
                .addOnSuccessListener {
                    Log.d("FirestoreHelper", "Successfully synced contribution ${contribution.id}")
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreHelper", "Error syncing contribution ${contribution.id}: ${e.message}")
                }
        }
    }

    fun fetchUsers(userDao: UserDao) {
        Log.d("FirestoreHelper", "Starting user fetch from Firestore")
        db.collection("users").get()
            .addOnSuccessListener { querySnapshot ->
                Log.d("FirestoreHelper", "Fetched ${querySnapshot.size()} users from Firestore")
                val users = querySnapshot.documents.mapNotNull { doc ->
                    try {
                        User(
                            id = doc.getString("id") ?: return@mapNotNull null,
                            username = doc.getString("username") ?: return@mapNotNull null,
                            password = doc.getString("password") ?: return@mapNotNull null,
                            role = doc.getString("role") ?: return@mapNotNull null
                        )
                    } catch (e: Exception) {
                        Log.e("FirestoreHelper", "Error mapping user document: ${e.message}")
                        null
                    }
                }

                if (users.isNotEmpty()) {
                    scope.launch {
                        try {
                            userDao.insertAll(users)
                            Log.d("FirestoreHelper", "Successfully saved ${users.size} users to Room")
                        } catch (e: Exception) {
                            Log.e("FirestoreHelper", "Error inserting users to Room: ${e.message}")
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreHelper", "Error fetching users: ${e.message}")
            }
    }

    fun fetchContributions(contributionDao: ContributionDao) {
        Log.d("FirestoreHelper", "Starting contribution fetch from Firestore")
        db.collection("contributions").get()
            .addOnSuccessListener { querySnapshot ->
                Log.d("FirestoreHelper", "Fetched ${querySnapshot.size()} contributions from Firestore")
                val contributions = querySnapshot.documents.mapNotNull { doc ->
                    try {
                        Contribution(
                            id = doc.getLong("id") ?: return@mapNotNull null,
                            userId = doc.getString("userId") ?: return@mapNotNull null,
                            amount = doc.getDouble("amount") ?: return@mapNotNull null,
                            date = doc.getLong("date") ?: return@mapNotNull null
                        )
                    } catch (e: Exception) {
                        Log.e("FirestoreHelper", "Error mapping contribution document: ${e.message}")
                        null
                    }
                }

                if (contributions.isNotEmpty()) {
                    scope.launch {
                        try {
                            contributionDao.insertAll(contributions)
                            Log.d("FirestoreHelper", "Successfully saved ${contributions.size} contributions to Room")
                        } catch (e: Exception) {
                            Log.e("FirestoreHelper", "Error inserting contributions to Room: ${e.message}")
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreHelper", "Error fetching contributions: ${e.message}")
            }
    }

    companion object {
        @Volatile
        private var instance: FirestoreHelper? = null

        fun getInstance(): FirestoreHelper {
            return instance ?: synchronized(this) {
                instance ?: FirestoreHelper().also { instance = it }
            }
        }
    }
}