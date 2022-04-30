package com.example.healtho.firebase

import com.example.healtho.homescreens.models.Sleep
import com.example.healtho.util.InternetChecker
import com.example.healtho.util.Objects
import com.example.healtho.util.Objects.NO_INTERNET_MESSAGE
import com.example.healtho.util.Objects.SLEEP_COLLECTION
import com.example.healtho.util.Objects.USER_COLLECTION
import com.example.healtho.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreSleepDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val internetChecker: InternetChecker
) {
     suspend fun getAllSleepLogs(email: String): Resource<List<Sleep>> = try {
        if (internetChecker.hasInternetConnection()) {
            val sleepLog =
                fireStore.collection(USER_COLLECTION).document(email).collection(SLEEP_COLLECTION)
                    .get()
                    .await()
                    .toObjects(Sleep::class.java)
            Resource.Success(sleepLog)
        } else
            Resource.Error(
                errorType = Objects.ERROR_TYPE.NO_INTERNET,
                message = NO_INTERNET_MESSAGE
            )
    } catch (e: Exception) {
        Resource.Error(e.message.toString())
    }

    suspend fun addSleep(email: String, sleepDTO: Sleep): Resource<Sleep> = try {
        if (internetChecker.hasInternetConnection()) {
            fireStore.collection(USER_COLLECTION).document(email).collection(SLEEP_COLLECTION)
                .add(sleepDTO).await()
            Resource.Success(sleepDTO)
        } else
            Resource.Error(
                errorType = Objects.ERROR_TYPE.NO_INTERNET,
                message = NO_INTERNET_MESSAGE
            )
    } catch (e: Exception) {
        Resource.Error(e.message.toString())
    }
}
