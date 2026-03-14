package com.launcherx.data.dao

import androidx.room.*
import com.launcherx.data.entities.DockAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DockAppDao {
    @Query("SELECT * FROM dock_apps ORDER BY slot")
    fun getAllDockApps(): Flow<List<DockAppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDockApp(dockApp: DockAppEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDockApps(dockApps: List<DockAppEntity>)

    @Delete
    suspend fun deleteDockApp(dockApp: DockAppEntity)

    @Query("DELETE FROM dock_apps WHERE slot = :slot")
    suspend fun deleteBySlot(slot: Int)

    @Query("DELETE FROM dock_apps")
    suspend fun deleteAll()
}
