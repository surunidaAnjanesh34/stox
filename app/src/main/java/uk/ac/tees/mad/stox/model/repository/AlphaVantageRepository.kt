package uk.ac.tees.mad.stox.model.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import uk.ac.tees.mad.stox.model.dataclass.alphavantage.GlobalQuoteResponse
import uk.ac.tees.mad.stox.model.dataclass.alphavantage.SearchResponse
import uk.ac.tees.mad.stox.model.serviceapi.alphaVantageApiService
import java.io.IOException

class AlphaVantageRepository(private val apiService: alphaVantageApiService) {
    private val API_KEY = "ULUBLAENL0134MJV"

    suspend fun getGlobalQuote(symbol: String): Result<GlobalQuoteResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getGlobalQuote(symbol = symbol, apiKey = API_KEY)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Result.success(body)
                    } else {
                        Result.failure(Exception("Response body is null"))
                    }
                } else {
                    Result.failure(Exception("API error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: IOException) {
                // Handle network errors
                e.printStackTrace()
                Result.failure(Exception("Network error: ${e.message}"))
            } catch (e: HttpException) {
                // Handle HTTP errors (e.g., 404 Not Found, 500 Internal Server Error)
                e.printStackTrace()
                Result.failure(Exception("HTTP error: ${e.code()} - ${e.message}"))
            } catch (e: Exception) {
                // Handle other errors
                e.printStackTrace()
                Result.failure(Exception("An unexpected error occurred: ${e.message}"))
            }
        }
    }

    suspend fun searchSymbol(keywords: String): Result<SearchResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchSymbol(keywords = keywords, apiKey = API_KEY)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Result.success(body)
                    } else {
                        Result.failure(Exception("Response body is null"))
                    }
                } else {
                    Result.failure(Exception("API error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: IOException) {
                // Handle network errors
                e.printStackTrace()
                Result.failure(Exception("Network error: ${e.message}"))
            } catch (e: HttpException) {
                // Handle HTTP errors (e.g., 404 Not Found, 500 Internal Server Error)
                e.printStackTrace()
                Result.failure(Exception("HTTP error: ${e.code()} - ${e.message}"))
            } catch (e: Exception) {
                // Handle other errors
                e.printStackTrace()
                Result.failure(Exception("An unexpected error occurred: ${e.message}"))
            }
        }
    }
}