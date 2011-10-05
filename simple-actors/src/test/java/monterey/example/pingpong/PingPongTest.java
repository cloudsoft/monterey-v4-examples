package monterey.example.pingpong;

import monterey.actor.ActorRef;
import monterey.actor.ActorSpec;
import monterey.venue.testharness.VenueTestHarness;
import org.testng.Assert;

public class PingPongTest {

    public void testMyActorIsInstantiated() throws Exception {

        VenueTestHarness harness = VenueTestHarness.Factory.newInstance();
        ActorRef actorRef = harness.newActor(new ActorSpec("simple.PiMaster", "lalala"));

        Assert.assertTrue(harness.getActorInstance(actorRef) instanceof PingPongTest);
        PingPongTest master = (PingPongTest) harness.getActorInstance(actorRef);


        harness.shutdown();
    }

    public static void main(String[] args) {
        try {
            (new PingPongTest()).testMyActorIsInstantiated();
        } catch (Exception e) {
            // ..
        }
    }
}
