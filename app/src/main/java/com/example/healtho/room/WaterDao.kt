package com.example.healtho.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.healtho.homescreens.models.Water
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {

    @Query("SELECT * FROM water_table WHERE timeStamp>:time ORDER BY timeStamp DESC")
    fun getAllAfterTime(time: Long): Flow<List<Water>>

    @Insert
    suspend fun insertWater(water: List<Water>)

    @Query("DELETE FROM water_table")
    suspend fun deleteAll()
}
