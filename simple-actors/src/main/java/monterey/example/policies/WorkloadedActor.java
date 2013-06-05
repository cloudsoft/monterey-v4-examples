package monterey.example.policies;

import java.util.concurrent.CancellationException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import monterey.actor.Actor;
import monterey.actor.ActorContext;
import monterey.actor.MessageContext;
import monterey.actor.annotation.PostResume;
import monterey.actor.annotation.PreSuspend;
import monterey.actor.annotation.PreTerminate;
import monterey.logging.Logger;
import monterey.logging.LoggerFactory;

import com.google.common.base.Preconditions;

public class WorkloadedActor implements Actor {
    
    private static final Logger LOG = new LoggerFactory().getLogger(WorkloadedActor.class);
    
    public static final String MSGS_PER_SEC_KEY = "msgsPerSec";

    // TODO Use venue capabilities/services, when that feature is available
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

    private static final long TIMEOUT_MS = 10*1000;

    private ActorContext context;
    private long count = 0L;
    private long msgsPerSec;
    private transient ScheduledFuture<?> future;
    
    public void init(final ActorContext context) {
        Preconditions.checkArgument(context.getConfigurationParams().containsKey(MSGS_PER_SEC_KEY), MSGS_PER_SEC_KEY+" configuration not set");
        this.context = context;
        this.msgsPerSec = (Long) context.getConfigurationParams().get(MSGS_PER_SEC_KEY);
        
        future = scheduleSends();
    }

    @PreSuspend
    public Object suspend() throws Exception {
        if (future != null) cancelSends(future, false);
        return count;
    }

    @PostResume
    public void resume(Object state) throws Exception {
        count = (Long) state;
    }
    
    @PreTerminate
    public void terminate() throws Exception {
        if (future != null) cancelSends(future, false);
    }

    private void cancelSends(ScheduledFuture<?> future, boolean force) throws Exception {
        future.cancel(force);
        try {
            future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (CancellationException e) {
            // expect this. Want to wait for the task to really have stopped before returning.
        }
    }

    private ScheduledFuture<?> scheduleSends() {
        // note: only works accurately for max 1000 per sec, and for factors of 1000
        long period = Math.max(1, 1000/msgsPerSec);
        return executor.scheduleAtFixedRate(new Runnable() {
            @Override public void run() {
                try {
                    context.sendTo(context.getSelf(), (count++));
                } catch (Exception e) {
                    LOG.warn(e, "Error during period send for %s", context.getSelf());
                }
            }
            
        }, 0, period, TimeUnit.MILLISECONDS);
    }

    public void onMessage(Object payload, MessageContext messageContext) {
        // no-op
        if (LOG.isDebugEnabled()) LOG.debug("Actor %s received message from %s: %s", context.getSelf(), messageContext.getSource(), payload);
    }
}
