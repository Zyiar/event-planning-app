package com.example.eventplanningapp.fragments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.eventplanningapp.database.Task
import com.example.eventplanningapp.database.TaskDatabase
import com.example.eventplanningapp.viewmodel.TaskViewModel
import com.example.eventplanningapp.viewmodel.TaskViewModelFactory
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.input.TextFieldValue


@Composable
fun TaskManagementFragment(navController: NavController, modifier: Modifier = Modifier) {
    val taskViewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory(TaskDatabase.getDatabase(navController.context).taskDao())
    )

    // State for user input

    val tasks by taskViewModel.allTasks.observeAsState(emptyList())
    var taskTitle by remember { mutableStateOf(TextFieldValue("")) }
    var taskDescription by remember { mutableStateOf(TextFieldValue("")) }


    var showDialog by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Task Management", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Add Task")
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (tasks.isEmpty()) {
            // Display message when no tasks are available
            Text(
                text = "No tasks added yet!",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            // Display tasks
            LazyColumn {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onComplete = { updatedTask -> taskViewModel.updateTaskCompletion(updatedTask) },
                        onDelete = { taskViewModel.deleteTask(it) },
                        onUpdate = { updatedTask, newName ->
                            taskViewModel.updateTaskCompletion(updatedTask.copy(title = newName))
                        }
                    )
                }
            }
        }

        if (showDialog) {
            AddTaskDialog(
                onDismiss = { showDialog = false },
                onAddTask = { title, description ->
                    taskViewModel.addTask(Task(title = title, description = description, isCompleted = false))
                    showDialog = false
                }
            )
        }
    }

}
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAddTask: (String, String) -> Unit
) {

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isCompleted by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = {
            Column {
                // Title TextField
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description TextField
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Task Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Checkbox for completion status
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = { isCompleted = it }
                    )
                    Text("Completed")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotEmpty() && description.isNotEmpty()) {
                        onAddTask(title, description)
                    }
                }
            ) {
                Text("Add Task")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TaskItem(
    task: Task,
    onComplete: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onUpdate: (Task, String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var updatedTitle by remember { mutableStateOf(task.title) }

    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isEditing) {
                TextField(
                    value = updatedTitle,
                    onValueChange = { updatedTitle = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    onUpdate(task.copy(title = updatedTitle), updatedTitle)
                    isEditing = false
                }) {
                    Icon(Icons.Filled.Check, contentDescription = "Save")
                }
            } else {
                Text(task.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                IconButton(onClick = { isEditing = true }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                }
            }

            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onComplete(task.copy(isCompleted = it)) }
            )


            IconButton(onClick = { onDelete(task) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }
        }
        Text(task.description, style = MaterialTheme.typography.bodyMedium)
    }
}



@Preview
@Composable
fun PreviewTaskManagement() {
    TaskManagementFragment(navController = rememberNavController())
}
