package me.hsgamer.simpleantispam;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
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
            Queue<String> messageQueue = messages.computeIfAbsent(uuid, uuid1 -> new LinkedList<>());
            if (messageQueue.contains(message)) {
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
}
