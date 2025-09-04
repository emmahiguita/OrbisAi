package com.example.orbisai.data.local.converters

import androidx.room.TypeConverter
import com.example.orbisai.data.models.EmployeeStatus
import com.example.orbisai.data.models.SaleStatus
import com.example.orbisai.domain.models.TransactionType
import com.example.orbisai.domain.models.TransactionStatus
import com.example.orbisai.domain.models.InvoiceStatus

class EnumConverters {
    
    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }
    
    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }
    
    @TypeConverter
    fun fromTransactionStatus(status: TransactionStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toTransactionStatus(value: String): TransactionStatus {
        return TransactionStatus.valueOf(value)
    }
    
    @TypeConverter
    fun fromInvoiceStatus(status: InvoiceStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toInvoiceStatus(value: String): InvoiceStatus {
        return InvoiceStatus.valueOf(value)
    }
    
    @TypeConverter
    fun fromEmployeeStatus(status: EmployeeStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toEmployeeStatus(value: String): EmployeeStatus {
        return EmployeeStatus.valueOf(value)
    }
    
    @TypeConverter
    fun fromSaleStatus(status: SaleStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toSaleStatus(value: String): SaleStatus {
        return SaleStatus.valueOf(value)
    }
}
