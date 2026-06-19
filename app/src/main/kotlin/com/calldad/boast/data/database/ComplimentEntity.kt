package com.calldad.boast.data.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "compliments",
    indices = [
        Index(value = ["category"]),
        Index(value = ["isCustom"]),
        Index(value = ["category", "isCustom"])
    ]
)
data class ComplimentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val category: String,
    val isCustom: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)