package com.example.healtho.firebase


import com.example.healtho.user.User
import com.example.healtho.user.UserDTO
import com.example.healtho.util.InternetChecker
import com.example.healtho.util.Objects
import com.example.healtho.util.Objects.NO_INTERNET_MESSAGE
import com.example.healtho.util.Objects.USER_COLLECTION
import com.example.healtho.util.Objects.USER_DOES_NOT_EXIST
import kotlinx.coroutines.tasks.await
import com.example.healtho.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject


class FirebaseRepo @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val internetChecker: InternetChecker,
    private val firebaseAuth: FirebaseAuth
)  {

    companion object {
        private const val AGE_FIELD_NAME = "age"
        private const val WEIGHT_FIELD_NAME = "weight"
        private const val USERNAME_FIELD_NAME = "username"
        private const val SLEEP_LIMIT_FIELD_NAME = "sleepLimit"
        private const val WATER_LIMIT_FIELD_NAME = "waterLimit"
    }

   suspend fun getUserData(email: String, password: String): Resource<User> =
        try {
            if (internetChecker.hasInternetConnection()) {
                val user = fireStore.collection(USER_COLLECTION).document(email).get().await()
                    .toObject(User::class.java)

                user?.let {
                    if(it.password == password) {
                        Resource.Success(user, "User Details Fetched Success")
                    }else{
                        Resource.Error(message = "Incorrect Email/Password")
                    }
                } ?: Resource.Error(message = USER_DOES_NOT_EXIST)
            } else
                Resource.Error(
                    errorType = Objects.ERROR_TYPE.NO_INTERNET,
                    message = NO_INTERNET_MESSAGE
                )
        } catch (e: Exception) {
            Resource.Error(message = e.message.toString())
        }

    suspend fun saveUserData(user: User): Resource<User> =
        try {
            if (internetChecker.hasInternetConnection()) {
                fireStore.collection(USER_COLLECTION).document(user.email)
                    .set(user).await()
                Resource.Success(user)
            } else
                Resource.Error(
                    errorType = Objects.ERROR_TYPE.NO_INTERNET,
                    message = NO_INTERNET_MESSAGE
                )
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }

    suspend fun saveUserName(username: String, email: String): Resource<Unit> =
        try {
            if (internetChecker.hasInternetConnection()) {
                fireStore.collection(USER_COLLECTION).document(email)
                    .update(USERNAME_FIELD_NAME, username)
                    .await()
                Resource.Success()
            } else
                Resource.Error(
                    errorType = Objects.ERROR_TYPE.NO_INTERNET,
                    message = NO_INTERNET_MESSAGE
                )
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }

    suspend fun saveUserAgeAndSleepLimit(
        age: Int?,
        limit: Int?,
        email: String
    ): Resource<Unit> =
        try {
            if (internetChecker.hasInternetConnection()) {
                fireStore.collection(USER_COLLECTION).document(email)
                    .update(AGE_FIELD_NAME, age, SLEEP_LIMIT_FIELD_NAME, limit).await()
                Resource.Success()
            } else
                Resource.Error(
                    errorType = Objects.ERROR_TYPE.NO_INTERNET,
                    message = NO_INTERNET_MESSAGE
                )
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }

    suspend fun saveUserWeightAndWaterQuantity(
        weight: Int,
        quantity: Int?,
        email: String
    ): Resource<Unit> =
        try {
            if (internetChecker.hasInternetConnection()) {
                fireStore.collection(USER_COLLECTION).document(email)
                    .update(WEIGHT_FIELD_NAME, weight, WATER_LIMIT_FIELD_NAME, quantity).await()
                Resource.Success()
            } else
                Resource.Error(
                    errorType = Objects.ERROR_TYPE.NO_INTERNET,
                    message = NO_INTERNET_MESSAGE
                )
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }


    suspend fun fetchAllUsers() = try {
        if (internetChecker.hasInternetConnection()) {
            val users =
                fireStore.collection(USER_COLLECTION).get().await().toObjects(UserDTO::class.java)
            Resource.Success(users)
        } else
            Resource.Error(
                errorType = Objects.ERROR_TYPE.NO_INTERNET,
                message = NO_INTERNET_MESSAGE
            )
    } catch (e: Exception) {
        Resource.Error(e.message.toString())
    }

    suspend fun updateUserSleepLimit(limit: Int, email: String): Resource<Unit> = try {
        if (internetChecker.hasInternetConnection()) {
            fireStore.collection(USER_COLLECTION).document(email)
                .update(SLEEP_LIMIT_FIELD_NAME, limit)
                .await()
            Resource.Success()
        } else
            Resource.Error(
                errorType = Objects.ERROR_TYPE.NO_INTERNET,
                message = NO_INTERNET_MESSAGE
            )
    } catch (e: Exception) {
        Resource.Error(e.message.toString())
    }

    suspend fun updateUserWaterLimit(limit: Int, email: String): Resource<Unit> = try {
        if (internetChecker.hasInternetConnection()) {
            fireStore.collection(USER_COLLECTION).document(email)
                .update(WATER_LIMIT_FIELD_NAME, limit)
                .await()
            Resource.Success()
        } else
            Resource.Error(
                errorType = Objects.ERROR_TYPE.NO_INTERNET,
                message = NO_INTERNET_MESSAGE
            )
    } catch (e: Exception) {
        Resource.Error(e.message.toString())
    }

    suspend fun registrationuser(email: String, password: String): Resource<Unit> = try {
        if (internetChecker.hasInternetConnection()) {
           firebaseAuth.createUserWithEmailAndPassword(email,password).await()
            Resource.Success()
        } else
            Resource.Error(
                errorType = Objects.ERROR_TYPE.NO_INTERNET,
                message = NO_INTERNET_MESSAGE
            )
    } catch (e: Exception) {
        Resource.Error(e.message.toString())
    }

}
