package com.example.healtho.user

data class UserDTO(
    val username: String = "",
    val email: String = "",
    val profileImg: String = "",
    val exp: Long = 0L,
    val waterLimit: Int = 0,
    val sleepLimit: Int = 0,
    val age: Int = 0,
    val weight: Int = 0
)
