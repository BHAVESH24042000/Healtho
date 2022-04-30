package com.example.healtho.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.healtho.homescreens.models.Sleep
import com.example.healtho.homescreens.models.Water
import com.example.healtho.homescreens.models.Workout


@Database(
    entities = [Water::class, Sleep::class, Workout::class],
    version = 1,
    exportSchema = false
)
abstract class HealthoDB : RoomDatabase() {
    abstract fun getWaterDao(): WaterDao
    abstract fun getSleepDao(): SleepDao
    abstract fun getWorkoutDao(): WorkoutDao
}
