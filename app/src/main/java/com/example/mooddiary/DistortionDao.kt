package com.example.mooddiary

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DistortionDao {
    @Query("SELECT * FROM distortions WHERE negativeThoughtId = :negativeThoughtId")
    fun getDistortionsForNegativeThought(negativeThoughtId: Int): Flow<List<Distortion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDistortions(distortions: List<Distortion>)

    @Update
    suspend fun updateDistortion(distortion: Distortion)
}
