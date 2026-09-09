package com.example.fittrack

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StepSensorManager(context: Context) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps

    private var baseline: Float? = null

    fun start() {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (sensor != null) {
            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_STEP_COUNTER) return

        val totalSinceBoot = event.values[0]
        if (baseline == null) baseline = totalSinceBoot

        _steps.value = (totalSinceBoot - baseline!!).coerceAtLeast(0f).toInt()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
