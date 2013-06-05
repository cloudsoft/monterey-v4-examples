package monterey.example.migratable;

import monterey.actor.*;
import monterey.example.migratable.MigratableActor;
import monterey.venue.testharness.VenueTestHarness;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.testng.Assert.*;

public class MigratableActorTest {

    VenueTestHarness harness;

    @BeforeMethod
    public void setupHarness() {
        harness = VenueTestHarness.Factory.newInstance("activemq");
    }

    @AfterMethod(alwaysRun=true)
    public void tearDownHarness() {
        if (harness != null) harness.shutdown();
    }

    @Test
    public void testActorIsInstantiated() throws Exception {
        ActorRef actorRef = harness.newActor(new ActorSpec(MigratableActor.class.getName(), "Migration test actor"));
        assertTrue(harness.getActorInstance(actorRef) instanceof MigratableActor);
    }

    @Test
    public void testPostMigrationIsDifferentActorInstance() throws Exception {
        ActorRef actorRef = harness.newActor(new ActorSpec(MigratableActor.class.getName(), "Migration test actor"));
        Actor preInstance = harness.getActorInstance(actorRef);
        harness.migrateActor(actorRef);
        Actor postInstance = harness.getActorInstance(actorRef);
        assertNotSame(preInstance, postInstance);
    }

    @Test
    public void testMigrate() throws Exception {

        // Create a subscription to track messages published by the actor
        // on the count topic.
        final List<Object> receivedMsgs = new CopyOnWriteArrayList<Object>();
        harness.subscribe("count", new MessageListener() {
            @Override
            public void onMessage(Object o, MessageContext messageContext) {
                receivedMsgs.add(o);
            }
        });
        ActorRef actorRef = harness.newActor(new ActorSpec(MigratableActor.class.getName(), "Suspend/Resume test actor"));
        MigratableActor actor = (MigratableActor) harness.getActorInstance(actorRef);
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
