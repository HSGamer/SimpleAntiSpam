package me.hsgamer.simpleantispam;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatListener implements Listener {
    private final Map<UUID, Queue<String>> messages = new ConcurrentHashMap<>();
    private final Map<UUID, Long> time = new ConcurrentHashMap<>();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (player.hasPermission("simpleantispam.bypass")) {
            return;
        }

        // Anti Spam
        if (Boolean.TRUE.equals(MainConfig.ANTI_SPAM_ENABLE.getValue())) {
            if (!time.containsKey(uuid)) {
                time.put(uuid, System.currentTimeMillis());
            } else if (System.currentTimeMillis() - time.get(uuid) >= (MainConfig.ANTI_SPAM_DELAY.getValue() * 1000L)) {
                time.remove(uuid);
            } else {
                MessageUtils.sendMessage(player, MainConfig.ANTI_SPAM_MESSAGE.getValue());
                event.setCancelled(true);
                return;
            }
        }

        // Anti Repeat
        if (Boolean.TRUE.equals(MainConfig.ANTI_REPEAT_ENABLE.getValue())) {
            String message = event.getMessage().toLowerCase(Locale.ROOT);
            Queue<String> messageQueue = messages.computeIfAbsent(player.getUniqueId(), uuid1 -> new LinkedList<>());
            if (checkRepeat(message, messageQueue)) {
                MessageUtils.sendMessage(player, MainConfig.ANTI_REPEAT_MESSAGE.getValue());
                event.setCancelled(true);
            } else {
                if (messageQueue.size() == MainConfig.ANTI_REPEAT_STORE.getValue()) {
                    messageQueue.remove();
                }
                messageQueue.add(message);
            }
        }
    }

    private boolean checkRepeat(String message, Collection<String> storedMessages) {
        if (Boolean.TRUE.equals(MainConfig.ANTI_REPEAT_LEVENSHTEIN_ENABLED.getValue())) {
            int threshold = MainConfig.ANTI_REPEAT_LEVENSHTEIN_THRESHOLD.getValue();
            return storedMessages.parallelStream()
                    .map(s -> StringUtils.getLevenshteinDistance(message, s))
                    .anyMatch(i -> i < threshold);
        } else {
            return storedMessages.contains(message);
        }
    }
}
