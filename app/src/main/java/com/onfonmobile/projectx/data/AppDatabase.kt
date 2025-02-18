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


//@Database(entities = [Member::class, Contribution::class, SavingsGoal::class, User::class], version = 2, exportSchema = false)
//abstract class AppDatabase : RoomDatabase() {
//
//    abstract fun memberDao(): MemberDao
//    abstract fun contributionDao(): ContributionDao
//    abstract fun savingsGoalDao(): SavingsGoalDao
//    abstract fun userDao(): UserDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: AppDatabase? = null
//
//        private val MIGRATION_1_2 = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                // Create new users table with UUID as primary key
//                database.execSQL(
//                    """
//                    CREATE TABLE users_new (
//                        id TEXT PRIMARY KEY NOT NULL,
//                        username TEXT NOT NULL,
//                        password TEXT NOT NULL,
//                        role TEXT NOT NULL
//                    )
//                    """.trimIndent()
//                )
//
//                // Copy existing data and generate UUIDs
//                database.execSQL(
//                    """
//                    INSERT INTO users_new (id, username, password, role)
//                    SELECT LOWER(HEX(RANDOMBLOB(16))), username, password, role FROM users
//                    """.trimIndent()
//                )
//
//                // Drop old table and rename new one
//                database.execSQL("DROP TABLE users")
//                database.execSQL("ALTER TABLE users_new RENAME TO users")
//            }
//        }
//
//        fun getDatabase(context: Context): AppDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "savings_app_db"
//                )
//                    .addMigrations(MIGRATION_1_2) // ✅ Use migration instead of destructive migration
//                    .build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}
