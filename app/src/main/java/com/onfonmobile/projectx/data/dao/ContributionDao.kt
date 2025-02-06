package com.onfonmobile.projectx.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.onfonmobile.projectx.data.entities.Contribution

@Dao
interface ContributionDao {

   // @Insert
    //suspend fun insertContribution(contribution: Contribution)
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insertContribution(contribution: Contribution)


   // @Query("SELECT * FROM contributions WHERE userId = :userId")
   // suspend fun getContributionsByUserId(userId: Long): List<Contribution>

    @Query("SELECT DISTINCT * FROM contributions WHERE userId = :userId")
    suspend fun getContributionsByUserId(userId: Long): List<Contribution>



    @Query("SELECT SUM(amount) FROM contributions WHERE userId = :userId")
    suspend fun getTotalContributionByUser(userId: Long): Double?

    @Query("SELECT * FROM contributions")
    suspend fun getAllContributions(): List<Contribution>
}
