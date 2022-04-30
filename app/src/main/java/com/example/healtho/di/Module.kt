package com.vaibhav.healthify.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.healtho.homescreens.models.Sleep
import com.example.healtho.homescreens.models.Water
import com.example.healtho.homescreens.models.Workout
import com.example.healtho.room.HealthoDB
import com.example.healtho.room.SleepDao
import com.example.healtho.room.WaterDao
import com.example.healtho.room.WorkoutDao
import com.example.healtho.util.ChartDataOrganizer
import com.example.healtho.util.InternetChecker
import com.example.healtho.util.Objects.DATASTORE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    private val Context.dataStore by preferencesDataStore(DATASTORE)

    @Provides
    fun providesFireStore(): FirebaseFirestore = Firebase.firestore

    @Provides
    fun providesFireBaseAuth(): FirebaseAuth = Firebase.auth


    @Provides
    fun providesStorage(): FirebaseStorage = Firebase.storage

    // Chart DataOrganizer
    @Provides
    fun providesWaterChartDataOrganizer(): ChartDataOrganizer<Water> = ChartDataOrganizer()

    @Provides
    fun providesSleepChartDataOrganizer(): ChartDataOrganizer<Sleep> = ChartDataOrganizer()

    @Provides
    fun providesWorkoutChartDataOrganizer(): ChartDataOrganizer<Workout> = ChartDataOrganizer()

    @Provides
    fun providesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    fun provideInternetChecker(@ApplicationContext appContext: Context) =
        InternetChecker(appContext)


    @Provides
    @Singleton
    fun providesRoomDb(@ApplicationContext context: Context): HealthoDB =
        Room.databaseBuilder(context, HealthoDB::class.java, "Healtho_DB")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providesWaterDao(roomDatabase: HealthoDB): WaterDao = roomDatabase.getWaterDao()

    @Provides
    fun providesSleepDao(roomDatabase: HealthoDB): SleepDao = roomDatabase.getSleepDao()

    @Provides
    fun providesWorkoutDao(roomDatabase: HealthoDB): WorkoutDao = roomDatabase.getWorkoutDao()
//
//    @Provides
//    fun providesLeaderBoardDao(roomDatabase: HealthifyDB): LeaderBoardDao =
//        roomDatabase.getLeaderBoardDao()
//
//    // mappers
//
//    @Provides
//    fun providesUserMapper(): UserMapper = UserMapper()
//
//    @Provides
//    fun providesWaterMapper(): WaterMapper = WaterMapper()
//
//    @Provides
//    fun providesSleepMapper(): SleepMapper = SleepMapper()
//
//    @Provides
//    fun providesLeaderBoardMapper(): LeaderBoardItemMapper = LeaderBoardItemMapper()
//
//    // Chart DataOrganizer
//    @Provides
//    fun providesWaterChartDataOrganizer(): ChartDataOrganizer<Water> = ChartDataOrganizer()
//
//    @Provides
//    fun providesSleepChartDataOrganizer(): ChartDataOrganizer<Sleep> = ChartDataOrganizer()
//
}
