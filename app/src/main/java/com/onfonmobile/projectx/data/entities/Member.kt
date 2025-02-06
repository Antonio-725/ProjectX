//package com.onfonmobile.projectx.data.entities
//
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//
//@Entity(tableName = "members")
//data class Member(
//    @PrimaryKey(autoGenerate = true) val id: Long = 0,
//    val name: String,
//    val role: String // "admin" or "user"
//)


package com.onfonmobile.projectx.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "members",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("userId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Member(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long, // Reference to the User entity
    val role: String // "admin" or "user"
)
