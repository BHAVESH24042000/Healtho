package com.example.healtho.room
import com.example.healtho.homescreens.models.Water
import com.example.healtho.homescreens.models.Workout
import javax.inject.Inject

class RoomWorkoutDataSource @Inject constructor(private val workoutDao: WorkoutDao) {

    fun getAllWorkoutLogsAfterTime(time: Long) = workoutDao.getAllAfterTime(time)

    suspend fun insertWorkout(workout: List<Workout>) = workoutDao.insertWorkout(workout)

    suspend fun deleteAllWorkoutLogs() = workoutDao.deleteAll()
}
