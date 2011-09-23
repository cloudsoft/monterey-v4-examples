package example.pingpong;

import java.util.concurrent.atomic.AtomicLong;

import monterey.actor.Actor;
import monterey.actor.ActorContext;
import monterey.actor.ActorRef;
import monterey.actor.MessageContext;

public class PingActor implements Actor {
    private ActorContext context;
    private ActorRef pong;
    private AtomicLong count = new AtomicLong(0L);
    private String message = "PING %d";
    
    public void init(ActorContext context) {
        this.context = context;
        
        pong = context.lookupActor("pong");
        context.sendTo(pong, "INIT");
    }

    public void onMessage(Object payload, MessageContext messageContext) {
        System.err.printf("Ping got '%s'", (String) payload);
        context.sendTo(pong, String.format(message, count.incrementAndGet()));
    }
}
