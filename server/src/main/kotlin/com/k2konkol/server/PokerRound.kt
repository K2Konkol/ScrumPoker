package com.k2konkol.server

import com.fasterxml.jackson.annotation.JsonProperty

data class PokerRound(
    @JsonProperty("player")
    val player: String = "",

    @JsonProperty("rank")
    val rank: Int = 0
)
