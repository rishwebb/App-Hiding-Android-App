package com.launcherx.data.dao

import androidx.room.*
import com.launcherx.data.entities.WidgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WidgetDao {
    @Query("SELECT * FROM widgets ORDER BY page, row, col")
    fun getAllWidgets(): Flow<List<WidgetEntity>>

    @Query("SELECT * FROM widgets WHERE page = :page")
    fun getWidgetsForPage(page: Int): Flow<List<WidgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidget(widget: WidgetEntity)

    @Delete
    suspend fun deleteWidget(widget: WidgetEntity)

    @Query("DELETE FROM widgets WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM widgets")
    suspend fun deleteAll()

    @Update
    suspend fun updateWidget(widget: WidgetEntity)
}
