package monterey.example.pi;

import monterey.actor.Actor;
import monterey.actor.ActorContext;
import monterey.actor.ActorRef;
import monterey.actor.MessageContext;

import java.util.ArrayList;
import java.util.List;

public class PiMaster implements Actor {

    private static final int NO_CALCULATORS = 8;
    private static final int NO_CALCULATIONS = 10000;
    private static final int NO_ELEMENTS_PER_MESSAGE = 10000;

    List<ActorRef> calculators = new ArrayList<ActorRef>();
    ActorContext context;
    Double pi;

    public void init(ActorContext context) {
        this.context = context;
        this.pi = 0d;
        createActors();
        scheduleWork();
    }

    private void createActors() {
        for (int i=0; i < NO_CALCULATORS; i++) {
            ActorRef ref = context.newActor(PiCalculator.getActorSpec(i));
            calculators.add(ref);
        }
    }

    private void scheduleWork() {
        for (int i=0; i < NO_CALCULATIONS; i++) {
            ActorRef recipient = calculators.get(i % NO_CALCULATORS);
            PiMessage m = new PiMessage(i * NO_ELEMENTS_PER_MESSAGE, NO_ELEMENTS_PER_MESSAGE);
            this.context.sendTo(recipient, m);
        }
    }

    public void onMessage(Object payload, MessageContext context) {
        pi += (Double) payload;
    }

}
