package monterey.example.pi;

import monterey.venue.testharness.VenueTestHarness;
import monterey.actor.ActorSpec;
import monterey.actor.ActorRef;

import org.testng.Assert;

public class PiCalculatorActorTest {
    public void testMyActorIsInstantiated() throws Exception {

        VenueTestHarness harness = VenueTestHarness.Factory.newInstance();
        ActorRef actorRef = harness.newActor(new ActorSpec("simple.PiCalculator", "myDisplayName"));
        Assert.assertTrue(harness.getActorInstance(actorRef) instanceof PiCalculator);

        // TODO: put your test logic here
        // e.g. harness.sendTo(actorRef, "my message");

        harness.shutdown();
    }
}
