package example.counter;

import monterey.actor.Actor;
import monterey.actor.ActorContext;
import monterey.actor.MessageContext;
import monterey.actor.trait.Suspendable;

import java.io.Serializable;

/** A very simple example of implementing a suspendable Actor.
 *
 * This shows how an actor with state can preserve it over a suspend resume cycle.
 *
 * This class defines a counter. On receiving a message, it will increment the counter and return the result. This could be
 * used, for example, as a database primary key generator.
 */
public class SuspendResumeActor implements Actor, Suspendable {
    private ActorContext context;
    private long count;

    /* The core of the suspendable Actor is the same as a stateless Actor */

    /* Init is run both when the Actor is started for the first time and when it is resumed. */
    public void init(ActorContext context) {
        this.context = context;
    }

    // TODO have more than one method, e.g. get higest and incrementAndGet.
    public void onMessage(Object payload, MessageContext messageContext) {
        // It is not neccessary to worry about concurrency because message handling is single threaded.
        count++;
        context.sendTo(messageContext.getSource(), count);

       // Also publish the count to a topic, for the CountPrinter Actor.
        context.publish("count", count);
    }

    /* Now we need to implement the methods in the Suspendable interface. */

    /**
     * Called by the framework when the actor is first created, after being initialized. Will not be called on resume.
     */
    public void start(Object state) {
        // Not really necessary in this case but given to show the sort of thing that can be done here.
        count = 0;
    }

    public Serializable suspend() {
        return count;
    }
    
    /**
     * Will be invoked by framework after initialization, when resuming a suspended actor.
     *
     * state is what was returned by suspend().
     */
    public void resume(Object state) {
        count = (Long) state;
    }
}
