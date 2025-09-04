package com.example.orbisai.data.local.dao

import androidx.room.*
import com.example.orbisai.data.models.Employee
import com.example.orbisai.data.models.EmployeeStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface EmployeeDao {
    
    @Query("SELECT * FROM employees ORDER BY name ASC")
    fun getAllEmployees(): Flow<List<Employee>>
    
    @Query("SELECT * FROM employees WHERE status = :status ORDER BY name ASC")
    fun getEmployeesByStatus(status: EmployeeStatus): Flow<List<Employee>>
    
    @Query("SELECT * FROM employees WHERE department = :department ORDER BY name ASC")
    fun getEmployeesByDepartment(department: String): Flow<List<Employee>>
    
    @Query("SELECT * FROM employees WHERE name LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchEmployees(query: String): Flow<List<Employee>>
    
    @Query("SELECT * FROM employees WHERE id = :employeeId")
    fun getEmployeeById(employeeId: Long): Flow<Employee?>
    
    @Query("SELECT COUNT(*) FROM employees WHERE status = :status")
    fun getEmployeeCountByStatus(status: EmployeeStatus): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM employees")
    fun getTotalEmployeeCount(): Flow<Int>
    
    @Query("SELECT AVG(salary) FROM employees WHERE status = :status")
    fun getAverageSalaryByStatus(status: EmployeeStatus): Flow<Double?>
    
    @Query("SELECT SUM(salary) FROM employees WHERE status = :status")
    fun getTotalSalaryByStatus(status: EmployeeStatus): Flow<Double?>
    
    @Query("SELECT * FROM employees WHERE hireDate BETWEEN :startDate AND :endDate ORDER BY hireDate DESC")
    fun getEmployeesByHireDateRange(startDate: Date, endDate: Date): Flow<List<Employee>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: Employee): Long
    
    @Update
    suspend fun updateEmployee(employee: Employee)
    
    @Delete
    suspend fun deleteEmployee(employee: Employee)
    
    @Query("DELETE FROM employees WHERE id = :employeeId")
    suspend fun deleteEmployeeById(employeeId: Long)
    
    @Query("DELETE FROM employees")
    suspend fun deleteAllEmployees()
    
    @Query("UPDATE employees SET status = :status WHERE id = :employeeId")
    suspend fun updateEmployeeStatus(employeeId: Long, status: EmployeeStatus)
    
    @Query("UPDATE employees SET salary = :salary WHERE id = :employeeId")
    suspend fun updateEmployeeSalary(employeeId: Long, salary: Double)
}
