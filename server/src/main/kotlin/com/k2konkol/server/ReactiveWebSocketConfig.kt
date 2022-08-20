package com.k2konkol.server

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import java.util.HashMap

@Configuration
class ReactiveWebSocketConfig {

    @Autowired
    @Qualifier("PokerRoundHandler")
    var webSocketHandler: WebSocketHandler? = null

    @Bean
    fun webSocketHandlerMapping(): HandlerMapping {
        val map: MutableMap<String, WebSocketHandler?> = HashMap()
        map["/play"] = webSocketHandler
        val handlerMapping = SimpleUrlHandlerMapping()
        handlerMapping.order = 1
        handlerMapping.urlMap = map
        return handlerMapping
    }

    @Bean
    fun handlerAdapter(): WebSocketHandlerAdapter {
        return WebSocketHandlerAdapter()
    }

}
