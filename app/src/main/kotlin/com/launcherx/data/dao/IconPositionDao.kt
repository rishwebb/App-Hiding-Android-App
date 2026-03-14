package com.launcherx.data.dao

import androidx.room.*
import com.launcherx.data.entities.IconPositionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IconPositionDao {
    @Query("SELECT * FROM icon_positions ORDER BY page, row, col")
    fun getAllPositions(): Flow<List<IconPositionEntity>>

    @Query("SELECT * FROM icon_positions WHERE page = :page ORDER BY row, col")
    fun getPositionsForPage(page: Int): Flow<List<IconPositionEntity>>

    @Query("SELECT MAX(page) FROM icon_positions")
    suspend fun getMaxPage(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosition(position: IconPositionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPositions(positions: List<IconPositionEntity>)

    @Delete
    suspend fun deletePosition(position: IconPositionEntity)

    @Query("DELETE FROM icon_positions WHERE packageName = :packageName")
    suspend fun deleteByPackageName(packageName: String)

    @Query("DELETE FROM icon_positions")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM icon_positions WHERE page = :page")
    suspend fun getCountForPage(page: Int): Int
}
