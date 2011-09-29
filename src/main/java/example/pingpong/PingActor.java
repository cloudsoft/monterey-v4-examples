package example.pingpong;

import java.util.concurrent.atomic.AtomicLong;

import com.google.common.base.Preconditions;

import monterey.actor.Actor;
import monterey.actor.ActorContext;
import monterey.actor.ActorRef;
import monterey.actor.MessageContext;
import monterey.logging.Logger;
import monterey.logging.LoggerFactory;

public class PingActor implements Actor {
    private static final Logger LOG = new LoggerFactory().getLogger("Actor");

    public static final String MESSAGE_KEY = "message";
    public static final String MESSAGE_FORMAT = "%s: %d";

    private ActorContext context;
    private AtomicLong count = new AtomicLong(0L);
    private String message;
    private ActorRef pong;
    
    public void init(ActorContext context) {
        Preconditions.checkArgument(context.getConfigurationParams().containsKey(MESSAGE_KEY), "Message configuration not set");
        this.context = context;
        this.message = context.getConfigurationParams().get(MESSAGE_KEY);
        LOG.info("Ping initialised with '%s'", message);

        pong = context.lookupActor("pong");
        context.sendTo(pong, "INIT");
    }

    public void onMessage(Object payload, MessageContext messageContext) {
        LOG.info("Ping got '%s'", (String) payload);
        context.sendTo(messageContext.getSource(), String.format(MESSAGE_FORMAT, message, count.incrementAndGet()));
    }
}
