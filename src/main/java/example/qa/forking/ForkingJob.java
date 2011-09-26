package example.qa.forking;

import java.util.ArrayList;
import java.util.List;

public class ForkingJob {

	final String jobId;
	final int depth;
	final int numWorkers;
	final long lowerBound;
	final long upperBound;

	public static boolean isInstance(Object blob) {
		if (!(blob instanceof String)) {
			return false;
		}
		String[] parts = ((String)blob).split("\t");
		return (parts.length > 0) && "JOB".equals(parts[0]);
	}

	public static ForkingJob fromExternalString(String blob) {
		String[] parts = blob.split("\t");
		String jobId = parts[1];
		int depth = Integer.parseInt(parts[2]);
		int numWorkers = Integer.parseInt(parts[3]);
		long lowerBound = Long.parseLong(parts[4]);
		long upperBound = Long.parseLong(parts[5]);
		
		return new ForkingJob(jobId, depth, numWorkers, lowerBound, upperBound);
	}
	
	public ForkingJob(String jobId, int depth, int numWorkers, long lowerBound, long upperBound) {
		this.jobId = jobId;
		this.depth = depth;
		this.numWorkers = numWorkers;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public String toExtrenalString() {
		return "JOB"+"\t"+jobId+"\t"+depth+"\t"+numWorkers+"\t"+lowerBound+"\t"+upperBound;
	}
	
	public List<ForkingJob> split() {
		int numChildren = (depth <= 1) ? numWorkers : (numWorkers/depth + (numWorkers%depth > 0 ? 1 : 0));
		
		long maxRangePerChild = (upperBound-lowerBound+1) / numChildren;
		int maxWorkersPerChild = (depth <= 1) ? 0 : numWorkers/depth;
		
		long nextLowerBound = lowerBound;
		int workerCount = 0;
		
		List<ForkingJob> result = new ArrayList<ForkingJob>();
		while (nextLowerBound <= upperBound) {
			long childLowerBound = nextLowerBound;
			long childUpperBound = Math.min(nextLowerBound+maxRangePerChild-1, upperBound);
			int childNumWorkers = Math.min(maxWorkersPerChild, (numWorkers-workerCount));
			result.add(new ForkingJob(jobId, depth-1, childNumWorkers, childLowerBound, childUpperBound));
			nextLowerBound = (childUpperBound+1);
			workerCount += childNumWorkers;
		}

		return result;
	}
}
