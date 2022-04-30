package com.example.healtho.homescreens.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.healtho.R

@Entity(tableName = "sleep_table")
data class Sleep(
    val sleepDuration: Int? = null,
    val timeStamp: Long? = null,
    var userEmail: String = "",

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
