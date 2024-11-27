package com.example.eventplanningapp.views

import androidx.lifecycle.*
import com.example.eventplanningapp.data.Budget
import com.example.eventplanningapp.data.BudgetDao
import kotlinx.coroutines.launch



class BudgetViewModel(private val budgetDao: BudgetDao) : ViewModel() {

    // LiveData to observe all budgets
    val allBudgets: LiveData<List<Budget>> = budgetDao.getAllBudgets()

    val totalBudget: LiveData<Double> = MediatorLiveData<Double>().apply {
        addSource(allBudgets) { budgets ->
            value = budgets.sumOf { it.amount }
        }
    }


    // Function to add a budget
    fun addBudget(budget: Budget) {
        viewModelScope.launch {
            budgetDao.insert(budget)
        }
    }

    // Function to update a budget
    fun updateBudget(budget: Budget) {
        viewModelScope.launch {
            budgetDao.update(budget)
        }
    }

    // Function to delete a budget
    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            budgetDao.delete(budget)
        }
    }
}

class BudgetViewModelFactory(private val budgetDao: BudgetDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BudgetViewModel(budgetDao) as T
    }
}
