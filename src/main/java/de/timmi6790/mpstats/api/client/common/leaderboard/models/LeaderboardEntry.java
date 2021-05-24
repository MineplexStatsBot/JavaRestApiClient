package de.timmi6790.mpstats.api.client.common.leaderboard.models;

import de.timmi6790.mpstats.api.client.common.player.models.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class LeaderboardEntry<P extends Player> {
    private final P player;
    private final long score;
}
