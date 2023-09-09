package org.shufygoth.npcrl.rest;

import io.javalin.http.Context;

import java.util.function.Consumer;

/**
 * An HTTP REST API endpoint which consumes a Javalin Context object used for returning the response to the requester
 * To create a new REST API endpoint, Implement this interface.
 * <p>
 * Have the "path" method return the endpoint with a leading forward slash.
 * <p>
 * example: return "/endpoint"
 * <p>
 *     <p>
 * Have the "method" method return the EndpointType enum that this endpoint is valid for.
 * <p>
 * example: return EndpointType.GET
 * <p>
 * meaning: This endpoint will only take GET requests.
 */
public interface Endpoint {
    String path();
    EndpointType type();
}
