package com.example.fittrack

import kotlin.math.sqrt

/**
 * Conservative repetition detector.
 *
 * This is intentionally NOT presented as medical-grade or universally accurate.
 * Each exercise has different motion characteristics, so production accuracy
 * should be validated with real sensor datasets and tuned per device/exercise.
 */
class ExerciseDetector {

    private var lastMagnitude = 0f
    private var lastPeakTime = 0L
    private var reps = 0

    fun reset() {
        lastMagnitude = 0f
        lastPeakTime = 0L
        reps = 0
    }

    fun update(x: Float, y: Float, z: Float, timestampMs: Long): Int {
        val magnitude = sqrt(x * x + y * y + z * z)

        val rising = magnitude > lastMagnitude + 1.2f
        val enoughTime = timestampMs - lastPeakTime > 450

        if (rising && enoughTime && magnitude > 12f) {
            reps++
            lastPeakTime = timestampMs
        }

        lastMagnitude = magnitude
        return reps
    }
}
