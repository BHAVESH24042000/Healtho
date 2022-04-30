package com.example.healtho.homescreens.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_table")
data class Workout(
    var userEmail: String = "",
    val timeStamp: Long = 0L,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)
