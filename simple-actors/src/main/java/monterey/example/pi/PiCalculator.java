package monterey.example.pi;

import monterey.actor.Actor;
import monterey.actor.ActorContext;
import monterey.actor.ActorSpec;
import monterey.actor.MessageContext;

public class PiCalculator implements Actor {

    private static final String TYPE = PiCalculator.class.getCanonicalName();

    ActorContext context;

    public void init(ActorContext context) {
        this.context = context;
    }

    public void onMessage(Object payload, MessageContext context) {
        PiMessage m = (PiMessage) payload;
        Double chunk = calculatePiFor(m.start, m.noElements);
        this.context.sendTo(context.getSource(), chunk);
    }

    private double calculatePiFor(int start, int noElements) {
        double acc = 0.0;
        for (int i = start; i <  (start + noElements); i++) {
            acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1);
        }
        return acc;
    }

    public static ActorSpec getActorSpec(int id) {
        return new ActorSpec(TYPE, "calculator-" + id);
    }

}
