package example.qa.forwarding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import monterey.actor.Actor;
import monterey.actor.ActorContext;
import monterey.actor.ActorRef;
import monterey.actor.MessageContext;
import monterey.actor.trait.Suspendable;
import monterey.actor.trait.Terminable;

import com.google.common.collect.ImmutableMap;

/**
 * An actor that can be configured to send messages to other actors of given ids, and to particular topics.
 * The actor can also be configured to subscribe to particular topics. 
 * <p>
 * This is done using the configuration parameters:
 * <ul>
 *   <li>destinationActorIds: comma-separated list of actor ids to be forwarded to (i.e. forwarding all received messages) 
 *   <li>publicationTopics: comma-separated list of topics to be published to (i.e. forwarding all received messages)
 *   <li>subscriptionTopics: comma-separated list of topics to subscribe to 
 * </ul> 
 */
public class FanOutActor implements Actor, Suspendable, Terminable {
    private ActorContext context;
    private int counter = 0;
    private MessageSequenceTracker messageSequenceTracker;
    private AtomicInteger concurrentCallCount = new AtomicInteger(0);
	private Collection<ActorRef> targetActors = new ArrayList<ActorRef>();
	private Collection<String> targetTopics = new ArrayList<String>();
    
    public void init(ActorContext context) {
        this.context = context;
    }

    public void onMessage(Object payload, MessageContext messageContext) {
    	incrementConcurrentCallCount();
    	try {
    		if (payload instanceof Integer) {
    			ActorRef source = messageContext.getSource();
    			String topic = messageContext.getTopic();
    			
    			if (topic == null) {
    				messageSequenceTracker.onReceivedDirectly(source, (Integer) payload);
    			} else {
    				messageSequenceTracker.onReceivedViaPublish(source, topic, (Integer) payload);
    			}
    		}
    		
    		for (ActorRef target : targetActors) {
    			context.sendTo(target, counter++);
    		}
    		for (String topic : targetTopics) {
    			context.publish(topic, counter++);
    		}
    		
    	} finally {
    		decrementConcurrentCallCount();
    	}
    }

	@Override
    public void start(Object state) {
    	incrementConcurrentCallCount();
    	try {
            messageSequenceTracker = new MessageSequenceTracker(context.getSelf());
            
            String concatenatedOtherIds = context.getConfigurationParams().get("destinationActorIds");
            String[] otherUntrimmedIds = concatenatedOtherIds.split(",");
            for (String otherUntrimmedId : otherUntrimmedIds) {
            	targetActors.add(context.lookupActor(otherUntrimmedId.trim()));
            }
            
            String concatenatedOtherTopics = context.getConfigurationParams().get("publicationTopics");
            String[] otherUntrimmedTopics = concatenatedOtherTopics.split(",");
            for (String otherUntrimmedTopic : otherUntrimmedTopics) {
            	targetTopics.add(otherUntrimmedTopic.trim());
            }
            
            String concatenatedSubscriptionTopics = context.getConfigurationParams().get("subscriptionTopics");
            String[] subscriptionTopics = concatenatedSubscriptionTopics.split(",");
            for (String subscriptionTopic : subscriptionTopics) {
            	context.subscribe(subscriptionTopic.trim());
            }
    	} finally {
    		decrementConcurrentCallCount();
    	}
    }

    public Serializable suspend() {
    	incrementConcurrentCallCount();
    	try {
    		return new ImmutableMap.Builder<String,Object>()
    				.put("counter", counter)
    				.put("messageSequenceTracker", messageSequenceTracker)
    				.put("targetActors", targetActors)
    				.put("targetTopics", targetTopics)
    				.build();
    	} finally {
    		decrementConcurrentCallCount();
    	}
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void resume(Object state) {
    	incrementConcurrentCallCount();
    	try {
    		counter = (Integer) ((Map)state).get("counter");
    		messageSequenceTracker = (MessageSequenceTracker) ((Map)state).get("messageSequenceTracker");
    		targetActors = (Collection<ActorRef>) ((Map)state).get("targetActors");
    		targetTopics = (Collection<String>) ((Map)state).get("targetTopics");
    		
    	} finally {
    		decrementConcurrentCallCount();
    	}
    }

    public void terminate(boolean force) {
    	incrementConcurrentCallCount();
    	try {
    		
    	} finally {
    		decrementConcurrentCallCount();
    	}
    }
    
    private void incrementConcurrentCallCount() {
    	int count = concurrentCallCount.incrementAndGet();
    	if (count != 1) {
    		throw new IllegalStateException("ERROR: "+context.getSelf()+" has "+count+" concurrent calls after increment");
    	}
    }
    
    private void decrementConcurrentCallCount() {
    	int count = concurrentCallCount.decrementAndGet();
    	if (count != 0) {
    		throw new IllegalStateException("ERROR: "+context.getSelf()+" has "+count+" concurrent calls after decrement");
    	}
    }
}
