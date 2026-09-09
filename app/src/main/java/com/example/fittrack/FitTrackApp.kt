package com.example.fittrack

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FitTrackApp(vm: FitnessViewModel) {
    var screen by remember { mutableStateOf("home") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("FitTrack") }) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = { screen = "home" }) { Text("Today") }
                OutlinedButton(onClick = { screen = "workout" }) { Text("Workout") }
                OutlinedButton(onClick = { screen = "history" }) { Text("History") }
            }

            Spacer(Modifier.height(16.dp))

            when (screen) {
                "workout" -> WorkoutScreen(vm)
                "history" -> HistoryScreen(vm)
                else -> HomeScreen(vm)
            }
        }
    }
}

@Composable
private fun HomeScreen(vm: FitnessViewModel) {
    val steps by vm.steps.collectAsState()
    val workouts by vm.workouts.collectAsState()

    val today = java.time.LocalDate.now().toEpochDay()
    val todayWorkouts = workouts.filter { it.dateEpochDay == today }
    val minutes = todayWorkouts.sumOf { it.durationSeconds } / 60

    Text("Today's health", style = MaterialTheme.typography.headlineMedium)
    Spacer(Modifier.height(16.dp))

    StatCard("Steps", steps.toString())
    StatCard("Workouts", todayWorkouts.size.toString())
    StatCard("Workout time", "$minutes min")

    Spacer(Modifier.height(16.dp))
    Text(
        "Sensor values are device estimates. For health decisions, use validated medical devices.",
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
private fun StatCard(label: String, value: String) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
private fun WorkoutScreen(vm: FitnessViewModel) {
    var exercise by remember { mutableStateOf(ExerciseCatalog.all.first()) }
    var expanded by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf("10") }
    var reps by remember { mutableStateOf("10") }
    var sets by remember { mutableStateOf("3") }
    var weight by remember { mutableStateOf("0") }

    Text("Record workout", style = MaterialTheme.typography.headlineMedium)
    Spacer(Modifier.height(12.dp))

    OutlinedButton(onClick = { expanded = true }) {
        Text(exercise)
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        ExerciseCatalog.all.forEach { item ->
            DropdownMenuItem(
                text = { Text(item) },
                onClick = {
                    exercise = item
                    expanded = false
                }
            )
        }
    }

    Spacer(Modifier.height(10.dp))

    OutlinedTextField(
        value = duration,
        onValueChange = { duration = it.filter(Char::isDigit) },
        label = { Text("Duration (minutes)") },
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = reps,
        onValueChange = { reps = it.filter(Char::isDigit) },
        label = { Text("Reps") },
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = sets,
        onValueChange = { sets = it.filter(Char::isDigit) },
        label = { Text("Sets") },
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = weight,
        onValueChange = { weight = it.filter { c -> c.isDigit() || c == '.' } },
        label = { Text("Weight (kg, optional)") },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(12.dp))

    Button(
        onClick = {
            val category = when (exercise) {
                in listOf("Walking", "Running", "Jogging", "Cycling", "Skipping") -> "Cardio"
                in listOf("Plank", "Side Plank", "Crunches", "Sit-ups", "Leg Raises", "Bicycle Crunches") -> "Core"
                else -> "Strength"
            }
            vm.addWorkout(
                exercise = exercise,
                category = category,
                durationSeconds = (duration.toIntOrNull() ?: 0) * 60,
                reps = reps.toIntOrNull() ?: 0,
                sets = sets.toIntOrNull() ?: 0,
                weightKg = weight.toDoubleOrNull() ?: 0.0
            )
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Save workout")
    }
}

@Composable
private fun HistoryScreen(vm: FitnessViewModel) {
    val workouts by vm.workouts.collectAsState()

    Text("Workout history", style = MaterialTheme.typography.headlineMedium)
    Spacer(Modifier.height(12.dp))

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(workouts) { workout ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(workout.exercise, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${workout.sets} sets × ${workout.reps} reps • " +
                            "${workout.durationSeconds / 60} min"
                    )
                    if (workout.weightKg > 0) {
                        Text("Weight: ${workout.weightKg} kg")
                    }
                }
            }
        }
    }
}
