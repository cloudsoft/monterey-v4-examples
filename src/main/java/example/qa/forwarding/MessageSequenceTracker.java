package example.qa.forwarding;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import monterey.actor.ActorRef;

public class MessageSequenceTracker implements Serializable {
	
	private static final long serialVersionUID = 6655933949597893754L;
	
	private final Map<ActorRef,Integer> lastReceivedDirectly = new HashMap<ActorRef,Integer>();
    private final Map<ActorRef,Map<String,Integer>> lastReceivedViaPublish = new HashMap<ActorRef,Map<String,Integer>>();
	private final ActorRef self;
    
    MessageSequenceTracker(ActorRef self) {
    	this.self = self;
    }
    
    public void onReceivedDirectly(ActorRef source, Integer payload) {
    	Integer prev = lastReceivedDirectly.get(source);
    	if (prev != null && prev > payload) {
    		throw new IllegalStateException("ERROR: "+self+" received from "+source+" payload "+payload+" but previously received "+prev);
    	}
    	lastReceivedDirectly.put(source,  payload);
	}
    
    public void onReceivedViaPublish(ActorRef source, String topic, Integer payload) {
    	Map<String, Integer> prevByTopic = lastReceivedViaPublish.get(source);
    	if (prevByTopic == null) {
    		prevByTopic = new HashMap<String, Integer>();
    		lastReceivedViaPublish.put(source,  prevByTopic);
    	}
    	Integer prev = prevByTopic.get(topic);
    	if (prev != null && prev > payload) {
    		throw new IllegalStateException("ERROR: "+self+" received from "+source+" on topic "+topic+" payload "+payload+" but previously received "+prev);
    	}
    	prevByTopic.put(topic,  payload);
	}
}
