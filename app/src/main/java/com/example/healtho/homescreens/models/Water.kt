package com.example.healtho.homescreens.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "water_table")
data class Water(
    var userEmail: String = "",
    val quantity: String = "",
    val timeStamp: Long = 0L,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)
