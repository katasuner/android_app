package com.cheba.mooddiary

import androidx.room.*

@Dao
interface NegativeThoughtDao {

    @Query("SELECT * FROM negative_thoughts WHERE eventId = :eventId")
    fun getThoughtsForEvent(eventId: Int): kotlinx.coroutines.flow.Flow<List<NegativeThought>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThought(thought: NegativeThought)

    @Update
    suspend fun updateThought(thought: NegativeThought)

    @Delete
    suspend fun deleteThought(thought: NegativeThought)
}

