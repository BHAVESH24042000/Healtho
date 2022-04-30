package com.example.healtho.util

sealed class Resource<T>(
    open val data: T? = null,
    open val message: String = "Oops something went wrong.",
    open val errorType: Objects.ERROR_TYPE = Objects.ERROR_TYPE.UNKNOWN
) {
    data class Success<T>(
        override val data: T? = null,
        override val message: String = "Success."
    ) :
        Resource<T>(data)

    data class Loading<T>(override val data: T? = null) : Resource<T>(data)
    data class Error<T>(
        override val message: String = "Oops something went wrong.",
        override val data: T? = null,
        override val errorType: Objects.ERROR_TYPE = Objects.ERROR_TYPE.UNKNOWN
    ) : Resource<T>(data, message)

    class Empty<T>() : Resource<T>()
}
