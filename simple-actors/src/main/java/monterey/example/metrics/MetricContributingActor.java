package monterey.example.metrics;

import java.util.Map;

import monterey.actor.Actor;
import monterey.actor.ActorContext;
import monterey.actor.MessageContext;
import monterey.actor.MetricProvider;
import monterey.actor.annotation.PostResume;
import monterey.actor.annotation.PostStart;

import com.google.common.collect.ImmutableMap;

public class MetricContributingActor implements Actor {
    public static final String FIRST_METRIC_ID = "acme.actor.metric.first";
    public static final String SECOND_METRIC_ID = "acme.actor.metric.second";
    public static final String PROPERTY_NAME  = "acme.actor.property.name";

    private int someMetric = 123;
    private long anotherMetric = 456L;

    private MetricProvider metricProvider = new MetricProvider() {
        public Map<String,String> getMetrics() {
            return ImmutableMap.of(
                FIRST_METRIC_ID, Integer.toString(someMetric),
                SECOND_METRIC_ID, Long.toString(anotherMetric));
        }
    };
    private ActorContext context;
    
    @Override
    public void init(ActorContext context) {
        this.context = context;
    }

    @PostStart
    public void start() {
        context.getMetricSupport().setProperty(PROPERTY_NAME, "some property value");
        context.getMetricSupport().setMetricProvider(metricProvider);
    }

    @PostResume
    public void resume() {
        context.getMetricSupport().setMetricProvider(metricProvider);
    }
    
    @Override
    public void onMessage(Object payload, MessageContext messageContext) {
        // put logic here...
    }
}
