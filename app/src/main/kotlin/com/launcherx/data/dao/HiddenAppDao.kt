package com.launcherx.data.dao

import androidx.room.*
import com.launcherx.data.entities.HiddenAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HiddenAppDao {
    @Query("SELECT * FROM hidden_apps")
    fun getAllHiddenApps(): Flow<List<HiddenAppEntity>>

    @Query("SELECT packageName FROM hidden_apps")
    suspend fun getHiddenPackageNames(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHiddenApp(hiddenApp: HiddenAppEntity)

    @Delete
    suspend fun deleteHiddenApp(hiddenApp: HiddenAppEntity)

    @Query("DELETE FROM hidden_apps WHERE packageName = :packageName")
    suspend fun deleteByPackageName(packageName: String)

    @Query("DELETE FROM hidden_apps")
    suspend fun deleteAll()
}
