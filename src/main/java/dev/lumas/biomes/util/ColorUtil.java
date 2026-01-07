package dev.lumas.biomes.util;

public class ColorUtil {

    public static int parseHexColor(String hexColor) {
        if (hexColor == null || hexColor.isEmpty()) {
            return -1;
        } else if (hexColor.startsWith("#")) {
            hexColor = hexColor.substring(1);
        }
        return Integer.parseInt(hexColor, 16);
    }
}
