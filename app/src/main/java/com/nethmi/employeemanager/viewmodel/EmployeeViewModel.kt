package com.nethmi.employeemanager.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nethmi.employeemanager.data.Employee
import com.nethmi.employeemanager.data.RetrofitInstance
import kotlinx.coroutines.launch

//  use AndroidViewModel to get Context
class EmployeeViewModel(application: Application) : AndroidViewModel(application) {

    private val _employees = mutableStateOf<List<Employee>>(emptyList())
    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _selectedDepartment = mutableStateOf("All")
    val selectedDepartment: State<String> = _selectedDepartment

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val sharedPref = application.getSharedPreferences("EmployeePrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    val filteredEmployees = derivedStateOf {
        val query = _searchQuery.value.lowercase()
        val dept = _selectedDepartment.value

        _employees.value.filter { employee ->
            val matchesSearch = employee.name.lowercase().contains(query) ||
                    employee.designation.lowercase().contains(query) ||
                    employee.department.lowercase().contains(query)

            val matchesDept = if (dept == "All") true else employee.department == dept

            matchesSearch && matchesDept
        }
    }

    init {
        fetchEmployees()
    }

    fun fetchEmployees() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // get Online data
                val response = RetrofitInstance.api.getEmployees()
                _employees.value = response

                // --- OFFLINE SUPPORT ---
                val json = gson.toJson(response)
                sharedPref.edit().putString("cached_employees", json).apply()

            } catch (e: Exception) {
                // get saved data without internet
                val cachedJson = sharedPref.getString("cached_employees", null)
                if (cachedJson != null) {
                    val type = object : TypeToken<List<Employee>>() {}.type
                    val cachedList: List<Employee> = gson.fromJson(cachedJson, type)
                    _employees.value = cachedList
                }
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onDepartmentSelected(dept: String) {
        _selectedDepartment.value = dept
    }

    fun addEmployee(employee: Employee) {
        viewModelScope.launch {
            try {
                RetrofitInstance.api.addEmployee(employee)
                fetchEmployees()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateEmployee(id: String, updatedEmployee: Employee) {
        viewModelScope.launch {
            try {
                RetrofitInstance.api.updateEmployee(id, updatedEmployee)
                fetchEmployees()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteEmployee(id: String) {
        viewModelScope.launch {
            try {
                RetrofitInstance.api.deleteEmployee(id)
                fetchEmployees()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}