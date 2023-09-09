package org.shufygoth.npcrl.environment;

import java.util.Map;

public final class ArenaInfoUtil {
    public static <T> T unbox(Map<String, ?> arenaInfo, String key) {
        return (T) arenaInfo.get(key);
    }
}
