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


@Component("PokerRoundHandler")
public class PokerRoundHandler implements WebSocketHandler {

    private static final ObjectMapper json = new ObjectMapper();

    private final List<PokerRound> pokerList = new ArrayList<>();

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

    private String writeValue(PokerRound round) {
        String value = "";
        try {
            value = json.writeValueAsString(round);
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
        sink.next(pokerList);
            });


    private Flux<List<PokerRound>> intervalFlux = Flux.interval(Duration.ofMillis(500))
            .zipWith(pokerMono, (time, element) -> element);


    @Override
    public Mono<Void> handle(WebSocketSession session) {

        Mono.just(session.getId()).log().subscribe();

        return session.send(intervalFlux.map(n -> writeArray(n)).map(session::textMessage))
                .and(session.receive()
                        .map(WebSocketMessage::getPayloadAsText).log()
                .map(round -> readValue(round)).map(pokerList::add));
    }
}
