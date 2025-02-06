package com.onfonmobile.projectx.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val password: String, // Store hashed password
    val role: String // "admin" or "user"
)
