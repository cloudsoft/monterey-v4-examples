package monterey.example.suspendresume;

import monterey.actor.*;
import monterey.venue.testharness.VenueTestHarness;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
    public void testActorIsInstantiated() throws Exception {
        ActorRef actorRef = harness.newActor(
                new ActorSpec("monterey.example.suspendresume.SuspendResumeActor", "Suspend/Resume test actor"));
        assertTrue(harness.getActorInstance(actorRef) instanceof SuspendResumeActor);
    }

    @Test
    public void testMigrate() throws Exception {
        // Create a subscription to track messages published by the SuspendResume
        // actor on the count topic.
        final List<Object> receivedMsgs = new CopyOnWriteArrayList<Object>();
        harness.subscribe("count", new MessageListener() {
            @Override
            public void onMessage(Object o, MessageContext messageContext) {
                receivedMsgs.add(o);
            }
        });
        ActorRef actorRef = harness.newActor(
                new ActorSpec(SuspendResumeActor.class.getName(), "Suspend/Resume test actor"));
        SuspendResumeActor actor = (SuspendResumeActor) harness.getActorInstance(actorRef);
        actor.start(null);

        // Check correct count is published
        harness.sendTo(actorRef, null);
        assertEventuallyEquals(receivedMsgs, Arrays.asList(1L), 10 * 1000);

        // Migrate the actor, then send it a second message
        harness.migrateActor(actorRef);
        harness.sendTo(actorRef, null);

        assertEventuallyEquals(receivedMsgs, Arrays.asList(1L, 2L), 10 * 1000);
    }

    private void assertEventuallyEquals(List<?> actual, List<?> expected, long timeout) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < startTime + timeout) {
            if (expected.equals(actual)) {
                return; // success
            }
            Thread.sleep(timeout / 100L);
        }
        assertEquals(actual, expected, "After waiting "+timeout+"ms");
    }
}
