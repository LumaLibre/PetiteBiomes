package net.lumamc.biomes.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class TextUtil {

    public static Component minimessage(String message) {
        return MiniMessage.miniMessage().deserialize(message);
    }
}
