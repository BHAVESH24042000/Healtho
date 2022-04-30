package com.example.healtho.userdetails

import com.example.healtho.firebase.FirebaseRepo
import com.example.healtho.PreferencesRepo
import com.example.healtho.user.User
import com.example.healtho.util.Resource
import com.example.healtho.util.getSleepQuantity
import com.example.healtho.util.getWaterQuantity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserDetailRepo @Inject constructor(
    private val authDataSource: FirebaseRepo,
    private val preferencesRepo: PreferencesRepo,

    )  {

    companion object {
        private const val USER_NOT_LOGGED_IN = "User is not logged in"
    }

    suspend fun getCurrentUser() = preferencesRepo.getUserData()
    suspend fun saveUserDataEntryCompleted() = preferencesRepo.saveUserDataCompleted()

    private suspend fun saveUserIntoPreferences(user: User) {
        preferencesRepo.saveUserData(user)
    }

    suspend fun saveUserName(username: String) = withContext(Dispatchers.IO) {
        val user = getCurrentUser()
        return@withContext user?.let {
            it.username = username
            val resource = authDataSource.saveUserName(username, it.email)
            if (resource is Resource.Success)
                saveUserIntoPreferences(it)
            resource
        } ?: Resource.Error(USER_NOT_LOGGED_IN)
    }

    suspend fun saveUserWeight(weight: Int) = withContext(Dispatchers.IO) {
        val user = getCurrentUser()
        return@withContext user?.let {
            it.weight = weight
            it.waterLimit = weight.getWaterQuantity()
            val resource =
                authDataSource.saveUserWeightAndWaterQuantity(weight, quantity = it.waterLimit, it.email)
            if (resource is Resource.Success)
                saveUserIntoPreferences(it)
            resource
        } ?: Resource.Error(USER_NOT_LOGGED_IN)
    }

    suspend fun saveUserAge(age: Int) = withContext(Dispatchers.IO) {
        val user = getCurrentUser()
        return@withContext user?.let {
            it.age = age
            it.sleepLimit = age.getSleepQuantity()
            val resource = authDataSource.saveUserAgeAndSleepLimit(it.age, it.sleepLimit, it.email)
            if (resource is Resource.Success)
                saveUserIntoPreferences(it)
            resource
        } ?: Resource.Error(USER_NOT_LOGGED_IN)
    }
}