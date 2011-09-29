package example.qa.controllable;

import monterey.actor.Actor;
import monterey.actor.ActorContext;
import monterey.actor.MessageContext;
import example.qa.controllable.Commands.NewActorCommand;
import example.qa.controllable.Commands.PublishCommand;
import example.qa.controllable.Commands.SendCommand;
import example.qa.controllable.Commands.SubscribeCommand;
import example.qa.controllable.Commands.UnsubscribeCommand;

public class ControllableActor implements Actor {
    private ActorContext context;
    
    public void init(ActorContext context) {
        this.context = context;
    }

    public void onMessage(Object payload, MessageContext messageContext) {
        if (payload instanceof SendCommand) {
            context.sendTo(((SendCommand)payload).destination, ((SendCommand)payload).payload);
        } else if (payload instanceof PublishCommand) {
            context.publish(((PublishCommand)payload).topic, ((PublishCommand)payload).payload);
        } else if (payload instanceof SubscribeCommand) {
            context.subscribe(((SubscribeCommand)payload).topic);
        } else if (payload instanceof UnsubscribeCommand) {
            context.unsubscribe(((UnsubscribeCommand)payload).topic);
        } else if (payload instanceof NewActorCommand) {
            context.newActor(((NewActorCommand)payload).actorSpec);
        }
    }
}
