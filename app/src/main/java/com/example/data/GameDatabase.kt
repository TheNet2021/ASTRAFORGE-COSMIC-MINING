package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "game_save")
data class GameSaveEntity(
    @PrimaryKey val id: Int = 1,
    val credits: Long = 100L,
    val darkMatter: Long = 0L,
    val energy: Int = 0,
    val playerLevel: Int = 1,
    val playerXp: Long = 0L,
    val equipmentLevel: Int = 0,
    val currentPlanetNumber: Int = 1,
    val unlockedPlanetsCsv: String = "1",
    val inventoryJson: String = "{}",
    val dronesJson: String = "[]",
    val facilitiesJson: String = "{}",
    val discoveredRelicsCsv: String = "",
    val craftedComponentsJson: String = "{}",
    val activeBoostsJson: String = "[]",
    val statsTotalTaps: Long = 0L,
    val statsTotalOresMined: Long = 0L,
    val statsTotalCreditsEarned: Long = 0L,
    val ascensionCount: Int = 0,
    val ascensionStellarShards: Long = 0L,
    val soundVolume: Float = 0.8f,
    val isMuted: Boolean = false,
    val screenShakeEnabled: Boolean = true,
    val batterySaverEnabled: Boolean = false,
    val researchJson: String = "{}",
    val claimedAchievementsCsv: String = "",
    val lastSavedTimestampMs: Long = System.currentTimeMillis()
)

@Dao
interface GameSaveDao {
    @Query("SELECT * FROM game_save WHERE id = 1 LIMIT 1")
    fun getSaveFlow(): Flow<GameSaveEntity?>

    @Query("SELECT * FROM game_save WHERE id = 1 LIMIT 1")
    suspend fun getSaveDirect(): GameSaveEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(save: GameSaveEntity)

    @Query("DELETE FROM game_save WHERE id = 1")
    suspend fun clearSave()
}

@Database(entities = [GameSaveEntity::class], version = 2, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameSaveDao(): GameSaveDao

    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun getInstance(context: Context): GameDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "astraforge_save.db"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
