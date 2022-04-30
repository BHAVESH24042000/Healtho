package com.example.healtho.firebase

import com.example.healtho.homescreens.models.Water
import com.example.healtho.homescreens.models.Workout
import com.example.healtho.util.InternetChecker
import com.example.healtho.util.Objects
import com.example.healtho.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseWorkoutDataSource  @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val internetChecker: InternetChecker
) {
    suspend fun getAllWorkoutLogs(email: String): Resource<List<Workout>> = try {
        if (internetChecker.hasInternetConnection()) {
            val workoutLog =
                fireStore.collection(Objects.USER_COLLECTION).document(email).collection(Objects.WORKOUT_COLLECTION)
                    .get()
                    .await()
                    .toObjects(Workout::class.java)
            Resource.Success(workoutLog)
        } else
            Resource.Error(
                errorType = Objects.ERROR_TYPE.NO_INTERNET,
                message = Objects.NO_INTERNET_MESSAGE
            )
    } catch (e: Exception) {
        Resource.Error(e.message.toString())
    }

    suspend fun addWater(email: String, workoutDTO: Workout): Resource<Workout> = try {
        if (internetChecker.hasInternetConnection()) {
            fireStore.collection(Objects.USER_COLLECTION).document(email).collection(Objects.WORKOUT_COLLECTION)
                .add(workoutDTO).await()
            Resource.Success(workoutDTO)
        } else
            Resource.Error(
                errorType = Objects.ERROR_TYPE.NO_INTERNET,
                message = Objects.NO_INTERNET_MESSAGE
            )
    } catch (e: Exception) {
        Resource.Error(e.message.toString())
    }

}