package com.example.healtho.firebase

import com.example.healtho.homescreens.models.Water
import com.example.healtho.util.InternetChecker
import com.example.healtho.util.Objects
import com.example.healtho.util.Objects.NO_INTERNET_MESSAGE
import com.example.healtho.util.Objects.USER_COLLECTION
import com.example.healtho.util.Objects.WATER_COLLECTION
import com.example.healtho.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirestoreWaterDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val internetChecker: InternetChecker
)  {

    suspend fun getAllWaterLogs(email: String): Resource<List<Water>> = try {
        if (internetChecker.hasInternetConnection()) {
            val waterLog =
                fireStore.collection(USER_COLLECTION).document(email).collection(WATER_COLLECTION)
                    .get()
                    .await()
                    .toObjects(Water::class.java)
            Resource.Success(waterLog)
        } else
            Resource.Error(
                errorType = Objects.ERROR_TYPE.NO_INTERNET,
                message = NO_INTERNET_MESSAGE
            )
    } catch (e: Exception) {
        Resource.Error(e.message.toString())
    }

    suspend fun addWater(email: String, waterDTO: Water): Resource<Water> = try {
        if (internetChecker.hasInternetConnection()) {
            fireStore.collection(USER_COLLECTION).document(email).collection(WATER_COLLECTION)
                .add(waterDTO).await()
            Resource.Success(waterDTO)
        } else
            Resource.Error(
                errorType = Objects.ERROR_TYPE.NO_INTERNET,
                message = NO_INTERNET_MESSAGE
            )
    } catch (e: Exception) {
        Resource.Error(e.message.toString())
    }
}