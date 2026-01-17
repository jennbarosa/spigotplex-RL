package org.shufygoth.npcrl.plugin;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Provides utility to cheese the spigot watchdog thread safely.
 * Use with caution. The cheese is permanent.
 */
public final class SpigotWatchdogCheese {
    public static final Map<String, Class<?>> classMemoizeMap = new HashMap<>();
    private static Class<?> getSpigotClass(String pathway) {
        String pathToMojang = "org.spigotmc.";
        String path = pathToMojang + pathway;
        Class<?> clazz;
        clazz = classMemoizeMap.computeIfAbsent(path, cls -> {
            try {
                return Class.forName(path);
            } catch (ClassNotFoundException e) {
                Bukkit.getLogger().warning("Unable to obtain Spigot class " + pathway + "\n" + e.getMessage());
                return null;
            }
        });
        return clazz;
    }

    private static <T> T getFieldValue(String fieldName, Object object, Class<?> clazz) {
        T value = null;
        try {
            // we do some trolling, hopefully this will work lmao
            if(!(object instanceof Class)) {
                // hack
                object = clazz.cast(object);
            }
            Field field = clazz.getDeclaredField(fieldName);
            boolean accessible = field.canAccess(object);
            if(!accessible)
                field.setAccessible(true);

            value = ((T) field.get(object));

            if(!accessible)
                field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        return value;
    }

    private static <T> Optional<T> getFieldValueSafe(String fieldName, Object object, Class<?> clazz) {
        return Optional.ofNullable(getFieldValue(fieldName, object, clazz));
    }

    private static void setFieldValue(String fieldName, Object object, Class<?> clazz, Object value) {
        try {
            // we do some trolling, hopefully this will work lmao
            if(!(object instanceof Class)) {
                // hack
                object = clazz.cast(object);
            }
            Field field = clazz.getDeclaredField(fieldName);
            boolean accessible = field.canAccess(object);
            if(!accessible)
                field.setAccessible(true);

            field.set(object, value);

            if(!accessible)
                field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void releaseTheDogs() {
        Class<?> watchdogThreadClass = getSpigotClass("WatchdogThread");
        Optional<?> maybeWatchdogThread
                = getFieldValueSafe("instance", null, watchdogThreadClass);
        if(maybeWatchdogThread.isEmpty()) {
            Bukkit.getLogger().warning("Could not cheese Spigot watchdog thread");
            return;
        }

        Object watchdogThread = maybeWatchdogThread.get();

        setFieldValue("stopping",
                watchdogThread,
                watchdogThreadClass,
                true
        );

        Bukkit.getLogger().warning("============================================================");
        Bukkit.getLogger().warning("Successfully cheesed Spigot watchdog thread. Happy debugging");
        Bukkit.getLogger().warning("============================================================");
    }
}

