package org.shufygoth.npcrl.rest.endpoints;

import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.shufygoth.npcrl.NpcRL;
import org.shufygoth.npcrl.environment.EnvironmentState;
import org.shufygoth.npcrl.environment.NpcAction;
import org.shufygoth.npcrl.environment.NpcEnv;
import org.shufygoth.npcrl.rest.SpigotCrossThreadWaiter;
import org.shufygoth.npcrl.rest.WebsocketEndpoint;

import java.util.function.Consumer;

/**
 * A web socket server that acts identical to the HTTP server, with all of the Npc environment control functionality into one endpoint, instead of multiple REST endpoints.
 * There should really only be one of these, because a web socket is not like a REST API server.
 */
public final class NpcWebsocketTrainingEndpoint implements WebsocketEndpoint {
    private final SpigotCrossThreadWaiter<EnvironmentState> spigotWaiter = new SpigotCrossThreadWaiter<>();
    @Override
    public Consumer<WsConfig> wsAccept() {
        return ws -> {
            ws.onMessage(this::onWsMessage);
            ws.onError(wsErrorContext -> Bukkit.broadcastMessage(String.format("%s%s", ChatColor.RED, wsErrorContext.error())));
            ws.onClose(ctx -> Bukkit.broadcastMessage(String.format("%sSocket disconnected", ChatColor.DARK_RED)));
        };
    }

    private void onWsMessage(WsMessageContext context) {
        NpcEnv env = NpcRL.plugin.env;
        if (context.message().trim().startsWith("step")) {
            this.onStepRequested(context, context.message(), env);
        } else if (context.message().trim().startsWith("reset")) {
            this.onResetRequested(context, context.message(), env);
        } else if (context.message().trim().startsWith("state")) {
            this.onStateRequested(context, context.message(), env);
        }
    }

    private void onStateRequested(WsMessageContext context, String message, NpcEnv env) {
        EnvironmentState state = this.spigotWaiter.schedule(env::getState);
        context.send(state);
    }

    private void onResetRequested(WsMessageContext context, String message, NpcEnv env) {
        EnvironmentState state = this.spigotWaiter.schedule(env::reset);
        if (state == null)
            context.send("error:state is null after reset");
        else context.send(state);
    }

    private void onStepRequested(WsMessageContext context, String message, NpcEnv env) {
        if (message.endsWith("p") || message.endsWith(":")) {
            context.send("error:Missing action");
            return;
        }

        String data = context.message().split(":")[1];
        int actionIndex = Integer.parseInt(data);
        if (actionIndex >= NpcAction.values().length || actionIndex < 0) {
            context.send("error:Invalid action");
            return;
        }

        NpcAction actionEnum = NpcAction.values()[actionIndex];
        long startTime = System.nanoTime();
        EnvironmentState state =  this.spigotWaiter.schedule(() -> env.step(actionEnum));
        long endTime = System.nanoTime();
        long elapsedTime = (endTime - startTime) / 1000000; // Convert nanoseconds to milliseconds
        //Bukkit.broadcastMessage(String.format("%sTraining step: %s%d ms", ChatColor.LIGHT_PURPLE, ChatColor.GOLD, elapsedTime));

        context.send(state);
    }

    @Override
    public String path() {
        return "/npc_rl";
    }
}
