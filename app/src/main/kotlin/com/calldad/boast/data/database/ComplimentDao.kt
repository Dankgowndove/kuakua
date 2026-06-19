package com.calldad.boast.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ComplimentDao {
    
    @Query("SELECT * FROM compliments")
    fun getAllCompliments(): Flow<List<ComplimentEntity>>
    
    @Query("SELECT * FROM compliments WHERE isCustom = 0")
    fun getBuiltinCompliments(): Flow<List<ComplimentEntity>>
    
    @Query("SELECT * FROM compliments WHERE isCustom = 1")
    fun getCustomCompliments(): Flow<List<ComplimentEntity>>
    
    @Query("SELECT * FROM compliments WHERE category = :category")
    fun getComplimentsByCategory(category: String): Flow<List<ComplimentEntity>>
    
    @Query("SELECT COUNT(*) FROM compliments WHERE isCustom = 0")
    fun getBuiltinCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM compliments WHERE isCustom = 1")
    fun getCustomCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM compliments")
    fun getTotalCount(): Flow<Int>
        
    @Query("SELECT COUNT(*) FROM compliments")
    suspend fun getCount(): Int    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompliment(compliment: ComplimentEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompliments(compliments: List<ComplimentEntity>)
    
    @Update
    suspend fun updateCompliment(compliment: ComplimentEntity)
    
    @Delete
    suspend fun deleteCompliment(compliment: ComplimentEntity)
    
    @Query("DELETE FROM compliments WHERE id = :id")
    suspend fun deleteComplimentById(id: Long)
    
    @Query("DELETE FROM compliments WHERE isCustom = 1")
    suspend fun deleteAllCustomCompliments()
    
    @Query("DELETE FROM compliments")
    suspend fun deleteAllCompliments()
    
    @Query("SELECT * FROM compliments ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomCompliment(): ComplimentEntity?
}