package org.shufygoth.npcrl.rest;

import io.javalin.http.Context;
import org.shufygoth.npcrl.environment.NpcEnv;

public interface HttpEndpoint extends Endpoint {
    void accept(Context context);
}
