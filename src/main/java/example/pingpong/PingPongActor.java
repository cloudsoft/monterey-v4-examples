package example.pingpong;

import java.io.Serializable;

import monterey.actor.Actor;
import monterey.actor.ActorContext;
import monterey.actor.ActorRef;
import monterey.actor.MessageContext;
import monterey.actor.trait.Suspendable;
import monterey.actor.trait.Terminable;

public class PingPongActor implements Actor, Suspendable, Terminable {
    private ActorContext context;
    private String message = "PING";
    
    public void init(ActorContext context) {
        this.context = context;
        
        ActorRef someoneElse = context.lookupActor(context.getConfigurationParams().get("someoneElseId"));
        context.sendTo(someoneElse, 1);
    }

    public void onMessage(Object payload, MessageContext messageContext) {
        context.sendTo(messageContext.getSource(), ((Integer) payload) + 1);
    }

    @Override
    public void start(Object state) {
    }

    public Serializable suspend() {
        return message;
    }

    public void resume(Object state) {
        message = (String) state;
    }

    public void terminate(boolean force) {
        // whatever's required to terminate ourselves
    }
}
