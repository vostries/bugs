package com.vostrik.buggame.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CbrApi {
    @GET("scripts/xml_metall.asp")
    suspend fun getMetalRates(
        @Query("date_req1") dateFrom: String,
        @Query("date_req2") dateTo: String
    ): Response<Metall>
}
