package monterey.example.echo;

import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import monterey.actor.ActorRef;
import monterey.actor.ActorSpec;
import monterey.actor.MessageContext;
import monterey.actor.MessageListener;
import monterey.venue.testharness.VenueTestHarness;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EchoTest {

    private static final long TIMEOUT = 10*1000;
            
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
        ActorRef actorRef = harness.newActor(new ActorSpec(EchoActor.class.getName(), "Echo test actor"));
        assertTrue(harness.getActorInstance(actorRef) instanceof EchoActor);
    }

    @Test
    public void testEchoedResponse() throws Exception {
        String msg = "echo!";
        final List<Object> expected = Collections.<Object>singletonList(msg);
        final List<Object> received = new CopyOnWriteArrayList<Object>();
        final CountDownLatch latch = new CountDownLatch(1);
        
        ActorRef actorRef = harness.newActor(new ActorSpec(EchoActor.class.getName(), "Echo Actor"));
        
        harness.addMessageListener(new MessageListener() {
                @Override public void onMessage(Object payload, MessageContext messageContext) {
                    received.add(payload);
                    latch.countDown();
                }
            });
        
        harness.sendTo(actorRef, msg);

        latch.await(TIMEOUT, TimeUnit.MILLISECONDS);
        Assert.assertEquals(received, expected);
    }

}
