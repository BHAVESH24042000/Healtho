package com.example.healtho.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.healtho.homescreens.models.Water
import com.example.healtho.homescreens.models.Workout
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Query("SELECT * FROM workout_table WHERE timeStamp>:time ORDER BY timeStamp DESC")
    fun getAllAfterTime(time: Long): Flow<List<Workout>>

    @Insert
    suspend fun insertWorkout(water: List<Workout>)

    @Query("DELETE FROM workout_table")
    suspend fun deleteAll()
}
