package com.vostrik.buggame.api

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "Metall", strict = false)
data class Metall(
    @field:ElementList(entry = "Record", inline = true, required = false)
    var records: List<Record>? = null
)

@Root(name = "Record", strict = false)
data class Record(
    @field:Attribute(name = "Date", required = false)
    var date: String? = null,

    @field:Attribute(name = "Code", required = false)
    var code: String? = null,

    @field:Element(name = "Buy", required = false)
    var buy: String? = null,

    @field:Element(name = "Sell", required = false)
    var sell: String? = null
)

data class GoldRate(
    val ratePerGram: Double,
    val date: String
)
