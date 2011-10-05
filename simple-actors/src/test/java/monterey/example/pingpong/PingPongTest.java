package monterey.example.pingpong;

import monterey.actor.ActorRef;
import monterey.actor.ActorSpec;
import monterey.venue.testharness.VenueTestHarness;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

public class PingPongTest {

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
    public void testActorsAreInstantiated() throws Exception {
        Map<String, String> config = new HashMap<String, String>();
        config.put(PingActor.MESSAGE_KEY, "ping!");
        ActorRef ping = harness.newActor(
                new ActorSpec("monterey.example.pingpong.PingActor", "Ping test actor", config));
        ActorRef pong = harness.newActor(
                new ActorSpec("monterey.example.pingpong.PongActor", "Pong test actor", config));
        assertTrue(harness.getActorInstance(ping) instanceof PingActor);
        assertTrue(harness.getActorInstance(pong) instanceof PongActor);
    }

}
