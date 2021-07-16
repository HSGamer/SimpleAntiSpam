package me.hsgamer.simpleantispam;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.path.BooleanConfigPath;
import me.hsgamer.hscore.config.path.DoubleConfigPath;
import me.hsgamer.hscore.config.path.IntegerConfigPath;
import me.hsgamer.hscore.config.path.StringConfigPath;
import org.bukkit.plugin.Plugin;

public class MainConfig extends PathableConfig {
    public static final StringConfigPath PREFIX = new StringConfigPath("prefix", "&f[&cSimpleAntiChat&f] ");
    public static final BooleanConfigPath ANTI_SPAM_ENABLE = new BooleanConfigPath("anti-spam.enable", true);
    public static final StringConfigPath ANTI_SPAM_MESSAGE = new StringConfigPath("anti-spam.message", "&c&lDo &nnot&r&f chat so fast!");
    public static final IntegerConfigPath ANTI_SPAM_DELAY = new IntegerConfigPath("anti-spam.delay", 2);
    public static final BooleanConfigPath ANTI_REPEAT_ENABLE = new BooleanConfigPath("anti-repeat.enable", true);
    public static final StringConfigPath ANTI_REPEAT_MESSAGE = new StringConfigPath("anti-repeat.message", "&c&lDo &nnot&r&f repeat messages!");
    public static final IntegerConfigPath ANTI_REPEAT_STORE = new IntegerConfigPath("anti-repeat.store", 2);
    public static final BooleanConfigPath ANTI_REPEAT_SIMILARITY_ENABLED = new BooleanConfigPath("anti-repeat.check-similarity.enabled", false);
    public static final DoubleConfigPath ANTI_REPEAT_SIMILARITY_THRESHOLD = new DoubleConfigPath("anti-repeat.check-similarity.threshold", 0.8);

    public MainConfig(Plugin plugin) {
        super(new BukkitConfig(plugin, "config.yml"));
    }
}
