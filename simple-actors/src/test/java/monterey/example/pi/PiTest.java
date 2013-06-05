package monterey.example.pi;

import monterey.actor.ActorRef;
import monterey.actor.ActorSpec;
import monterey.venue.testharness.VenueTestHarness;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class PiTest {

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
        ActorRef actorRef = harness.newActor(new ActorSpec(PiMaster.class.getName(), "Pi test actor"));
        assertTrue(harness.getActorInstance(actorRef) instanceof PiMaster);
    }

    @Test
    public void testMasterConvergesToPi() throws Exception {
        ActorRef actorRef = harness.newActor(new ActorSpec(PiMaster.class.getName(), "Pi test actor"));
        PiMaster master = (PiMaster) harness.getActorInstance(actorRef);
        
        // TODO Should demonstrate better way of waiting for work to complete!
        Thread.sleep(2*1000);
        
        assertTrue(Math.abs(Math.PI - master.pi) < 0.00001, "actual="+master.pi+"; diff="+(Math.PI - master.pi));
    }
}
