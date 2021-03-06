package de.timmi6790.mpstats.api.client.java.player.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.timmi6790.mpstats.api.client.java.player.models.JavaPlayer;

import java.io.IOException;
import java.io.Serial;
import java.util.UUID;

public class JavaPlayerDeserializer extends StdDeserializer<JavaPlayer> {
    @Serial
    private static final long serialVersionUID = 1972105464341747687L;

    public JavaPlayerDeserializer() {
        super(JavaPlayer.class);
    }

    @Override
    public JavaPlayer deserialize(final JsonParser jsonParser, final DeserializationContext ctxt) throws IOException {
        final JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        return new JavaPlayer(
                node.get("name").textValue(),
                UUID.fromString(node.get("uuid").textValue())
        );
    }
}
