package com.example.fittrack

import android.content.Context
import androidx.room3.Dao
import androidx.room3.Database
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Room
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert
    suspend fun insert(workout: WorkoutEntity)

    @Query("SELECT * FROM workouts ORDER BY dateEpochDay DESC, id DESC")
    fun observeAll(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE dateEpochDay = :day ORDER BY id DESC")
    fun observeForDay(day: Long): Flow<List<WorkoutEntity>>
}

@Database(
    entities = [WorkoutEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : androidx.room3.RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder<AppDatabase>(
                    context.applicationContext,
                    "fittrack.db"
                ).build().also { INSTANCE = it }
            }
    }
}
