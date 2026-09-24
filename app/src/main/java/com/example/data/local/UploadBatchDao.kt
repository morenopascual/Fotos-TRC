package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UploadBatchDao {
    @Query("SELECT * FROM upload_batches ORDER BY timestamp DESC")
    fun getAllBatches(): Flow<List<UploadBatchEntity>>

    @Query("SELECT * FROM upload_batches WHERE category = :category ORDER BY timestamp DESC")
    fun getBatchesByCategory(category: String): Flow<List<UploadBatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: UploadBatchEntity): Long

    @Query("DELETE FROM upload_batches WHERE id = :id")
    suspend fun deleteBatchById(id: Long)

    @Query("DELETE FROM upload_batches")
    suspend fun clearAllBatches()
}
