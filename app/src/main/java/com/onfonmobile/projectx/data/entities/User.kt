package com.onfonmobile.projectx.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class User(
   // @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val username: String,
    val password: String, // Store hashed password
    var role: String // "admin" or "user"
)
