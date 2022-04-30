package com.example.healtho

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.healtho.user.User
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PreferencesRepo @Inject constructor(private val dataStore: DataStore<androidx.datastore.preferences.core.Preferences>) {

    companion object {
        private val USER_KEY = stringPreferencesKey("User")
        private val USER_DATA_KEY = booleanPreferencesKey("UserData")
    }

    suspend fun saveUserData(user: User) = withContext(Dispatchers.IO) {
        val userSerialized = Gson().toJson(user)
        dataStore.edit {
            it[USER_KEY] = userSerialized
        }
    }

    suspend fun removeUserData() = withContext(Dispatchers.IO) {
        dataStore.edit {
            it.remove(USER_KEY)
        }
    }

    suspend fun getUserData(): User? = withContext(Dispatchers.IO) {
        return@withContext dataStore.data.map {
            val serializedUser = it[USER_KEY]
            return@map serializedUser?.let { sUser ->
                Gson().fromJson(sUser, User::class.java)
            }
        }.first()
    }



    suspend fun saveUserDataCompleted() = withContext(Dispatchers.IO) {
        dataStore.edit {
            it[USER_DATA_KEY] = true
        }
    }

    suspend fun removeUserDataCompleted() = withContext(Dispatchers.IO) {
        dataStore.edit {
            it.remove(USER_DATA_KEY)
        }
    }



}
