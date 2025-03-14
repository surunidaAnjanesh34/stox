package uk.ac.tees.mad.stox.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uk.ac.tees.mad.stox.model.dataclass.room.HomeScreenStockData

@Database(entities = [HomeScreenStockData::class], version = 1, exportSchema = false)
@TypeConverters(HomeScreenStockDataItemTypeConverter::class)
abstract class StoxDatabase : RoomDatabase() {
    abstract fun homeScreenStockDataDao(): HomeScreenStockDataDao
}