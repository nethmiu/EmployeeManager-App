package com.nethmi.employeemanager.data

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("employees")
    suspend fun getEmployees(): List<Employee>

    @POST("employees")
    suspend fun addEmployee(@Body employee: Employee): Employee

    @PUT("employees/{id}")
    suspend fun updateEmployee(@Path("id") id: String, @Body employee: Employee): Employee

    @DELETE("employees/{id}")
    suspend fun deleteEmployee(@Path("id") id: String): Employee
}