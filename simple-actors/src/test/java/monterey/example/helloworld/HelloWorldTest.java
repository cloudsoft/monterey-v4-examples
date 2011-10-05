package monterey.example.helloworld;

import monterey.actor.ActorRef;
import monterey.actor.ActorSpec;
import monterey.venue.testharness.VenueTestHarness;
import org.testng.Assert;

public class HelloWorldTest {

    public void testMyActorIsInstantiated() throws Exception {

        VenueTestHarness harness = VenueTestHarness.Factory.newInstance();
        ActorRef actorRef = harness.newActor(new ActorSpec("simple.PiMaster", "lalala"));

        Assert.assertTrue(harness.getActorInstance(actorRef) instanceof HelloWorldTest);
        HelloWorldTest master = (HelloWorldTest) harness.getActorInstance(actorRef);

        harness.shutdown();
    }

    public static void main(String[] args) {
        try {
            (new HelloWorldTest()).testMyActorIsInstantiated();
        } catch (Exception e) {
            // ..
        }
    }
}
