package org.shufygoth.npcrl.rest.endpoints;

import io.javalin.http.Context;
import org.shufygoth.npcrl.environment.EnvironmentState;
import org.shufygoth.npcrl.environment.NpcEnv;
import org.shufygoth.npcrl.rest.EndpointType;

public class NpcTrainingResetEndpoint extends NpcHttpTrainingEndpoint {
    @Override
    public void onRequest(Context context, NpcEnv env) {
        EnvironmentState state = this.spigotCrossThreadWaiter.schedule(env::reset);
        if (state == null)
            context.status(505).result("state is null after reset");
        else context.status(200).json(state);
    }
    @Override
    public String path() {
        return "/reset";
    }

    @Override
    public EndpointType type() {
        return EndpointType.HTTP_GET;
    }
}
