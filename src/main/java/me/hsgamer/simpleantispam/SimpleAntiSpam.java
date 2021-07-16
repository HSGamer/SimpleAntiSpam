package me.hsgamer.simpleantispam;

import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;

public final class SimpleAntiSpam extends BasePlugin {
    private final MainConfig config = new MainConfig(this);

    @Override
    public void load() {
        MessageUtils.setPrefix(MainConfig.PREFIX::getValue);
        config.setup();
    }

    @Override
    public void enable() {
        registerListener(new ChatListener());
    }
}
