package com.example.orbisai.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.orbisai.data.local.converters.DateConverter
import java.util.Date

@Entity(tableName = "employees")
@TypeConverters(DateConverter::class)
data class Employee(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val phone: String,
    val position: String,
    val department: String,
    val salary: Double,
    val hireDate: Date,
    val status: EmployeeStatus = EmployeeStatus.ACTIVE,
    val avatarUrl: String? = null,
    val notes: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class EmployeeStatus {
    ACTIVE,
    INACTIVE,
    ON_LEAVE,
    TERMINATED
}
