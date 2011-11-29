package monterey.example.helloworld;

import monterey.actor.ActorRef;
import monterey.actor.ActorSpec;
import monterey.venue.testharness.VenueTestHarness;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class HelloWorldTest {

    VenueTestHarness harness;

    @BeforeMethod
    public void setupHarness() {
        harness = VenueTestHarness.Factory.newInstance("activemq");
    }

    @AfterMethod(alwaysRun=true)
    public void tearDownHarness() {
        harness.shutdown();
    }

    @Test
    public void testActorIsInstantiated() throws Exception {
        ActorRef actorRef = harness.newActor(
                new ActorSpec(HelloWorldActor.class.getName(), "Hello, world! test actor"));
        assertTrue(harness.getActorInstance(actorRef) instanceof HelloWorldActor);
    }

}
