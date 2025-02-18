package com.onfonmobile.projectx.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.onfonmobile.projectx.data.entities.User
import com.onfonmobile.projectx.data.entities.UserWithContributions

@Dao
interface UserDao {

    @Insert
    suspend fun insert(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>) // Correct method signature


    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT username FROM users WHERE username = :username LIMIT 1")
    suspend fun getUsername(username: String): String?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("UPDATE users SET password = :password WHERE username = :username")
    suspend fun updatePassword(username: String, password: String)

    @Transaction
    @Query("SELECT * FROM users")
    suspend fun getUsersWithContributions(): List<UserWithContributions>

    @Query("SELECT * FROM users")
    fun getAllUsersLiveData(): LiveData<List<User>>
}
//@Dao
//interface UserDao {
//    @Insert
//    suspend fun insert(user: User): Long
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertAll(users: List<User>)
//
//    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
//    suspend fun getUserByUsername(username: String): User?
//
//    @Query("SELECT * FROM users WHERE firestoreId = :firestoreId LIMIT 1")
//    suspend fun getUserByFirestoreId(firestoreId: String): User?
//
//    @Query("SELECT username FROM users WHERE username = :username LIMIT 1")
//    suspend fun getUsername(username: String): String?
//
//    @Query("SELECT * FROM users")
//    suspend fun getAllUsers(): List<User>
//
//    @Query("UPDATE users SET password = :password WHERE username = :username")
//    suspend fun updatePassword(username: String, password: String)
//
//    @Query("UPDATE users SET firestoreId = :firestoreId WHERE id = :localId")
//    suspend fun updateFirestoreId(localId: Long, firestoreId: String)
//
//    @Transaction
//    @Query("SELECT * FROM users")
//    suspend fun getUsersWithContributions(): List<UserWithContributions>
//
//    @Query("SELECT * FROM users")
//    fun getAllUsersLiveData(): LiveData<List<User>>
//
//    @Query("SELECT * FROM users WHERE id = :userId")
//    suspend fun getUserById(userId: Long): User?
//
//
//    // New method for handling sync conflicts
//    @Transaction
//    suspend fun upsertUser(user: User) {
//        val existingUser = user.firestoreId?.let { getUserByFirestoreId(it) }
//        if (existingUser == null) {
//            // New user - insert
//            insert(user)
//        } else {
//            // Existing user - update only if newer
//            if (user.lastUpdated > existingUser.lastUpdated) {
//                insertAll(listOf(user.copy(id = existingUser.id)))
//            }
//        }
//    }
//
//
//}
