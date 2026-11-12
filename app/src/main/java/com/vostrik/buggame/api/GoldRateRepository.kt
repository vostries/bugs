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

            for (i in 0..7) {
                val dateStr = dateFormat.format(calendar.time)
                val response = api.getDailyRates(dateStr)

                if (response.isSuccessful) {
                    val valCurs = response.body()
                    val gold = valCurs?.valutes?.find { it.charCode == "XAU" }

                    if (gold != null && gold.value != null) {
                        val valueStr = gold.value!!.replace(",", ".")
                        val ratePerTroyOunce = valueStr.toDoubleOrNull() ?: return@withContext null
                        val nominal = gold.nominal ?: 1
                        val ratePerGram = (ratePerTroyOunce * nominal) / 31.1034768

                        return@withContext GoldRate(ratePerGram, dateStr)
                    }
                }

                calendar.add(Calendar.DAY_OF_YEAR, -1)
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
