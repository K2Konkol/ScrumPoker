package com.k2konkol.server

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.SynchronousSink
import java.time.Duration

@Component("PokerRoundHandler")
class PokerRoundHandler : WebSocketHandler {
    private val pokerMap: MutableMap<String, PokerRound> = HashMap()
    private fun readValue(round: String): PokerRound {
        var value = PokerRound()
        try {
            value = json.readValue(round, PokerRound::class.java)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
        return value
    }

    private fun writeArray(stringList: List<PokerRound>?): String {
        var value = ""
        try {
            value = json.writeValueAsString(stringList)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
        return value
    }

    private val pokerMono =
        Flux.generate { sink: SynchronousSink<List<PokerRound>?> -> sink.next(pokerMap.values.stream().toList()) }
    private val intervalFlux = Flux.interval(Duration.ofMillis(25))
        .zipWith(pokerMono) { time: Long?, element: List<PokerRound>? -> element }

    override fun handle(session: WebSocketSession): Mono<Void> {
        return session.send(intervalFlux.map { n: List<PokerRound>? -> writeArray(n) }
            .map { payload: String? -> session.textMessage(payload!!) })
            .and(session.receive()
                .map { obj: WebSocketMessage -> obj.payloadAsText }.log()
                .map { round: String -> readValue(round) }
                .map { pokerRound: PokerRound ->
                    pokerMap[session.id] = pokerRound
                    Mono.empty<Any?>()
                }).doOnTerminate { pokerMap.remove(session.id) }
    }

    companion object {
        private val json = ObjectMapper()
    }
}
