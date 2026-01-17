package org.shufygoth.npcrl.rest;

import io.javalin.websocket.WsConfig;

import java.util.function.Consumer;

/**
 * Represents a web socket server endpoint.
 */
public interface WebsocketEndpoint extends Endpoint {
    Consumer<WsConfig> wsAccept();

    @Override
    default EndpointType type() {
        return EndpointType.WEBSOCKET;
    }
}
