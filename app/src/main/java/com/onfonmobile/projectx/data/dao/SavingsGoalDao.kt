package com.onfonmobile.projectx.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.onfonmobile.projectx.data.entities.SavingsGoal

@Dao
interface SavingsGoalDao {

    @Insert
    suspend fun insert(savingsGoal: SavingsGoal)

    @Query("SELECT * FROM savings_goals WHERE id = :id LIMIT 1")
    suspend fun getGoalById(id: Long): SavingsGoal?

    @Query("SELECT * FROM savings_goals")
    suspend fun getAllSavingsGoals(): List<SavingsGoal>

    @Query("UPDATE savings_goals SET currentAmount = :currentAmount WHERE id = :id")
    suspend fun updateSavingsGoalAmount(id: Long, currentAmount: Double)
}
