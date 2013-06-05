package monterey.example.metrics;

import monterey.actor.ActorRef;
import monterey.actor.ActorSpec;
import monterey.venue.management.metrics.ActorMetrics;
import monterey.venue.testharness.VenueTestHarness;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

public class MetricContributingActorTest {

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
    public void testActorContributesCustomMetrics() throws Exception {
        ActorRef actorRef = harness.newActor(new ActorSpec(MetricContributingActor.class.getName(), "test actor"));
        ActorMetrics metrics = harness.getActorMetrics(actorRef);
        
        Assert.assertEquals(metrics.getCustomProperties(), ImmutableMap.of(
            MetricContributingActor.PROPERTY_NAME, "some property value"
        ));
        Assert.assertEquals(metrics.getCustomMetrics(), ImmutableMap.of(
            MetricContributingActor.FIRST_METRIC_ID, "123",
            MetricContributingActor.SECOND_METRIC_ID, "456"
        ));
    }
    
    @Test
    public void testActorCustomMetricsAvailableAfterMigration() throws Exception {
        ActorRef actorRef = harness.newActor(new ActorSpec(MetricContributingActor.class.getName(), "test actor"));
        harness.migrateActor(actorRef);
        ActorMetrics metrics = harness.getActorMetrics(actorRef);
        
        Assert.assertEquals(metrics.getCustomProperties(), ImmutableMap.of(
            MetricContributingActor.PROPERTY_NAME, "some property value"
        ));
        Assert.assertEquals(metrics.getCustomMetrics(), ImmutableMap.of(
            MetricContributingActor.FIRST_METRIC_ID, "123",
            MetricContributingActor.SECOND_METRIC_ID, "456"
        ));
    }
}
