package org.shufygoth.npcrl.rest;

import io.javalin.Javalin;
import org.shufygoth.npcrl.rest.endpoints.EndpointManager;

public final class NpcEnvironmentRestServer {
    private final Javalin app;
    private final EndpointManager endpointManager;
    public NpcEnvironmentRestServer() {
        this.app = Javalin.create();
        this.endpointManager = new EndpointManager(this.app);
        this.app.start(8755);
    }

    public void stop() {
        if (this.app == null) return;
        this.app.stop();
    }
}
