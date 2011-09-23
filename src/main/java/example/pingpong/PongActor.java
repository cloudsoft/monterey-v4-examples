package example.pingpong;

import java.util.concurrent.atomic.AtomicLong;

import monterey.actor.Actor;
import monterey.actor.ActorContext;
import monterey.actor.MessageContext;

public class PongActor implements Actor {
    private ActorContext context;
    private AtomicLong count = new AtomicLong(0L);
    private String message = "PONG %d";
    
    public void init(ActorContext context) {
        this.context = context;
    }

    public void onMessage(Object payload, MessageContext messageContext) {
        System.err.printf("Pong got '%s'", (String) payload);
        context.sendTo(messageContext.getSource(), String.format(message, count.incrementAndGet()));
    }
}
