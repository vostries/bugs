package com.vostrik.buggame.api

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "ValCurs", strict = false)
data class ValCurs(
    @field:ElementList(entry = "Valute", inline = true, required = false)
    var valutes: List<Valute>? = null
)

@Root(name = "Valute", strict = false)
data class Valute(
    @field:Element(name = "NumCode", required = false)
    var numCode: String? = null,

    @field:Element(name = "CharCode", required = false)
    var charCode: String? = null,

    @field:Element(name = "Nominal", required = false)
    var nominal: Int? = null,

    @field:Element(name = "Name", required = false)
    var name: String? = null,

    @field:Element(name = "Value", required = false)
    var value: String? = null
)

data class GoldRate(
    val ratePerGram: Double,
    val date: String
)
