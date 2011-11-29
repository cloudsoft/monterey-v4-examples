package monterey.example.migratable;

import java.io.Serializable;

import monterey.actor.Actor;
import monterey.actor.ActorContext;
import monterey.actor.MessageContext;
import monterey.actor.annotation.PostResume;
import monterey.actor.annotation.PostStart;
import monterey.actor.annotation.PreSuspend;

/** 
 * A very simple example of implementing a suspendable Actor.
 *
 * This shows how an actor can preserve its state when being migrated from one venue
 * to another (i.e. when being suspended and resumed). Any subscriptions done in
 * start() will be automatically re-subscribed.
 * 
 * On resume, it will be a different instance of the actor so its fields must be 
 * set on resume.
 * 
 * In this class, the state is a simple counter of the number of messages received.
 * 
 * Here, we use annotations; alternatively we could have implemented 
 * {@link monterey.actor.trait.Suspendable}.
 */
public class MigratableActor implements Actor {
    private ActorContext context;
    private long count;

    @Override
    public void init(ActorContext context) {
        this.context = context;
    }

    @Override
    public void onMessage(Object payload, MessageContext messageContext) {
        count++;
        
        // Publish the latest count (primarily for the purpose of testing this actor).
        context.publish("count", count);
    }

    @PostStart
    public void start(Object state) {
        count = 0;
        context.subscribe("topic1");
    }

    @PreSuspend
    public Serializable suspend() {
        return count;
    }
    
    @PostResume
    public void resume(Object state) {
        count = (Long) state;
    }
}
