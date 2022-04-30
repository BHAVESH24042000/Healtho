package com.example.healtho.room
import com.example.healtho.homescreens.models.Water
import javax.inject.Inject

class RoomWaterDataSource @Inject constructor(private val waterDao: WaterDao) {

    fun getAllWaterLogsAfterTime(time: Long) = waterDao.getAllAfterTime(time)

    suspend fun insertWater(water: List<Water>) = waterDao.insertWater(water)

    suspend fun deleteAllWaterLogs() = waterDao.deleteAll()
}
