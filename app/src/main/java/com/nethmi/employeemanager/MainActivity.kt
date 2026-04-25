package com.nethmi.employeemanager

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nethmi.employeemanager.ui.theme.EmployeeManagerTheme
import com.nethmi.employeemanager.ui.theme.EmployeeScreen
import com.nethmi.employeemanager.ui.theme.LoginScreen
import com.nethmi.employeemanager.viewmodel.EmployeeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        setContent {
            var isDarkMode by remember { mutableStateOf(false) }

            EmployeeManagerTheme(darkTheme = isDarkMode) {
                var isLoggedIn by remember {
                    mutableStateOf(sharedPref.getBoolean("isLoggedIn", false))
                }

                Surface(color = MaterialTheme.colorScheme.background) {
                    if (isLoggedIn) {
                        //  create ViewModel with Context
                        val employeeViewModel: EmployeeViewModel = viewModel(
                            factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                        )

                        EmployeeScreen(
                            viewModel = employeeViewModel,
                            isDarkMode = isDarkMode,
                            onThemeChange = { isDarkMode = it },
                            onLogout = {
                                sharedPref.edit().putBoolean("isLoggedIn", false).apply()
                                isLoggedIn = false
                            }
                        )
                    } else {
                        LoginScreen(onLoginSuccess = {
                            sharedPref.edit().putBoolean("isLoggedIn", true).apply()
                            isLoggedIn = true
                        })
                    }
                }
            }
        }
    }
}