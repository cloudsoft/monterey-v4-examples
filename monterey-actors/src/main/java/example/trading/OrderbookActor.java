package example.trading;

import java.io.Serializable;

import monterey.actor.Actor;
import monterey.actor.ActorContext;
import monterey.actor.ActorRef;
import monterey.actor.MessageContext;
import monterey.actor.trait.Suspendable;
import monterey.actor.trait.Terminable;

public class OrderbookActor implements Actor, Suspendable, Terminable {
    private ActorContext context;
    private String stockName;
    
    public void init(ActorContext context) {
        this.context = context;
        this.stockName = context.getConfigurationParams().get("stockName");
    }

    public void onMessage(Object payload, MessageContext source) {
        // Send to our target actor; alteratively could have replied back via "source"
        if (payload instanceof Bid) {
            // pretend we've matched. notify counterparties
            ActorRef trader1 = context.lookupActor("trader1id");
            ActorRef trader2 = context.lookupActor("trader2id");
            
            context.sendTo(trader1, "some info about trade");
            context.sendTo(trader2, "some info about trade");
        } else if (payload instanceof Ask) {
            // pretend we didn't match
        } else {
            //...
        }
        
        context.publish(stockName, "state of book blah");
    }

    @Override
    public void start(Object state) {
    }

    public Serializable suspend() {
        return "state of my book";
    }

    public void resume(Object state) {
        // restore the state of my book
    }

    public void terminate(boolean force) {
        // whatever's required to terminate ourselves
    }
    
    private static class Bid {}
    private static class Ask {}
}
