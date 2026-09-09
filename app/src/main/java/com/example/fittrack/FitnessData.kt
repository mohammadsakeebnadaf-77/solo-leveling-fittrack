package com.example.fittrack

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exercise: String,
    val category: String,
    val dateEpochDay: Long,
    val durationSeconds: Int = 0,
    val reps: Int = 0,
    val sets: Int = 0,
    val weightKg: Double = 0.0,
    val distanceMeters: Double = 0.0,
    val caloriesEstimate: Double = 0.0,
    val notes: String = ""
)

data class DailySummary(
    val steps: Int = 0,
    val workouts: Int = 0,
    val workoutMinutes: Int = 0,
    val calories: Int = 0
)

object ExerciseCatalog {
    val all = listOf(
        "Walking", "Running", "Jogging", "Cycling", "Jumping Jacks",
        "High Knees", "Mountain Climbers", "Burpees", "Skipping",
        "Squats", "Lunges", "Reverse Lunges", "Bulgarian Split Squats",
        "Calf Raises", "Wall Sit", "Push-ups", "Incline Push-ups",
        "Shoulder Press", "Bicep Curls", "Tricep Extensions", "Lateral Raises",
        "Plank", "Side Plank", "Crunches", "Sit-ups", "Leg Raises",
        "Bicycle Crunches", "Stretching", "Mobility"
    )
}
