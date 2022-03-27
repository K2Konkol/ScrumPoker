package com.k2konkol.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class PokerRound {
    @NonNull
    @JsonProperty("player") String player;
    @JsonProperty("rank") int rank;
}
