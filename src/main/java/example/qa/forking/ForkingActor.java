package example.qa.forking;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import monterey.actor.Actor;
import monterey.actor.ActorContext;
import monterey.actor.ActorRef;
import monterey.actor.ActorSpec;
import monterey.actor.MessageContext;
import monterey.actor.trait.Suspendable;
import monterey.actor.trait.Terminable;
import monterey.logging.Logger;
import monterey.logging.LoggerFactory;

import com.google.common.collect.ImmutableMap;

/**
 * An actor that splits a job up into parts, to be handled by spawned WorkingActors. The actors
 * participating in the job will form a tree, with ForkingActor instances at the nodes and 
 * WorkingActor instances at the leaves. 
 * 
 * The actors to handle a job are created for the duration of a single job.
 * 
 * A job consists of a range of numbers to be processed. These numbers are divided up amongst
 * the WorkingActors, who then send their result back to their parent ForkingActor. The 
 * ForkingActor correlates these responses, and when it is complete it returns its merged result
 * to its parent.
 * 
 * TODO Currently the "work" consists of summing all of the numbers in the range. We could generalise
 * the actors so they could be configurable about what to do with the work and how to merge results.
 */
public class ForkingActor implements Actor, Suspendable, Terminable {
    private static final Logger LOG = new LoggerFactory().getLogger(ForkingActor.class);
    
    private final Map<String, ActorRef> jobRequestors = new HashMap<String,ActorRef>();
    private final Map<String, ForkingResultCorrelator> jobCorrelators = new HashMap<String,ForkingResultCorrelator>();
    private ActorContext context;
    private boolean oneshot;
    
    public void init(ActorContext context) {
        this.context = context;
        oneshot = Boolean.parseBoolean(context.getConfigurationParams().get("oneshot"));
    }

    public void onMessage(Object payload, MessageContext messageContext) {
    	if (ForkingJob.isInstance(payload)) {
    		ForkingJob job = ForkingJob.fromExternalString((String)payload);
    		jobRequestors.put(job.jobId, messageContext.getSource());
    		jobCorrelators.put(job.jobId, new ForkingResultCorrelator(job));
			spawnJob(job);
    		
    	} else if (ForkingResult.isResult(payload)) {
    		ForkingResult result = ForkingResult.fromExternalString((String)payload);
			correlate(result);
    	}
    }

	private void correlate(ForkingResult result) {
		ForkingResultCorrelator correlator = jobCorrelators.get(result.jobId);
		correlator.addResult(result);
		
		if (correlator.isDone()) {
			long answer = correlator.getMergedResult();
			ActorRef requestor = jobRequestors.remove(result.jobId);
			jobCorrelators.remove(result.jobId);
			context.sendTo(requestor, answer);
		}
		if (oneshot) {
			context.terminate();
		}
	}

	private void spawnJob(ForkingJob job) {
		List<ForkingJob> children = job.split();
		
    	if (job.depth >= 1) {
    		
            for (int i = 0; i < children.size(); i++) {
            	ForkingJob childJob = children.get(i);
            	String childName = context.getSelf().getId()+"-forker"+i;
            	LOG.info("Spawning "+childName+" for "+childJob);
            	
            	Map<String, String> childConfig = new ImmutableMap.Builder<String,String>()
            			.put("oneshot", Boolean.TRUE.toString())
            			.build();
    			ActorSpec actorSpec = new ActorSpec("example.qa.forking.ForkingActor", childName, childConfig);
    			ActorRef child = context.newActor(actorSpec);
    			
    			context.sendTo(child, childJob.toExtrenalString());
            }
            
    	} else {
    		
            for (int i = 0; i < children.size(); i++) {
            	ForkingJob childJob = children.get(i);
            	String childName = context.getSelf().getId()+"-worker"+i;
            	LOG.info("Spawning "+childName+" for "+childJob);
            	
            	Map<String, String> childConfig = new ImmutableMap.Builder<String,String>()
            			.put("oneshot", Boolean.TRUE.toString())
            			.build();
    			ActorSpec actorSpec = new ActorSpec("example.qa.forking.WorkingActor", childName, childConfig);
    			ActorRef child = context.newActor(actorSpec);
    			
    			context.sendTo(child, childJob.toExtrenalString());
            }
    	}
	}

	@Override
    public void start(Object state) {
    }

    public Serializable suspend() {
    	return null;
    }

    public void resume(Object state) {
    }

    public void terminate(boolean force) {
    }
}
