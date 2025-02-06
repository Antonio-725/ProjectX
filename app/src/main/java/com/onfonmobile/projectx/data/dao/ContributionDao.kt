package com.onfonmobile.projectx.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.onfonmobile.projectx.data.entities.Contribution

@Dao
interface ContributionDao {

    @Insert
    suspend fun insertContribution(contribution: Contribution)

    @Query("SELECT * FROM contributions WHERE userId = :userId")
    suspend fun getContributionsByMember(userId: Long): List<Contribution>

    @Query("SELECT * FROM contributions WHERE userId = :userId")
    suspend fun getContributionsByUserId(userId: Long): List<Contribution>

    @Query("SELECT SUM(amount) FROM contributions WHERE userId = :userId")
    suspend fun getTotalContribution(userId: Long): Double

    @Query("SELECT SUM(amount) FROM contributions")
    suspend fun getTotalContribution(): Double

    @Query("SELECT SUM(amount) FROM contributions WHERE userId IN (:userIds)")
    suspend fun getTotalContributionForUsers(userIds: List<Long>): Double

    @Query("SELECT SUM(amount) FROM contributions")
    suspend fun getTotalContributionForAllUsers(): Double

    @Query("SELECT SUM(amount) FROM contributions WHERE userId = :userId")
    suspend fun getTotalContributionForCurrentUser(userId: Long): Double

    @Query("SELECT * FROM contributions WHERE userId = :userId")
    suspend fun getContributionsForUser(userId: Int): List<Contribution>

    @Query("SELECT * FROM contributions")
    suspend fun getAllContributions(): List<Contribution>
}
