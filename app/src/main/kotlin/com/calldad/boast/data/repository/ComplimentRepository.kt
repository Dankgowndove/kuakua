package com.calldad.boast.data.repository

import android.util.Log
import com.calldad.boast.data.COMPLIMENTS_LIST
import com.calldad.boast.data.database.ComplimentDao
import com.calldad.boast.data.database.ComplimentEntity
import kotlinx.coroutines.flow.Flow

/**
 * 夸赞语句数据仓库
 * 负责管理夸赞语句的数据访问逻辑
 */
class ComplimentRepository(
    private val complimentDao: ComplimentDao
) {
    
    /**
     * 获取所有夸赞语句
     */
    fun getAllCompliments(): Flow<List<ComplimentEntity>> {
        return complimentDao.getAllCompliments()
    }
    
    /**
     * 获取随机夸赞语句
     */
    suspend fun getRandomCompliment(): ComplimentEntity? {
        return complimentDao.getRandomCompliment()
    }
    
    /**
     * 根据分类获取夸赞语句
     */
    fun getComplimentsByCategory(category: String): Flow<List<ComplimentEntity>> {
        return complimentDao.getComplimentsByCategory(category)
    }
    
    /**
     * 获取自定义夸赞语句
     */
    fun getCustomCompliments(): Flow<List<ComplimentEntity>> {
        return complimentDao.getCustomCompliments()
    }
    
    /**
     * 添加自定义夸赞语句
     */
    suspend fun addCompliment(compliment: ComplimentEntity) {
        complimentDao.insertCompliment(compliment)
    }
    
    /**
     * 更新夸赞语句
     */
    suspend fun updateCompliment(compliment: ComplimentEntity) {
        complimentDao.updateCompliment(compliment)
    }
    
    /**
     * 删除夸赞语句
     */
    suspend fun deleteCompliment(id: Long) {
        complimentDao.deleteComplimentById(id)
    }
    
    /**
     * 获取统计信息
     */
    fun getStatistics(): Flow<Statistics> {
        return kotlinx.coroutines.flow.combine(
            complimentDao.getBuiltinCount(),
            complimentDao.getCustomCount(),
            complimentDao.getTotalCount()
        ) { builtin, custom, total ->
            Statistics(
                builtinCount = builtin,
                customCount = custom,
                totalCount = total
            )
        }
    }
    
    /**
     * 初始化数据库（填充默认数据）
     */
    suspend fun initializeDatabase() {
        try {
            val count = complimentDao.getCount()
            if (count == 0) {
                val builtinCompliments = COMPLIMENTS_LIST.mapIndexed { index, text ->
                    ComplimentEntity(
                        id = index.toLong() + 1,
                        text = text,
                        category = getCategoryForIndex(index),
                        isCustom = false
                    )
                }
                complimentDao.insertCompliments(builtinCompliments)
            }
        } catch (e: Exception) {
            Log.e("ComplimentRepository", "初始化数据库失败", e)
        }
    }
    
    /**
     * 统计信息数据类
     */
    data class Statistics(
        val builtinCount: Int,
        val customCount: Int,
        val totalCount: Int
    )

    companion object {
        private val CATEGORIES = listOf(
            "外貌", "能力", "性格", "努力", "社交",
            "综合", "创意", "鼓励", "暖心"
        )

        fun getCategoryForIndex(index: Int): String {
            return CATEGORIES[index / 5]
        }
    }
}