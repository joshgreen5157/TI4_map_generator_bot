package ti4.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.experimental.UtilityClass;
import ti4.json.PersistenceManager;

@UtilityClass
public class GameMessageManager {

    private static final String GAME_MESSAGES_FILE = "GameMessages.json";

    public static synchronized void add(String gameName, String messageId, GameMessageType type, long gameSaveTime) {
        GameMessages allGameMessages = readFile();
        if (allGameMessages == null) {
            allGameMessages = new GameMessages(new HashMap<>());
        }

        List<GameMessage> messages = allGameMessages.gameNameToMessages.computeIfAbsent(gameName, k -> new ArrayList<>());
        messages.add(new GameMessage(messageId, type, gameSaveTime));

        persistFile(allGameMessages);
    }

    public static synchronized String replace(String gameName, String messageId, GameMessageType type, long gameSaveTime) {
        GameMessages allGameMessages = readFile();
        if (allGameMessages == null) {
            allGameMessages = new GameMessages(new HashMap<>());
        }

        List<GameMessage> messages = allGameMessages.gameNameToMessages.computeIfAbsent(gameName, k -> new ArrayList<>());

        String replacedMessageId = null;
        if (!messages.isEmpty()) {
            for (int i = 0; i < messages.size(); i++) {
                GameMessage message = messages.get(i);
                if (message.type == type) {
                    replacedMessageId = messages.remove(i).messageId();
                }
            }
        }

        messages.add(new GameMessage(messageId, type, gameSaveTime));

        persistFile(allGameMessages);

        return replacedMessageId;
    }

    public static synchronized void remove(List<String> gameNames) {
        GameMessages allGameMessages = readFile();
        if (allGameMessages == null) {
            return;
        }

        gameNames.forEach(allGameMessages.gameNameToMessages::remove);

        persistFile(allGameMessages);
    }

    public static synchronized Optional<String> remove(String gameName, GameMessageType type) {
        GameMessages allGameMessages = readFile();
        if (allGameMessages == null) {
            return Optional.empty();
        }

        List<GameMessage> messages = allGameMessages.gameNameToMessages.computeIfAbsent(gameName, k -> new ArrayList<>());
        GameMessage message = messages.stream()
            .filter(m -> m.type == type)
            .findFirst()
            .orElse(null);
        if (message == null) {
            return Optional.empty();
        }

        messages.remove(message);

        persistFile(allGameMessages);

        return Optional.of(message.messageId);
    }

    public static synchronized Optional<String> get(String gameName, GameMessageType type) {
        GameMessages allGameMessages = readFile();
        if (allGameMessages == null) {
            return Optional.empty();
        }

        List<GameMessage> messages = allGameMessages.gameNameToMessages.computeIfAbsent(gameName, k -> new ArrayList<>());
        return messages.stream()
            .filter(m -> m.type == type)
            .findFirst()
            .map(GameMessage::messageId);
    }

    private static GameMessages readFile() {
        try {
            GameMessages gameMessages = PersistenceManager.readObjectFromJsonFile(GAME_MESSAGES_FILE, GameMessages.class);
            return gameMessages != null ? gameMessages : new GameMessages(new HashMap<>());
        } catch (IOException e) {
            BotLogger.log("Failed to read json data for GameMessages.", e);
            return null;
        }
    }

    private static void persistFile(GameMessages toPersist) {
        try {
            PersistenceManager.writeObjectToJsonFile(GAME_MESSAGES_FILE, toPersist);
        } catch (Exception e) {
            BotLogger.log("Failed to write json data for GameMessages.", e);
        }
    }

    private record GameMessages(Map<String, List<GameMessage>> gameNameToMessages) {}

    public record GameMessage(String messageId, GameMessageType type, long gameSaveTime) {}
}
