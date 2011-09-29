package example.trading;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import monterey.actor.Actor;
import monterey.actor.ActorContext;
import monterey.actor.ActorRef;
import monterey.actor.MessageContext;
import monterey.actor.trait.Suspendable;
import monterey.actor.trait.Terminable;

public class TraderActor implements Actor, Suspendable, Terminable {
    private ActorContext context;
    private Collection<String> stocks;
    
    public void init(ActorContext context) {
        this.context = context;
        this.stocks = Arrays.asList("a"); // todo find out stocks somehow
    }

    public void onMessage(Object payload, MessageContext source) {
        // Send to our target actor; alteratively could have replied back via "source"
        if (payload instanceof TradeInfo) {
            // do something about having made a trade
        } else if (payload instanceof OrderBook) {
            // do something with the latest book for a given stock
        } else {
            //...
        }
        
        // Pretend decided to buy something
        // TODO Want an actor-registry
        String ibmId = null; // TODO get from somewhere
        ActorRef ibm = context.lookupActor(ibmId);
        context.sendTo(ibm, new Sell());
    }

    @Override
    public void start(Object state) {
        for (String stock : stocks) {
            context.subscribe(stock);
        }
    }

    public Serializable suspend() {
        return "my position";
    }

    public void resume(Object state) {
        // begin trading again
    }

    public void terminate(boolean force) {
        // whatever's required to terminate ourselves
    }
    
    private static class TradeInfo {}
    private static class OrderBook {}
    private static class Sell {}
}
