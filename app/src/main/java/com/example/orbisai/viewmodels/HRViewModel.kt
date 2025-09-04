package com.example.orbisai.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class Employee(
    val id: Int,
    val name: String,
    val position: String,
    val department: String,
    val email: String,
    val phone: String,
    val avatar: String = ""
)

class HRViewModel : ViewModel() {
    var employees by mutableStateOf(
        listOf(
            Employee(1, "Ana García", "Desarrolladora Senior", "Tecnología", "ana.garcia@empresa.com", "+34 600 123 456"),
            Employee(2, "Carlos López", "Diseñador UX/UI", "Diseño", "carlos.lopez@empresa.com", "+34 600 234 567"),
            Employee(3, "María Rodríguez", "Product Manager", "Producto", "maria.rodriguez@empresa.com", "+34 600 345 678"),
            Employee(4, "David Martín", "Ingeniero DevOps", "Tecnología", "david.martin@empresa.com", "+34 600 456 789"),
            Employee(5, "Laura Sánchez", "Analista de Datos", "Analytics", "laura.sanchez@empresa.com", "+34 600 567 890"),
            Employee(6, "Javier Torres", "Marketing Manager", "Marketing", "javier.torres@empresa.com", "+34 600 678 901"),
            Employee(7, "Sofia Herrera", "Recursos Humanos", "RRHH", "sofia.herrera@empresa.com", "+34 600 789 012"),
            Employee(8, "Miguel Castro", "Contador", "Finanzas", "miguel.castro@empresa.com", "+34 600 890 123")
        )
    )
        private set
    
    var searchQuery by mutableStateOf("")
        private set
    
    val filteredEmployees: List<Employee>
        get() = if (searchQuery.isEmpty()) {
            employees
        } else {
            employees.filter { employee ->
                employee.name.contains(searchQuery, ignoreCase = true) ||
                employee.position.contains(searchQuery, ignoreCase = true) ||
                employee.department.contains(searchQuery, ignoreCase = true)
            }
        }
    
    fun updateSearchQuery(query: String) {
        searchQuery = query
    }
    
    fun addEmployee(employee: Employee) {
        employees = employees + employee
    }
    
    fun removeEmployee(employeeId: Int) {
        employees = employees.filter { it.id != employeeId }
    }
    
    fun updateEmployee(employee: Employee) {
        employees = employees.map { if (it.id == employee.id) employee else it }
    }
}
