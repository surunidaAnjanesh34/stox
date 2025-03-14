package uk.ac.tees.mad.stox.model.room

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import uk.ac.tees.mad.stox.model.dataclass.room.HomeScreenStockDataItem

@ProvidedTypeConverter
class HomeScreenStockDataItemTypeConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromHomeScreenStockDataItem(homeScreenStockDataItem: HomeScreenStockDataItem): String {
        return gson.toJson(homeScreenStockDataItem)
    }

    @TypeConverter
    fun toHomeScreenStockDataItem(homeScreenStockDataItemString: String): HomeScreenStockDataItem {
        val type = object : TypeToken<HomeScreenStockDataItem>() {}.type
        return gson.fromJson(homeScreenStockDataItemString, type)
    }
}