package uk.ac.tees.mad.stox.model.serviceapi

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import uk.ac.tees.mad.stox.model.dataclass.alphavantage.GlobalQuoteResponse
import uk.ac.tees.mad.stox.model.dataclass.alphavantage.SearchResponse

interface alphaVantageApiService {
    @GET("query")
    suspend fun getGlobalQuote(
        @Query("function") function: String = "GLOBAL_QUOTE",
        @Query("symbol") symbol: String,
        @Query("datatype") datatype: String = "json",
        @Query("apikey") apiKey: String
    ): Response<GlobalQuoteResponse>

    @GET("query")
    suspend fun searchSymbol(
        @Query("function") function: String = "SYMBOL_SEARCH",
        @Query("keywords") keywords: String,
        @Query("datatype") datatype: String = "json",
        @Query("apikey") apiKey: String
    ): Response<SearchResponse>
}