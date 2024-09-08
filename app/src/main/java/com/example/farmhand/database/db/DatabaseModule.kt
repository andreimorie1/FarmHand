package com.example.farmhand.database.db

import android.content.Context
import androidx.room.Room
import com.example.farmhand.database.database.AppDatabase
import com.example.farmhand.database.repositories.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "userdata"
        ).build()
    }

    @Provides
    fun provideUserRepository(db: AppDatabase): UserRepository {
        return UserRepository(db) // You might need to pass the DAO here: UserRepository(db.userDao())
    }
}