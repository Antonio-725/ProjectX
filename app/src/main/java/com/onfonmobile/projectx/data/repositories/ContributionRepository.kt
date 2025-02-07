package com.onfonmobile.projectx.data.repositories
import android.util.Log
import com.onfonmobile.projectx.data.AppDatabase
import com.onfonmobile.projectx.data.entities.Contribution
import com.onfonmobile.projectx.data.entities.User
import com.onfonmobile.projectx.data.entities.UserTotalContribution
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ContributionRepository(private val db: AppDatabase) {
    private val userDao = db.userDao()
    private val contributionDao = db.contributionDao()
    private val mutex = Mutex()


    suspend fun getAllUsers(): List<User> = userDao.getAllUsers()

    suspend fun getUserByUsername(username: String): User? =
        userDao.getUserByUsername(username)


    suspend fun getTotalContributionByUser(userId: Long): Double? {
        val total = contributionDao.getTotalContributionByUser(userId)
        Log.d("ContributionRepository", "Getting total for user $userId: $total")
        return total
    }

suspend fun insertContribution(contribution: Contribution) {
    mutex.withLock {
        val existingContributions = contributionDao.getContributionsByUserId(contribution.userId)
        val isDuplicate = existingContributions.any {
            it.amount == contribution.amount && it.date == contribution.date
        }
        if (!isDuplicate) {
            Log.d("ContributionRepository", "Inserting contribution: $contribution")
            contributionDao.insertContribution(contribution)
        } else {
            Log.d("ContributionRepository", "Duplicate contribution found, skipping insertion: $contribution")
        }
    }
}
    
    suspend fun getContributionsByUser(userId: Long): List<Contribution> {
        val contributions = contributionDao.getContributionsByUserId(userId)
        Log.d("ContributionRepository", "Getting all contributions for user $userId: ${contributions.joinToString { "${it.amount}" }}")
        return contributions
    }
    suspend fun getTotalContributionsPerUser(): List<UserTotalContribution> {
        return contributionDao.getTotalContributionsPerUser()
    }


}
