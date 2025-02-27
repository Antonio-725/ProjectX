package com.onfonmobile.projectx.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "contributions",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Contribution(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,          // Reference to User ID
    val amount: Double,        // Contribution amount
    val date: Long             // Date of contribution (timestamp)
)
