package me.hsgamer.simpleantispam;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.AdvancedConfigPath;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.StickyConfigPath;
import me.hsgamer.hscore.config.path.BooleanConfigPath;
import me.hsgamer.hscore.config.path.DoubleConfigPath;
import me.hsgamer.hscore.config.path.IntegerConfigPath;
import me.hsgamer.hscore.config.path.StringConfigPath;
import org.bukkit.plugin.Plugin;

import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public static final BooleanConfigPath ANTI_CHAT_UNICODE_ENABLED = new BooleanConfigPath("anti-chat-unicode.enable", true);
    public static final StringConfigPath ANTI_CHAT_UNICODE_MESSAGE = new StringConfigPath("anti-chat-unicode.message", "&c&lThere are invalid characters on your chat!");
    public static final StickyConfigPath<List<UnicodeBlock>> ANTI_CHAT_UNICODE_ALLOWED_BLOCKS = new StickyConfigPath<>(
            new AdvancedConfigPath<List<String>, List<UnicodeBlock>>("anti-chat-unicode.allowed-blocks", Arrays.asList(
                    UnicodeBlock.BASIC_LATIN,
                    UnicodeBlock.LATIN_1_SUPPLEMENT,
                    UnicodeBlock.LATIN_EXTENDED_A,
                    UnicodeBlock.GENERAL_PUNCTUATION,
                    UnicodeBlock.CYRILLIC,
                    UnicodeBlock.CYRILLIC_EXTENDED_A
            )) {
                @Override
                public List<String> getFromConfig(Config config) {
                    return CollectionUtils.createStringListFromObject(config.get(getPath(), ""), true);
                }

                @Override
                public List<UnicodeBlock> convert(List<String> rawValue) {
                    List<UnicodeBlock> list = new ArrayList<>();
                    rawValue.forEach(name -> {
                        try {
                            list.add(UnicodeBlock.forName(name));
                        } catch (Exception e) {
                            // IGNORED
                        }
                    });
                    return list;
                }

                @Override
                public List<String> convertToRaw(List<UnicodeBlock> value) {
                    List<String> list = new ArrayList<>();
                    value.forEach(block -> list.add(block.toString()));
                    return list;
                }
            }
    );

    public MainConfig(Plugin plugin) {
        super(new BukkitConfig(plugin, "config.yml"));
    }
}
