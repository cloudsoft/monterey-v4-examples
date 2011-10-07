package monterey.example.helloworld;

import monterey.actor.Actor;
import monterey.actor.ActorContext;
import monterey.actor.MessageContext;

public class HelloWorldActor implements Actor {
    private ActorContext context;
    
    public void init(ActorContext context) {
        this.context = context;
    }

    public void onMessage(Object payload, MessageContext messageContext) {
        System.out.println("Received "+payload+" from "+messageContext.getSource());
        context.sendTo(messageContext.getSource(), "hello "+payload);
    }
}
