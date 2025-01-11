package com.cheba.mooddiary

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PositiveThoughtDao {

    @Query("SELECT * FROM positive_thoughts WHERE negativeThoughtId = :negativeThoughtId")
    fun getThoughtsForNegativeThought(negativeThoughtId: Int): Flow<List<PositiveThought>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThought(thought: PositiveThought)

    @Update
    suspend fun updateThought(thought: PositiveThought)

    @Delete
    suspend fun deleteThought(thought: PositiveThought)
}
