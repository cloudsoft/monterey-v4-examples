package monterey.example.suspendresume;

import monterey.actor.ActorRef;
import monterey.actor.ActorSpec;
import monterey.venue.testharness.VenueTestHarness;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class SuspendResumeTest {

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
    public void testMyActorIsInstantiated() throws Exception {
        ActorRef actorRef = harness.newActor(
                new ActorSpec("monterey.example.suspendresume.SuspendResumeActor", "Suspend/Resume test actor"));
        assertTrue(harness.getActorInstance(actorRef) instanceof SuspendResumeActor);
    }
}
