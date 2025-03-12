package com.onfonmobile.projectx.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.onfonmobile.projectx.data.dao.ContributionDao
import com.onfonmobile.projectx.data.dao.MemberDao
import com.onfonmobile.projectx.data.dao.SavingsGoalDao
import com.onfonmobile.projectx.data.dao.UserDao
import com.onfonmobile.projectx.data.entities.Contribution
import com.onfonmobile.projectx.data.entities.Member
import com.onfonmobile.projectx.data.entities.SavingsGoal
import com.onfonmobile.projectx.data.entities.User

@Database(entities = [Member::class, Contribution::class, SavingsGoal::class, User::class], version = 3, exportSchema = false)  // ✅ Updated version to 2
abstract class AppDatabase : RoomDatabase() {

    abstract fun memberDao(): MemberDao
    abstract fun contributionDao(): ContributionDao
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "savings_app_db"
                )
                    .fallbackToDestructiveMigration() // ✅ Add this line
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

