package monterey.example.pingpong;

import monterey.actor.Actor;
import monterey.actor.ActorContext;
import monterey.actor.ActorRef;
import monterey.actor.ActorSpec;
import monterey.actor.MessageContext;
import monterey.logging.Logger;
import monterey.logging.LoggerFactory;

import com.google.common.base.Preconditions;
import monterey.util.StringUtils;

public class PingActor implements Actor {
    private static final Logger LOG = new LoggerFactory().getLogger("Actor");

    public static final String MESSAGE_KEY = "message";
    public static final String MESSAGE_FORMAT = "%s: %d";

    private ActorContext context;
    private long count = 0L;
    private String message;
    
    public void init(ActorContext context) {
        Preconditions.checkArgument(context.getConfigurationParams().containsKey(MESSAGE_KEY), "Message configuration not set");
        this.context = context;
        this.message = context.getConfigurationParams().get(MESSAGE_KEY);
        LOG.info("Ping initialised with '%s'", message);

        ActorRef pong = context.newActor(new ActorSpec(PongActor.class.getName(), "Pong actor " + this.message, "Pong Actor created by Ping Actor " + this.message, context.getConfigurationParams()));
        context.sendTo(pong, "INIT");
    }

    public void onMessage(Object payload, MessageContext messageContext) {
        LOG.info("Ping got '%s'", (String) payload);
        context.sendTo(messageContext.getSource(), String.format(MESSAGE_FORMAT, message, ++count));
    }
}
