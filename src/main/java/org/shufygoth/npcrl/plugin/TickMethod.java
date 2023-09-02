package org.shufygoth.npcrl.plugin;

import net.minecraft.server.v1_8_R3.MinecraftServer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TickMethod {
    private final MinecraftServer server;
    private final Method tickMethod;
    public TickMethod() {
        try {
            this.tickMethod = MinecraftServer.class.getDeclaredMethod("A");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        this.tickMethod.setAccessible(true);
        this.server = MinecraftServer.getServer();
    }
    public void execute() {
        try {
            while(true) {
                tickMethod.invoke(server);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
