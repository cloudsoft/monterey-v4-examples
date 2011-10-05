package monterey.example.echo;

import monterey.actor.ActorRef;
import monterey.actor.ActorSpec;
import monterey.venue.testharness.VenueTestHarness;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class EchoTest {

    VenueTestHarness harness;

    @BeforeMethod
    private void setupHarness() {
        harness = VenueTestHarness.Factory.newInstance();
    }

    @AfterMethod(alwaysRun=true)
    private void tearDownHarness() {
        harness.shutdown();
    }

    @Test
    public void testActorIsInstantiated() throws Exception {
        ActorRef actorRef = harness.newActor(new ActorSpec("monterey.example.echo.EchoActor", "Echo test actor"));
        assertTrue(harness.getActorInstance(actorRef) instanceof EchoActor);
    }

    @Test
    public void testEchoedResponse() {
        ActorRef actorRef = harness.newActor(new ActorSpec("monterey.example.echo.EchoActor", "Echo Actor"));
        harness.sendTo(actorRef, "echo!");
        String response = "";
        assertEquals(response, "echo!");
    }

}
