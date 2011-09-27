package example.counter;

import monterey.actor.Actor;
import monterey.actor.ActorRef;
import monterey.actor.ActorSpec;
import monterey.actor.factory.ActorFactoryRegistry;
import monterey.actor.factory.pojo.PojoFactory;
import monterey.actor.impl.ActorContextImpl;
import monterey.actor.impl.BasicActorRef;
import monterey.test.ActorProdder;
import monterey.venue.Venue;
import monterey.venue.jms.activemq.ActiveMqAdmin;
import monterey.venue.spi.ActorState;
import org.apache.activemq.broker.BrokerService;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.jms.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.testng.Assert.assertEquals;

/** Test the Suspend / Resume example code. */
public class  SuspendResumeTest implements MessageListener {
    private ActorRef counterActor;
    private ActorRef countPrinter;
    private ActorProdder actorProdder;
    private ActiveMqAdmin admin;
    private Venue venue;
    private BrokerService activeMqBroker;
    private long count;
    private final long TIMEOUT = 1000;
    private final List<Object> messages = new ArrayList<Object>(10);
    private Session consumerSession;
    private Connection connection;
    private ActorProdder rx;

    // TODO Use don's new test code instead of the setup methods here, which are mostly copied.

    private void setupBroker() throws Exception {
        // activeMqBroker = new BrokerService();
        // activeMqBroker.addConnector("tcp://localhost:61616");
        // activeMqBroker.start();
        // activeMqBroker.waitUntilStarted();
        // activeMqBroker.deleteAllMessages();
        admin = new ActiveMqAdmin();
        admin.setBrokerUrl("tcp://localhost:61616");

        connection = admin.createConnection();
  //      consumerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
  //      consumerSession.createDurableSubscriber("count");
  //     consumerSession.setMessageListener(this);
    }

    @Override
    public void onMessage(Message message) {
        try {
            messages.add(((ObjectMessage) message).getObject());
        } catch (Exception ex) {
            messages.add(ex);
        }
    }

    private void setupVenue() throws Exception {
        ActorFactoryRegistry actors = new ActorFactoryRegistry();
        actors.addFactory(PojoFactory.POJO, new PojoFactory());

        setupBroker();

        venue = new Venue("test-venue", "test-venue", actors);
        venue.setJmsAdmin(admin);
        venue.start();
    }

    private void shutdownBroker() throws Exception {
        if (activeMqBroker != null) {
            activeMqBroker.stop();
        }
    }

    @AfterTest(alwaysRun = true)
    private void shutdownVenue() throws Exception {
        venue.shutdown();
        shutdownBroker();
    }

    @BeforeTest
    private void setup() throws Exception {
        setupVenue();

        counterActor =  venue.newActor(new ActorSpec(SuspendResumeActor.class.getName(), "counter-actor"));
        countPrinter =  venue.newActor(new ActorSpec(CountPrinter.class.getName(), "counter-printer"));

        actorProdder = new ActorProdder(admin, "prodder");

        rx = new ActorProdder(admin, "Receive count prodder");
        rx.subscribe("count");
    }

    @Test(enabled = false)
    public void testGetFirstCountIs1() throws Exception {
        actorProdder.sendTo(counterActor, "");

        // Tester actor has the side effect of setting a variable to the count it last received.
        // (Initially it sets it to zero)
        assertEquals(rx.getMessage(), 0);

        actorProdder.sendTo(counterActor,"");
        assertEquals(rx.getMessage(), 1);
    }

    @Test(enabled = false)
    public void testGetSecondCountIs2() throws Exception {
        actorProdder.sendTo(counterActor, "");

    }

    @Test(enabled = false)
    public void testHasCountAfterResume() throws Exception {
        Thread.sleep(10000);
        ActorState counterState = venue.suspendActor(counterActor.getId());
        venue.resumeActor(counterActor.getId(), counterState);
    }
}
