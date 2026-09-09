# FitTrack Android MVP

FitTrack is a Kotlin + Jetpack Compose Android fitness-tracking starter project.

## Included

- Jetpack Compose UI
- Local Room 3 database
- Daily workout records
- Exercise library
- Step Counter sensor
- Accelerometer hook for future exercise recognition
- Workout history
- Basic duration/reps/sets/weight logging
- Health Connect dependency and manifest permissions prepared for the next integration stage

## Important accuracy note

The accelerometer repetition detector in this MVP is deliberately conservative and generic.
It is NOT accurate for every exercise and is NOT medical-grade.

A production version should use exercise-specific algorithms or a validated ML model,
test across multiple phone models, and compare predictions against labeled sensor data.

## Open in Android Studio

1. Install a current Android Studio.
2. Open this folder.
3. Allow Gradle to sync.
4. Run on a physical Android phone.
5. Grant Activity Recognition permission.
6. Test the step counter on the device.

## Next production stages

1. Add Health Connect permission flow and synchronization.
2. Add a foreground workout service for long-running tracking.
3. Add GPS route tracking with explicit location permission.
4. Build exercise-specific rep counters.
5. Add charts and weekly/monthly analytics.
6. Add export/delete-data controls and a privacy policy.
7. Add automated tests and device testing.
8. Generate a signed Android App Bundle (.aab).
9. Complete Play Console health-app declaration, Data Safety form, store listing and testing requirements.
