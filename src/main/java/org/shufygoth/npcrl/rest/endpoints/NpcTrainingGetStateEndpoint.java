package org.shufygoth.npcrl.rest.endpoints;

import io.javalin.http.Context;
import org.shufygoth.npcrl.environment.EnvironmentState;
import org.shufygoth.npcrl.environment.NpcEnv;
import org.shufygoth.npcrl.rest.EndpointType;

public class NpcTrainingGetStateEndpoint extends NpcHttpTrainingEndpoint {
    @Override
    public void onRequest(Context context, NpcEnv env) {
        EnvironmentState state = this.spigotCrossThreadWaiter.schedule(env::getState);
        context.status(200).json(state);
    }
    @Override
    public String path() {
        return "/state";
    }

    @Override
    public EndpointType type() {
        return EndpointType.HTTP_GET;
    }
}
