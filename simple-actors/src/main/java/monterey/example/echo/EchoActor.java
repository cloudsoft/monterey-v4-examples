package monterey.example.echo;

import monterey.actor.Actor;
import monterey.actor.ActorContext;
import monterey.actor.MessageContext;

/**
 * A simple actor that sends back to the originator the same message payload as it receives (hence "echo"). 
 */
public class EchoActor implements Actor {
    private ActorContext context;
    
    public void init(ActorContext context) {
        this.context = context;
    }

    public void onMessage(Object payload, MessageContext messageContext) {
        System.out.println("Echoing "+payload+" from "+messageContext.getSource());
        context.sendTo(messageContext.getSource(), payload);
    }
}
