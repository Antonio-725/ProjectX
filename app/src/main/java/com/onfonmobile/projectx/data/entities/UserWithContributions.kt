package com.onfonmobile.projectx.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithContributions(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",         // Refers to User's primary key
        entityColumn = "userId"

    // Matches Contribution's foreign key (corrected here)
    )
    val contributions: List<Contribution>
)
