package com.onfonmobile.projectx.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.onfonmobile.projectx.data.entities.User
import com.onfonmobile.projectx.data.entities.UserWithContributions

@Dao
interface UserDao {

    @Insert
    suspend fun insert(user: User)

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
}
