package monterey.example.pingpong;

import monterey.actor.Actor;
import monterey.actor.ActorContext;
import monterey.actor.MessageContext;
import monterey.logging.Logger;
import monterey.logging.LoggerFactory;

import com.google.common.base.Preconditions;

public class PongActor implements Actor {
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
        LOG.info("Pong initialised with '%s'", message);
    }

    public void onMessage(Object payload, MessageContext messageContext) {
        LOG.info("Pong got '%s'", (String) payload);
        context.sendTo(messageContext.getSource(), String.format(MESSAGE_FORMAT, message, ++count));
    }
}
