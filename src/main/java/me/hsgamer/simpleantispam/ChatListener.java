package me.hsgamer.simpleantispam;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.bukkit.utils.PermissionUtils;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.lang.Character.UnicodeBlock;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatListener implements Listener {
    private final Map<UUID, Queue<String>> messages = new ConcurrentHashMap<>();
    private final Map<UUID, Long> time = new ConcurrentHashMap<>();
    private final JaroWinklerSimilarity similarityChecker = new JaroWinklerSimilarity();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (player.hasPermission("simpleantispam.bypass")) {
            return;
        }

        // Anti Chat Unicode
        if (Boolean.TRUE.equals(MainConfig.ANTI_CHAT_UNICODE_ENABLED.getValue())) {
            String message = event.getMessage();
            List<UnicodeBlock> allowedBlocks = MainConfig.ANTI_CHAT_UNICODE_ALLOWED_BLOCKS.getValue();
            boolean cancelled = false;
            for (char c : message.toCharArray()) {
                if (!allowedBlocks.contains(UnicodeBlock.of(c))) {
                    cancelled = true;
                    break;
                }
            }
            if (cancelled) {
                MessageUtils.sendMessage(player, MainConfig.ANTI_CHAT_UNICODE_MESSAGE.getValue());
                event.setCancelled(true);
                return;
            }
        }

        // Anti Spam
        if (Boolean.TRUE.equals(MainConfig.ANTI_SPAM_ENABLE.getValue())) {
            if (!time.containsKey(uuid)) {
                time.put(uuid, System.currentTimeMillis());
            } else {
                int delay = PermissionUtils
                        .getNumbersFromPermissions(player, "simpleantispam.delay")
                        .map(Number::intValue)
                        .min(Integer::compareTo)
                        .orElse(MainConfig.ANTI_SPAM_DELAY.getValue());
                if (System.currentTimeMillis() - time.get(uuid) >= (delay * 1000L)) {
                    time.remove(uuid);
                } else {
                    MessageUtils.sendMessage(player, MainConfig.ANTI_SPAM_MESSAGE.getValue());
                    event.setCancelled(true);
                    return;
                }
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
        if (Boolean.TRUE.equals(MainConfig.ANTI_REPEAT_SIMILARITY_ENABLED.getValue())) {
            double threshold = MainConfig.ANTI_REPEAT_SIMILARITY_THRESHOLD.getValue();
            return storedMessages.parallelStream()
                    .map(s -> similarityChecker.apply(message, s))
                    .anyMatch(i -> i >= threshold);
        } else {
            return storedMessages.contains(message);
        }
    }
}
