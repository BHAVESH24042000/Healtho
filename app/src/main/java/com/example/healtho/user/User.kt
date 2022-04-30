package com.example.healtho.user

data class User(
    var username: String = "",
    val email: String = "",
    var password:String = "",
    val profileImg: String = "",
    var exp: Long? = null,
    var waterLimit: Int? = null,
    var sleepLimit: Int? = null,
    var age: Int? = null,
    var weight: Int? = null
)
