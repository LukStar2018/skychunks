package de.johni0702.minecraft.bobby;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Set;

import static net.minecraft.util.GsonHelper.getAsString;

public class SkyblockManager {
    // Diese Modi sollen laut deiner Anforderung komplett ignoriert / nicht gecacht werden
    private static final Set<String> BLACKLISTED_MODES = Set.of(
            "dynamic",          // Private Inseln, fremde Inseln
            "crystal_hollows",  // Kristallhöhlen
            "dungeon"           // Dungeons
    );

    private static String currentSkyblockMode = null;
    private static boolean allowCaching = true;

    public static void handleLocrawResponse(String chatMessage) {
        try {
            if (!chatMessage.startsWith("{") || !chatMessage.contains("SKYBLOCK")) {
                return;
            }

            JsonObject json = JsonParser.parseString(chatMessage).getAsJsonObject();
            if (json.has("mode")) {
                String mode = json.get("mode").getAsString().toLowerCase();
                currentSkyblockMode = mode;

                // Prüfen, ob der Modus auf der Blacklist steht
                if (BLACKLISTED_MODES.contains(mode)) {
                    allowCaching = false;
                } else {
                    allowCaching = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isCachingAllowed() {
        return allowCaching;
    }

    public static String getCurrentMode() {
        return currentSkyblockMode;
    }
}