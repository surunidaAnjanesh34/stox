package uk.ac.tees.mad.stox.model.dataclass.alphavantage

import com.google.gson.annotations.SerializedName

data class GlobalQuoteResponse(
    @SerializedName("Global Quote") val globalQuote: GlobalQuote,
)

data class GlobalQuote(
    @SerializedName("01. symbol") val symbol: String,
    @SerializedName("02. open") val open: String,
    @SerializedName("03. high") val high: String,
    @SerializedName("04. low") val low: String,
    @SerializedName("05. price") val price: String,
    @SerializedName("06. volume") val volume: String,
    @SerializedName("07. latest trading day") val latestTradingDay: String,
    @SerializedName("08. previous close") val previousClose: String,
    @SerializedName("09. change") val change: String,
    @SerializedName("10. change percent") val changePercent: String,
)