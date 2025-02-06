package com.onfonmobile.projectx.data.repositories
import com.onfonmobile.projectx.data.AppDatabase
import com.onfonmobile.projectx.data.entities.Contribution
import com.onfonmobile.projectx.data.entities.User

class ContributionRepository(private val db: AppDatabase) {

    private val userDao = db.userDao()
    private val contributionDao = db.contributionDao()

    suspend fun getAllUsers(): List<User> = userDao.getAllUsers()

    suspend fun getUserByUsername(username: String): User? =
        userDao.getUserByUsername(username)

    suspend fun insertContribution(contribution: Contribution) {
        contributionDao.insertContribution(contribution)
    }
}
