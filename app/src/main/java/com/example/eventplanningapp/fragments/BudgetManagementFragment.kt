package com.example.eventplanningapp.fragments

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eventplanningapp.data.Budget
import com.example.eventplanningapp.data.BudgetDatabase
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import com.example.eventplanningapp.views.BudgetViewModel
import com.example.eventplanningapp.views.BudgetViewModelFactory

@Composable
fun BudgetManagementFragment(navController: NavController) {
    val budgetViewModel: BudgetViewModel = viewModel(
        factory = BudgetViewModelFactory(BudgetDatabase.getDatabase(navController.context).budgetDao())
    )

    // State variables for budget fields
    var showDialog by remember { mutableStateOf(false) }
    var budgetName by remember { mutableStateOf("") }
    var budgetAmount by remember { mutableStateOf("") }
    var budgetCategory by remember { mutableStateOf("") }

    // Observe all budgets
    val allBudgets by budgetViewModel.allBudgets.observeAsState(emptyList())
    val totalBudget by budgetViewModel.totalBudget.observeAsState(0.0)

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Budget Management", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Total Budget: Ksh ${"%.2f".format(totalBudget)}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Add budget button
        Button(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Text("Add Budget")
        }

        // Displaying all budgets
        Spacer(modifier = Modifier.height(20.dp))
        if (allBudgets.isNotEmpty()) {
            LazyColumn {
                items(allBudgets) { budget ->
                    BudgetItem(budget, onDelete = { budgetViewModel.deleteBudget(it) })
                }
            }
        } else {
            Text(text= "No budgets available yet", modifier = Modifier.align(CenterHorizontally))
        }

        // Add Budget Dialog
        if (showDialog) {
            AddBudgetDialog(
                onDismiss = { showDialog = false },
                onAddBudget = { name, amount, category ->
                    if (name.isNotEmpty() && amount.isNotEmpty() && category.isNotEmpty()) {
                        budgetViewModel.addBudget(Budget(name = name, amount = amount.toDouble(), category = category))
                        showDialog = false // Close the dialog after adding task
                    } else {
                        Toast.makeText(navController.context, "All fields are required", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

@Composable
fun AddBudgetDialog(
    onDismiss: () -> Unit,
    onAddBudget: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Budget") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Budget Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Budget Amount") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Budget Category") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onAddBudget(name, amount, category) }) {
                Text("Add Budget")
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
fun BudgetItem(budget: Budget, onDelete: (Budget) -> Unit) {
    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(budget.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Text("Ksh ${budget.amount}", style = MaterialTheme.typography.bodyLarge)
            IconButton(onClick = { onDelete(budget) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }
        }
        Text("Category: ${budget.category}", style = MaterialTheme.typography.bodyMedium)
    }
}
