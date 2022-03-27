package com.k2konkol.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Component("PokerRoundHandler")
public class PokerRoundHandler implements WebSocketHandler {

    private static final ObjectMapper json = new ObjectMapper();
    private final Map<String, PokerRound> pokerMap = new HashMap<>();

    private PokerRound readValue(String round) {
        PokerRound value = new PokerRound();
        try {
            value = json.readValue(round, PokerRound.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return value;
    }

    private String writeArray(List<PokerRound> stringList) {
        String value = "";
        try {
            value = json.writeValueAsString(stringList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return value;
    }

    private Flux<List<PokerRound>> pokerMono = Flux.generate(sink -> {
        sink.next(pokerMap.values().stream().toList());
            });

    private Flux<List<PokerRound>> intervalFlux = Flux.interval(Duration.ofMillis(25))
            .zipWith(pokerMono, (time, element) -> element);

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(intervalFlux.map(n -> writeArray(n)).map(session::textMessage))
                .and(session.receive()
                        .map(WebSocketMessage::getPayloadAsText).log()
                .map(round -> readValue(round))
                        .map(pokerRound -> {pokerMap.put(session.getId(), pokerRound);
                            return Mono.empty();
                        })).doOnTerminate(() -> pokerMap.remove(session.getId()));
    }
}
