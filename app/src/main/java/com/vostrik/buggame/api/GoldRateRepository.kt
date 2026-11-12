package com.vostrik.buggame.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class GoldRateRepository {
    private val api: CbrApi = Retrofit.Builder()
        .baseUrl("https://www.cbr.ru/")
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build()
        .create(CbrApi::class.java)

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)

    suspend fun getGoldRate(): GoldRate? = withContext(Dispatchers.IO) {
        try {
            val calendar = Calendar.getInstance()
            val dateTo = dateFormat.format(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, -7)
            val dateFrom = dateFormat.format(calendar.time)

            val response = api.getMetalRates(dateFrom, dateTo)

            if (response.isSuccessful) {
                val metall = response.body()
                val goldRecords = metall?.records?.filter { it.code == "1" }

                if (!goldRecords.isNullOrEmpty()) {
                    val lastRecord = goldRecords.last()
                    val buyStr = lastRecord.buy?.replace(",", ".") ?: return@withContext null
                    val ratePerGram = buyStr.toDoubleOrNull() ?: return@withContext null

                    return@withContext GoldRate(ratePerGram, lastRecord.date ?: dateTo)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }

        null
    }

    companion object {
        @Volatile
        private var instance: GoldRateRepository? = null

        fun getInstance(): GoldRateRepository {
            return instance ?: synchronized(this) {
                instance ?: GoldRateRepository().also { instance = it }
            }
        }
    }
}
