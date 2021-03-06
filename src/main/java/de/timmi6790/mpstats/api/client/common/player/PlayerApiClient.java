package de.timmi6790.mpstats.api.client.common.player;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.timmi6790.mpstats.api.client.AbstractApiClient;
import de.timmi6790.mpstats.api.client.common.board.exceptions.InvalidBoardNameException;
import de.timmi6790.mpstats.api.client.common.filter.models.Reason;
import de.timmi6790.mpstats.api.client.common.game.exceptions.InvalidGameNameRestException;
import de.timmi6790.mpstats.api.client.common.leaderboard.exceptions.InvalidLeaderboardCombinationRestException;
import de.timmi6790.mpstats.api.client.common.player.deserializers.GeneratedPlayerEntryDeserializer;
import de.timmi6790.mpstats.api.client.common.player.deserializers.InvalidPlayerNameRestExceptionDeserializer;
import de.timmi6790.mpstats.api.client.common.player.deserializers.PlayerEntryDeserializer;
import de.timmi6790.mpstats.api.client.common.player.deserializers.PlayerStatsDeserializer;
import de.timmi6790.mpstats.api.client.common.player.exceptions.InvalidPlayerNameRestException;
import de.timmi6790.mpstats.api.client.common.player.models.GeneratedPlayerEntry;
import de.timmi6790.mpstats.api.client.common.player.models.Player;
import de.timmi6790.mpstats.api.client.common.player.models.PlayerEntry;
import de.timmi6790.mpstats.api.client.common.player.models.PlayerStats;
import de.timmi6790.mpstats.api.client.common.stat.exceptions.InvalidStatNameRestException;
import de.timmi6790.mpstats.api.client.exception.BaseRestException;
import de.timmi6790.mpstats.api.client.exception.ExceptionHandler;
import de.timmi6790.mpstats.api.client.exception.exceptions.UnknownApiException;
import lombok.Getter;
import okhttp3.HttpUrl;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

import static lombok.AccessLevel.PROTECTED;

@Getter(PROTECTED)
public class PlayerApiClient<P extends Player> extends AbstractApiClient {
    private final Class<?> playerClass;

    public PlayerApiClient(final String baseUrl,
                           final String apiKey,
                           final String schema,
                           final ObjectMapper objectMapper,
                           final ExceptionHandler exceptionHandler,
                           final Class<P> playerClass) {
        super(baseUrl, apiKey, schema, objectMapper, exceptionHandler);

        this.playerClass = playerClass;

        final JavaType playerStatsType = this.getPlayerStatsType();
        this.getObjectMapper().registerModule(
                new SimpleModule()
                        .addDeserializer(PlayerStats.class, new PlayerStatsDeserializer<>(playerStatsType, playerClass))
                        .addDeserializer(GeneratedPlayerEntry.class, new GeneratedPlayerEntryDeserializer())
                        .addDeserializer(PlayerEntry.class, new PlayerEntryDeserializer())
                        .addDeserializer(InvalidPlayerNameRestException.class, new InvalidPlayerNameRestExceptionDeserializer())
        );

        exceptionHandler.registerException("player-1", InvalidPlayerNameRestException.class);
    }

    protected String getPlayerBaseUrl() {
        return this.getBaseSchemaUrl() + "/player";
    }

    protected final JavaType getPlayerStatsType() {
        return this.getObjectMapper().getTypeFactory().constructParametricType(PlayerStats.class, this.playerClass);
    }

    protected Optional<PlayerStats<P>> getPlayerStats(final HttpUrl.Builder httpBuilder,
                                                      final boolean includeEmptyEntries,
                                                      final ZonedDateTime saveTime,
                                                      final Set<Reason> filterReasons) throws InvalidGameNameRestException, InvalidStatNameRestException, InvalidPlayerNameRestException, InvalidLeaderboardCombinationRestException, InvalidBoardNameException {
        httpBuilder
                .addQueryParameter("includeEmptyEntries", String.valueOf(includeEmptyEntries))
                .addQueryParameter("saveTime", saveTime.toString());
        this.addFilterReasons(httpBuilder, filterReasons);
        try {
            return Optional.ofNullable(
                    this.getResponseThrow(
                            this.constructGetRequest(httpBuilder.build()),
                            this.getPlayerStatsType()
                    )
            );
        } catch (final InvalidGameNameRestException | InvalidStatNameRestException | InvalidBoardNameException | InvalidLeaderboardCombinationRestException | InvalidPlayerNameRestException e) {
            throw e;
        } catch (final BaseRestException baseRestException) {
            throw new UnknownApiException(baseRestException);
        }
    }

    public Optional<PlayerStats<P>> getPlayerGameStats(final String playerName,
                                                       final String gameName,
                                                       final String boardName,
                                                       final boolean includeEmptyEntries,
                                                       final Set<Reason> filterReasons) throws InvalidGameNameRestException, InvalidPlayerNameRestException, InvalidLeaderboardCombinationRestException, InvalidBoardNameException {
        return this.getPlayerGameStats(
                playerName,
                gameName,
                boardName,
                includeEmptyEntries,
                ZonedDateTime.now(),
                filterReasons
        );
    }

    public Optional<PlayerStats<P>> getPlayerGameStats(final String playerName,
                                                       final String gameName,
                                                       final String boardName,
                                                       final boolean includeEmptyEntries,
                                                       final ZonedDateTime saveTime,
                                                       final Set<Reason> filterReasons) throws InvalidGameNameRestException, InvalidPlayerNameRestException, InvalidLeaderboardCombinationRestException, InvalidBoardNameException {
        final HttpUrl.Builder httpBuilder = HttpUrl.parse(this.getPlayerBaseUrl() + "/" + playerName + "/stats/game/" + gameName + "/" + boardName)
                .newBuilder();
        try {
            return this.getPlayerStats(httpBuilder, includeEmptyEntries, saveTime, filterReasons);
        } catch (final InvalidStatNameRestException exception) {
            // Should never be thrown
            throw new UnknownApiException(exception);
        }
    }

    public Optional<PlayerStats<P>> getPlayerStatStats(final String playerName,
                                                       final String statName,
                                                       final String boardName,
                                                       final boolean includeEmptyEntries,
                                                       final Set<Reason> filterReasons) throws InvalidStatNameRestException, InvalidPlayerNameRestException, InvalidLeaderboardCombinationRestException, InvalidBoardNameException {
        return this.getPlayerStatStats(
                playerName,
                statName,
                boardName,
                includeEmptyEntries,
                ZonedDateTime.now(),
                filterReasons
        );
    }

    public Optional<PlayerStats<P>> getPlayerStatStats(final String playerName,
                                                       final String statName,
                                                       final String boardName,
                                                       final boolean includeEmptyEntries,
                                                       final ZonedDateTime saveTime,
                                                       final Set<Reason> filterReasons) throws InvalidStatNameRestException, InvalidPlayerNameRestException, InvalidLeaderboardCombinationRestException, InvalidBoardNameException {
        final HttpUrl.Builder httpBuilder = HttpUrl.parse(this.getPlayerBaseUrl() + "/" + playerName + "/stats/stat/" + statName + "/" + boardName)
                .newBuilder();
        try {
            return this.getPlayerStats(httpBuilder, includeEmptyEntries, saveTime, filterReasons);
        } catch (final InvalidGameNameRestException exception) {
            // Should never be thrown
            throw new UnknownApiException(exception);
        }
    }
}
