package example.qa.forking;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class ForkingResultCorrelator {

	final ForkingJob job;
	final List<ForkingResult> results = new LinkedList<ForkingResult>();

	public ForkingResultCorrelator(ForkingJob job) {
		this.job = job;
	}
	
	public void addResult(ForkingResult result) {
		results.add(result);
		Collections.sort(results, new Comparator<ForkingResult>() {
			@Override public int compare(ForkingResult o1, ForkingResult o2) {
				return new Long(o1.lowerBound).compareTo(new Long(o2.upperBound));
			}
		});
	}
	
	public boolean isDone() {
		// Do we have lower and upper extremes?
		if (results.isEmpty() || job.lowerBound != results.get(0).lowerBound || job.upperBound != results.get(results.size()-1).upperBound) {
			return false;
		}
		
		// Do we have any holes?
		long nextExpectedLower = job.lowerBound;
		for (ForkingResult next : results) {
			if (next.lowerBound != nextExpectedLower) {
				return false;
			}
			nextExpectedLower = next.upperBound+1;
		}
		
		return true;
	}

	public long getMergedResult() {
		long answer = 0;
		for (ForkingResult result : results) {
			answer += result.result;
		}
		return answer;
	}
}
