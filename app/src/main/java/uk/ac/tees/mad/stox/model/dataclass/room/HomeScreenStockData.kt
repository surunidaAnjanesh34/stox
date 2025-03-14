package uk.ac.tees.mad.stox.model.dataclass.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import uk.ac.tees.mad.stox.model.room.HomeScreenStockDataItemTypeConverter

@Entity(tableName = "home_screen_stock_data")
data class HomeScreenStockData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val symbol: String,
    @TypeConverters(HomeScreenStockDataItemTypeConverter::class)
    val stockData: HomeScreenStockDataItem,
    val timestamp: Long = System.currentTimeMillis()
)

data class HomeScreenStockDataItem(
    val open: String,
    val high: String,
    val low: String,
    val price: String,
    val volume: String,
    val latestTradingDay: String,
    val previousClose: String,
    val change: String,
    val changePercent: String,
)