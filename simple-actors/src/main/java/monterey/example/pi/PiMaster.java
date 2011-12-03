package monterey.example.pi;

import monterey.actor.Actor;
import monterey.actor.ActorContext;
import monterey.actor.ActorRef;
import monterey.actor.MessageContext;
import monterey.logging.Logger;
import monterey.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PiMaster implements Actor {

    private static final Logger LOG = new LoggerFactory().getLogger(PiMaster.class);

    private static final int NO_CALCULATORS = 8;
    private static final int NO_CALCULATIONS = 100;
    private static final int NO_ELEMENTS_PER_MESSAGE = 10000;

    private int remainingResponses = NO_CALCULATIONS;

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
        LOG.info("Creating worker actors");
        for (int i=0; i < NO_CALCULATORS; i++) {
            ActorRef ref = context.newActor(PiCalculator.getActorSpec(i));
            calculators.add(ref);
        }
    }

    private void scheduleWork() {
        LOG.info("Scheduling work");
        for (int i=0; i < NO_CALCULATIONS; i++) {
            ActorRef recipient = calculators.get(i % NO_CALCULATORS);
            PiMessage m = new PiMessage(i * NO_ELEMENTS_PER_MESSAGE, NO_ELEMENTS_PER_MESSAGE);
            this.context.sendTo(recipient, m);
        }
    }

    public void onMessage(Object payload, MessageContext context) {
        pi += (Double) payload;
        if (--remainingResponses == 0) {
            System.out.println("Calculation finished\nPi: " + pi);
        }
    }

}
