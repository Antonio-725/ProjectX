package com.onfonmobile.projectx.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goals")
data class SavingsGoal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetAmount: Double,
    val currentAmount: Double = 0.0, // Amount saved so far
    val targetDate: Long // The target date for achieving the goal (timestamp)
)
