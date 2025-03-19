package uk.ac.tees.mad.stox.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uk.ac.tees.mad.stox.model.dataclass.room.HomeScreenStockData
import uk.ac.tees.mad.stox.model.dataclass.room.HomeScreenStockDataItem

@Dao
interface HomeScreenStockDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeScreenStockData(data: HomeScreenStockData)

    @Query("SELECT * FROM home_screen_stock_data WHERE userId = :userId ORDER BY symbol")
    suspend fun getHomeScreenStockDataForUser(userId: String): List<HomeScreenStockData>

    @Query("SELECT * FROM home_screen_stock_data WHERE userId = :userId AND symbol = :symbol")
    suspend fun getHomeScreenStockDataForUserAndSymbol(
        userId: String, symbol: String
    ): HomeScreenStockData?

    @Query("DELETE FROM home_screen_stock_data WHERE userId = :userId")
    suspend fun deleteHomeScreenStockDataForUser(userId: String)

    @Query("DELETE FROM home_screen_stock_data WHERE userId = :userId AND symbol = :symbol")
    suspend fun deleteHomeScreenStockDataForUserAndSymbol(userId: String, symbol: String)

    @Query("UPDATE home_screen_stock_data SET stockData = :stockData AND timestamp = :timestamp WHERE userId = :userId AND symbol = :symbol")
    suspend fun updateHomeScreenStockData(
        userId: String, symbol: String, stockData: HomeScreenStockDataItem, timestamp: Long
    )

    @Query("SELECT COUNT(*) FROM home_screen_stock_data WHERE userId = :userId")
    suspend fun getHomeScreenStockDataCountForUser(userId: String): Int

    @Query("SELECT EXISTS (SELECT 1 FROM home_screen_stock_data WHERE userId = :userId AND symbol = :symbol)")
    fun isPresentinFavourites(userId: String, symbol: String): Boolean
}