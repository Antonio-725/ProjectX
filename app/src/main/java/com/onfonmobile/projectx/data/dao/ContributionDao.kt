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

@Dao
interface ContributionDao {

   // @Insert
    //suspend fun insertContribution(contribution: Contribution)
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insertContribution(contribution: Contribution)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contributions: List<Contribution>) // Correct method signature


    // @Query("SELECT * FROM contributions WHERE userId = :userId")
   // suspend fun getContributionsByUserId(userId: Long): List<Contribution>

    @Query("SELECT DISTINCT * FROM contributions WHERE userId = :userId")
    suspend fun getContributionsByUserId(userId: String): List<Contribution>


    @Query("SELECT SUM(amount) FROM contributions WHERE userId = :userId")
    suspend fun getTotalContributionByUser(userId: String): Double?

    // New method to get total contributions for all users
    @Query("SELECT userId, SUM(amount) as total FROM contributions GROUP BY userId")
    suspend fun getTotalContributionsPerUser(): List<UserTotalContribution>

    @Query("SELECT * FROM contributions")
    fun getAllContributionsLiveData(): LiveData<List<Contribution>>  // This is the LiveData version



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
//@Dao
//interface ContributionDao {
//
//    // Insert a single contribution
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertContribution(contribution: Contribution)
//
//    // Insert multiple contributions
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertAll(contributions: List<Contribution>)
//
//    // Get contributions by user ID (using String for userId)
//    @Query("SELECT DISTINCT * FROM contributions WHERE userId = :userId")
//    suspend fun getContributionsByUserId(userId: String): List<Contribution>
//
//    // Get total contributions for a specific user (using String for userId)
//    @Query("SELECT SUM(amount) FROM contributions WHERE userId = :userId")
//    suspend fun getTotalContributionByUser(userId: String): Double?
//
//    // Get total contributions for all users (grouped by userId)
////    @Query("SELECT userId, SUM(amount) as total FROM contributions GROUP BY userId")
////    suspend fun getTotalContributionsPerUser(): List<UserTotalContribution>
//
//        @Query("SELECT userId, SUM(amount) as total FROM contributions GROUP BY userId")
//        suspend fun getTotalContributionsPerUser(): List<UserTotalContribution>
//
//
//    // Get all contributions as LiveData
//    @Query("SELECT * FROM contributions")
//    fun getAllContributionsLiveData(): LiveData<List<Contribution>>
//
//    // Get all contributions as a list
//    @Query("SELECT * FROM contributions")
//    suspend fun getAllContributions(): List<Contribution>
//
//    // Get total contributions grouped by month
//    @Query("""
//        SELECT strftime('%m', datetime(date / 1000, 'unixepoch')) AS month,
//               SUM(amount) AS total
//        FROM contributions
//        GROUP BY month
//    """)
//    suspend fun getTotalContributionsByMonth(): List<MonthlyContribution>
//}
//
