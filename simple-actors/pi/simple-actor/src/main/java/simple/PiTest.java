package simple;

import monterey.actor.ActorRef;
import monterey.actor.ActorSpec;
import monterey.venue.testharness.VenueTestHarness;
import org.testng.Assert;

/**
 * Created by IntelliJ IDEA.
 * User: sam
 * Date: 04/10/2011
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */
public class PiTest {

    public void testMyActorIsInstantiated() throws Exception {

        VenueTestHarness harness = VenueTestHarness.Factory.newInstance();
        ActorRef actorRef = harness.newActor(new ActorSpec("simple.PiMaster", "lalala"));

        Assert.assertTrue(harness.getActorInstance(actorRef) instanceof PiMaster);
        PiMaster master = (PiMaster) harness.getActorInstance(actorRef);

        System.out.println("The wait is over..");
        System.out.println(master.pi);
        
        harness.shutdown();
    }

    public static void main(String[] args) {
        try {
            (new PiTest()).testMyActorIsInstantiated();
        } catch (Exception e) {
            // ..
        }
    }
}
