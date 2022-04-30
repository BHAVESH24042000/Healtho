package com.example.healtho.auth

import com.example.healtho.firebase.FirebaseRepo
import com.example.healtho.PreferencesRepo
import com.example.healtho.user.User
import com.example.healtho.util.Objects.USER_DOES_NOT_EXIST
import com.example.healtho.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class AuthRepo @Inject constructor(
    private val preferencesRepo: PreferencesRepo,
    private val authDataSource: FirebaseRepo,

) {

    companion object
    {
        private const val USER_NOT_LOGGED_IN = "User is not logged in"
    }

    suspend fun getCurrentUser() = preferencesRepo.getUserData()

    suspend fun isUserLoggedIn() = getCurrentUser() != null

    suspend fun saveUserDataEntryCompleted() = preferencesRepo.saveUserDataCompleted()


    private suspend fun saveUserIntoPreferences(user: User) {
        preferencesRepo.saveUserData(user)
    }

    private suspend fun removeUserFromPreferences() {
        preferencesRepo.removeUserData()
    }

    private suspend fun removeUserDataCompleted() {
        preferencesRepo.removeUserData()
    }

    suspend fun logoutUser() = withContext(Dispatchers.IO) {
        removeUserFromPreferences()
        removeUserDataCompleted()
    }

    suspend fun isUserRegistered(email: String, password: String): Resource<Boolean> =
        withContext(Dispatchers.IO) {

            val userResource = authDataSource.getUserData(email, password)
            if (userResource is Resource.Error) {
                if (userResource.message == USER_DOES_NOT_EXIST)
                    return@withContext Resource.Success(false)
                else
                    return@withContext Resource.Error(userResource.message, errorType = userResource.errorType)
            } else {
                userResource.data?.let { saveUserIntoPreferences(it) }
                return@withContext Resource.Success(true)
            }
        }

    suspend fun continueAfterCheckingLogin(email: String, password: String) : Any? = withContext(Dispatchers.IO) {
        val userResource = authDataSource.getUserData(email, password)
        if (userResource is Resource.Error) {
           return@withContext userResource.copy()
        } else if(userResource is Resource.Success && userResource.message == "User Details Fetched Success") {
            userResource.data?.let {
                saveUserIntoPreferences(it)
                return@withContext userResource
            }
        }else{
            return@withContext userResource.message
        }
    }

    suspend fun continueAfterCheckingRegister(email: String, password: String): Any? = withContext(Dispatchers.IO){

        val isUserRegister = authDataSource.registrationuser(email, password)

        if(isUserRegister is Resource.Success) {
            val user = User(
                "",
                email,
                password,
                "",
                -1,
                -1,
                -1,
                -1,
                -1,
            )

            val userResource = authDataSource.saveUserData(user)

             if(userResource is Resource.Success) {
                userResource.data?.let {
                    saveUserIntoPreferences(it)
                    return@withContext Resource.Success(it, "Registration Success")
                }
                return@withContext userResource
            }else{
                 return@withContext userResource
             }
        }else{
            return@withContext isUserRegister
        }
    }


    suspend fun updateUserWaterLimit(limit: Int) = withContext(Dispatchers.IO) {
        return@withContext getCurrentUser()?.let {
            it.waterLimit = limit
            val resource = authDataSource.updateUserWaterLimit(it.waterLimit!!, it.email)
            if (resource is Resource.Success)
                saveUserIntoPreferences(it)
            resource
        } ?: Resource.Error(USER_NOT_LOGGED_IN)
    }

    suspend fun updateUserSleepLimit(limit: Int) = withContext(Dispatchers.IO) {
        return@withContext getCurrentUser()?.let {
            it.sleepLimit = limit
            Timber.d("Editing sleep Limit in REPO")
            val resource = authDataSource.updateUserSleepLimit(it.sleepLimit!!, it.email)
            if (resource is Resource.Success)
                saveUserIntoPreferences(it)
            resource
        } ?: Resource.Error(USER_NOT_LOGGED_IN)
    }


}