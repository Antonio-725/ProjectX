//package com.onfonmobile.projectx.data.entities
//
//import androidx.room.Entity
//import androidx.room.ForeignKey
//import androidx.room.PrimaryKey
//
//@Entity(
//    tableName = "contributions",
//    foreignKeys = [ForeignKey(
//        entity = Member::class,
//        parentColumns = arrayOf("id"),
//        childColumns = arrayOf("memberId"),
//        onDelete = ForeignKey.CASCADE
//    )]
//)
//data class Contribution(
//    @PrimaryKey(autoGenerate = true) val id: Long = 0,
//    val memberId: Long, // Reference to Member
//    val amount: Double,
//    val date: Long // Timestamp of the contribution
//)
//package com.onfonmobile.projectx.data.entities
//
//import androidx.room.Entity
//import androidx.room.ForeignKey
//import androidx.room.PrimaryKey
//
//@Entity(
//    tableName = "contributions",
//    foreignKeys = [ForeignKey(
//        entity = Member::class,
//        parentColumns = arrayOf("id"),
//        childColumns = arrayOf("memberId"),
//        onDelete = ForeignKey.CASCADE
//    )]
//)
//data class Contribution(
//    @PrimaryKey(autoGenerate = true) val id: Long = 0,
//    val memberId: Long, // Reference to Member
//    val amount: Double, // The amount contributed (equal to the day number)
//    val date: Long // The day number (e.g., 1 for day 1, 2 for day 2)
//)
package com.onfonmobile.projectx.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "contributions",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Contribution(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,          // Reference to User ID
    val amount: Double,        // Contribution amount
    val date: Long             // Date of contribution (timestamp)
)
