package com.example.healtho.room

import com.example.healtho.homescreens.models.Sleep
import javax.inject.Inject

class RoomSleepDataSource @Inject constructor(private val sleepDao: SleepDao) {

    fun getAllSleepAfterTime(time: Long) = sleepDao.getAllAfterTime(time)

    suspend fun insertSleep(sleeps: List<Sleep>) = sleepDao.insertSleep(sleeps)

    suspend fun deleteAll() = sleepDao.deleteAll()
}
