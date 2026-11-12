package com.vostrik.buggame.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CbrApi {
    @GET("scripts/XML_daily.asp")
    suspend fun getDailyRates(
        @Query("date_req") date: String? = null
    ): Response<ValCurs>
}
