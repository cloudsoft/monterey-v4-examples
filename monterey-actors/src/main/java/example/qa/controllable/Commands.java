package example.qa.controllable;

import monterey.actor.ActorRef;
import monterey.actor.ActorSpec;

public class Commands {

    public static class SendCommand {
        public ActorRef destination;
        public Object payload;
        
        SendCommand(ActorRef destination, Object payload) {
            this.destination = destination;
            this.payload = payload;
        }
    }
    
    public static class PublishCommand {
        public String topic;
        public Object payload;
        
        PublishCommand(String topic, Object payload) {
            this.topic = topic;
            this.payload = payload;
        }
    }

    public static class SubscribeCommand {
        public String topic;
        
        SubscribeCommand(String topic) {
            this.topic = topic;
        }
    }
    
    public static class UnsubscribeCommand {
        public String topic;
        
        UnsubscribeCommand(String topic) {
            this.topic = topic;
        }
    }
    
    public static class NewActorCommand {
        public ActorSpec actorSpec;
        
        NewActorCommand(ActorSpec actorSpec) {
            this.actorSpec = actorSpec;
        }
    }
}
