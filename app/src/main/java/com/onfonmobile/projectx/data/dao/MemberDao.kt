package com.onfonmobile.projectx.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.onfonmobile.projectx.data.entities.Member

@Dao
interface MemberDao {

    @Insert
    suspend fun insert(member: Member)

    @Query("SELECT * FROM members WHERE id = :id LIMIT 1")
    suspend fun getMemberById(id: Long): Member?

    @Query("SELECT * FROM members")
    suspend fun getAllMembers(): List<Member>

    @Query("DELETE FROM members WHERE id = :id")
    suspend fun deleteMemberById(id: Long)
}
