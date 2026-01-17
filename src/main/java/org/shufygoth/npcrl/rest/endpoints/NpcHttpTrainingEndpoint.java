package org.shufygoth.npcrl.rest.endpoints;

import io.javalin.http.Context;
import org.shufygoth.npcrl.NpcRL;
import org.shufygoth.npcrl.environment.EnvironmentState;
import org.shufygoth.npcrl.environment.NpcEnv;
import org.shufygoth.npcrl.rest.EndpointType;
import org.shufygoth.npcrl.rest.HttpEndpoint;
import org.shufygoth.npcrl.rest.SpigotCrossThreadWaiter;

/**
 * Deprecated. Use {@see NpcWebSocketTrainingEndpoint} for much better performance.
 * <p>
 * Base abstract class that exposes different utility parameters to the npc endpoints that extend this class.
 * This endpoint is meant to be registered by a Javalin app instance.
 * Class reserved for more future usage
 */
@Deprecated
public abstract class NpcHttpTrainingEndpoint implements HttpEndpoint {
    protected final SpigotCrossThreadWaiter<EnvironmentState> spigotCrossThreadWaiter;
    public NpcHttpTrainingEndpoint() {
        this.spigotCrossThreadWaiter = new SpigotCrossThreadWaiter<>();
    }

    @Override
    public void accept(Context context) {
        this.onRequest(context, NpcRL.plugin.env);
    }

    /**
     * Equivalent to the Endpoint interface accept() method, but injects the current NpcEnv training environment.
     * @param context The javalin request context. Used to respond to the request
     * @param env The current Npc training environment
     */
    public void onRequest(Context context, NpcEnv env) {
        context.status(500);
    }

    @Override
    public abstract String path();

    @Override
    public EndpointType type() {
        return EndpointType.HTTP_GET;
    }
}
