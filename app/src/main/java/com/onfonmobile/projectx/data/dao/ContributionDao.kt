package com.onfonmobile.projectx.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.onfonmobile.projectx.data.entities.Contribution
import com.onfonmobile.projectx.data.entities.MonthlyContribution
import com.onfonmobile.projectx.data.entities.UserTotalContribution
//import com.onfonmobile.projectx.data.models.MonthlyContribution

//@Dao
//interface ContributionDao {
//
//   // @Insert
//    //suspend fun insertContribution(contribution: Contribution)
//   @Insert(onConflict = OnConflictStrategy.REPLACE)
//   suspend fun insertContribution(contribution: Contribution)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertAll(contributions: List<Contribution>) // Correct method signature
//
//
//    // @Query("SELECT * FROM contributions WHERE userId = :userId")
//   // suspend fun getContributionsByUserId(userId: Long): List<Contribution>
//
//    @Query("SELECT DISTINCT * FROM contributions WHERE userId = :userId")
//    suspend fun getContributionsByUserId(userId: String): List<Contribution>
//
//
//    @Query("SELECT SUM(amount) FROM contributions WHERE userId = :userId")
//    suspend fun getTotalContributionByUser(userId: String): Double?
//
//    // New method to get total contributions for all users
//    @Query("SELECT userId, SUM(amount) as total FROM contributions GROUP BY userId")
//    suspend fun getTotalContributionsPerUser(): List<UserTotalContribution>
//
//    @Query("SELECT * FROM contributions")
//    fun getAllContributionsLiveData(): LiveData<List<Contribution>>  // This is the LiveData version
//
//
//
//    @Query("SELECT * FROM contributions")
//    suspend fun getAllContributions(): List<Contribution>
//
//    @Query("""
//    SELECT strftime('%m', datetime(date / 1000, 'unixepoch')) AS month,
//           SUM(amount) AS total
//    FROM contributions
//    GROUP BY month
//""")
//    suspend fun getTotalContributionsByMonth(): List<MonthlyContribution>
//
//}
@Dao
interface ContributionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContribution(contribution: Contribution)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contributions: List<Contribution>)

    @Query("SELECT * FROM contributions WHERE userId = :userId")
    fun getContributionsByUserIdLive(userId: String): LiveData<List<Contribution>> // For UI updates

    @Query("SELECT * FROM contributions WHERE userId = :userId")
    suspend fun getContributionsByUserId(userId: String): List<Contribution> // For suspend functions

    @Query("SELECT SUM(amount) FROM contributions WHERE userId = :userId")
    fun getTotalContributionByUserLive(userId: String): LiveData<Double?> // LiveData version

    @Query("SELECT SUM(amount) FROM contributions WHERE userId = :userId")
    suspend fun getTotalContributionByUser(userId: String): Double? // Suspend version

    @Query("SELECT userId, SUM(amount) as total FROM contributions GROUP BY userId")
    suspend fun getTotalContributionsPerUser(): List<UserTotalContribution>

    @Query("SELECT * FROM contributions")
    fun getAllContributionsLiveData(): LiveData<List<Contribution>>

    @Query("SELECT * FROM contributions")
    suspend fun getAllContributions(): List<Contribution>

    @Query("""
        SELECT strftime('%m', datetime(date / 1000, 'unixepoch')) AS month,
               SUM(amount) AS total
        FROM contributions
        GROUP BY month
    """)
    suspend fun getTotalContributionsByMonth(): List<MonthlyContribution>
}
