package com.example.farmhand.database.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.farmhand.database.DAO.FarmingDao
import com.example.farmhand.database.DAO.UserDao
import com.example.farmhand.database.converter.DateConverter
import com.example.farmhand.database.entities.Logs
import com.example.farmhand.database.entities.Task
import com.example.farmhand.database.entities.User


@Database(
    entities = [
        User::class,
        Task::class,
        Logs::class
    ], version = 1
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val farmingDao: FarmingDao
}

object DatabaseClient {
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "farmhand_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
