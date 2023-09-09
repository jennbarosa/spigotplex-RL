package org.shufygoth.npcrl.rest.endpoints;

import io.javalin.http.Context;
import org.shufygoth.npcrl.environment.EnvironmentState;
import org.shufygoth.npcrl.environment.NpcAction;
import org.shufygoth.npcrl.environment.NpcEnv;
import org.shufygoth.npcrl.rest.EndpointType;

import java.util.Optional;

/**
 * REST API ENDPOINT
 * <p>
 * /step
 */
public class NpcTrainingStepEndpoint extends NpcHttpTrainingEndpoint {
    @Override
    public void onRequest(Context context, NpcEnv env) {
        // get step action
        String data = context.queryParam("action");
        Optional<NpcAction> action = queryParamToNpcAction(data);
        if (action.isEmpty()) {
            context.status(451).result("invalid action parameter");
            return;
        }

        // run the environment step on the main spigot thread, then stall this thread (http server thread) until we get the next state back
        EnvironmentState state = this.spigotCrossThreadWaiter.schedule(() -> env.step(action.get()));
        context.status(200).json(state);
    }

    @Override
    public String path() {
        return "/step";
    }

    @Override
    public EndpointType type() {
        return EndpointType.HTTP_GET;
    }

    private Optional<NpcAction> queryParamToNpcAction(String queryParam) {
        if (queryParam == null || queryParam.isEmpty() || queryParam.isBlank())
            return Optional.empty();

        int actionIndex = Integer.parseInt(queryParam);
        if (actionIndex >= NpcAction.values().length || actionIndex < 0)
            return Optional.empty();

        return Optional.of(NpcAction.values()[actionIndex]);
    }
}
