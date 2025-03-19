package uk.ac.tees.mad.stox.model.repository

import uk.ac.tees.mad.stox.model.dataclass.room.HomeScreenStockData
import uk.ac.tees.mad.stox.model.dataclass.room.HomeScreenStockDataItem
import uk.ac.tees.mad.stox.model.room.HomeScreenStockDataDao

class HomeScreenStockDataRepository(private val homeScreenStockDataDao: HomeScreenStockDataDao) {
    suspend fun insertHomeScreenStockData(data: HomeScreenStockData) {
        homeScreenStockDataDao.insertHomeScreenStockData(data)
    }

    suspend fun getHomeScreenStockDataForUser(userId: String): List<HomeScreenStockData> {
        return homeScreenStockDataDao.getHomeScreenStockDataForUser(userId)
    }

    suspend fun getHomeScreenStockDataForUserAndSymbol(
        userId: String, symbol: String
    ): HomeScreenStockData? {
        return homeScreenStockDataDao.getHomeScreenStockDataForUserAndSymbol(userId, symbol)
    }

    suspend fun deleteHomeScreenStockDataForUser(userId: String) {
        homeScreenStockDataDao.deleteHomeScreenStockDataForUser(userId)
    }

    suspend fun deleteHomeScreenStockDataForUserAndSymbol(userId: String, symbol: String) {
        homeScreenStockDataDao.deleteHomeScreenStockDataForUserAndSymbol(userId, symbol)
    }

    suspend fun updateHomeScreenStockData(
        userId: String, symbol: String, stockData: HomeScreenStockDataItem, timestamp: Long
    ) {
        homeScreenStockDataDao.updateHomeScreenStockData(userId, symbol, stockData, timestamp)
    }

    suspend fun getHomeScreenStockDataCountForUser(userId: String): Int {
        return homeScreenStockDataDao.getHomeScreenStockDataCountForUser(userId)
    }

    suspend fun isPresentinFavourites(userId: String, symbol: String): Boolean {
        return homeScreenStockDataDao.isPresentinFavourites(userId, symbol)
    }
}