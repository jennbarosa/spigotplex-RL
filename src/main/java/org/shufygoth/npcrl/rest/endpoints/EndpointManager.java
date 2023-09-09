package org.shufygoth.npcrl.rest.endpoints;

import io.javalin.Javalin;
import org.shufygoth.npcrl.rest.Endpoint;
import org.shufygoth.npcrl.rest.HttpEndpoint;
import org.shufygoth.npcrl.rest.WebsocketEndpoint;

import java.util.ArrayList;
import java.util.List;

public class EndpointManager {
    private final List<Endpoint> endpoints;
    private final Javalin appInstance;

    public EndpointManager(Javalin appInstance) {
        this.appInstance = appInstance;
        this.endpoints = new ArrayList<>();
        this.defineEndpoints();
        this.registerEndpointsWithApp();
    }
    private void defineEndpoints() {
        //
        // Register REST API endpoints
        //
        this.endpoints.add(new NpcTrainingStepEndpoint());
        this.endpoints.add(new NpcTrainingResetEndpoint());
        this.endpoints.add(new NpcTrainingGetStateEndpoint());
        this.endpoints.add(new NpcWebsocketTrainingEndpoint());
    }
    private void registerEndpointsWithApp() {
        if (appInstance == null) return;
        endpoints.forEach(this::addEndpointToJavalinApp);
    }
    private void addEndpointToJavalinApp(Endpoint endpoint) {
        if (endpoint instanceof HttpEndpoint httpEndpoint) {
            switch (endpoint.type()) {
                case HTTP_GET -> this.appInstance.get(endpoint.path(), httpEndpoint::accept);
                case HTTP_POST -> this.appInstance.post(endpoint.path(), httpEndpoint::accept);
            }
        } else if (endpoint instanceof WebsocketEndpoint wsEndpoint) {
            this.appInstance.ws(wsEndpoint.path(), wsEndpoint.wsAccept());
        }
    }
}
