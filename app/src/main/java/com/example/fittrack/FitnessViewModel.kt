package com.example.fittrack

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class FitnessViewModel(application: Application) :
    AndroidViewModel(application), SensorEventListener {

    private val dao = AppDatabase.get(application).workoutDao()
    private val stepSensor = StepSensorManager(application)

    val workouts: StateFlow<List<WorkoutEntity>> =
        dao.observeAll().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    val steps: StateFlow<Int> = stepSensor.steps

    private val sensorManager =
        application.getSystemService(Application.SENSOR_SERVICE) as SensorManager

    private val detector = ExerciseDetector()

    init {
        stepSensor.start()
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun addWorkout(
        exercise: String,
        category: String,
        durationSeconds: Int,
        reps: Int,
        sets: Int,
        weightKg: Double
    ) {
        viewModelScope.launch {
            dao.insert(
                WorkoutEntity(
                    exercise = exercise,
                    category = category,
                    dateEpochDay = LocalDate.now().toEpochDay(),
                    durationSeconds = durationSeconds,
                    reps = reps,
                    sets = sets,
                    weightKg = weightKg,
                    caloriesEstimate = estimateCalories(durationSeconds, category)
                )
            )
        }
    }

    private fun estimateCalories(seconds: Int, category: String): Double {
        val minutes = seconds / 60.0
        val met = when (category) {
            "Cardio" -> 6.0
            "Strength" -> 4.0
            "Core" -> 4.0
            else -> 2.5
        }
        // User weight is intentionally not assumed. This is only a relative estimate.
        return minutes * met
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            detector.update(
                event.values[0],
                event.values[1],
                event.values[2],
                System.currentTimeMillis()
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onCleared() {
        stepSensor.stop()
        sensorManager.unregisterListener(this)
        super.onCleared()
    }

    companion object {
        fun factory(app: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FitnessViewModel(app) as T
                }
            }
    }
}
