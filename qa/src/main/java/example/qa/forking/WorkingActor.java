package example.qa.forking;

import java.io.Serializable;

import monterey.actor.Actor;
import monterey.actor.ActorContext;
import monterey.actor.MessageContext;
import monterey.actor.trait.Suspendable;
import monterey.actor.trait.Terminable;
import monterey.logging.Logger;
import monterey.logging.LoggerFactory;

public class WorkingActor implements Actor, Suspendable, Terminable {
    private static final Logger LOG = new LoggerFactory().getLogger(WorkingActor.class);
    
    private ActorContext context;
    private boolean oneshot;
    
    public void init(ActorContext context) {
        this.context = context;
        oneshot = Boolean.parseBoolean(context.getConfigurationParams().get("oneshot"));
    }

    public void onMessage(Object payload, MessageContext messageContext) {
    	if (!ForkingJob.isInstance(payload)) {
    		throw new IllegalArgumentException("Unexpected message: "+payload);
    	}
    	
		ForkingJob job = ForkingJob.fromExternalString((String)payload);
		
		long result = 0;
		for (long i = job.lowerBound; i < job.upperBound; i++) {
			result += i;
		}
		
		context.sendTo(messageContext.getSource(), new ForkingResult(job.jobId, job.lowerBound, job.upperBound, result));
		
		if (oneshot) {
			context.terminate();
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
