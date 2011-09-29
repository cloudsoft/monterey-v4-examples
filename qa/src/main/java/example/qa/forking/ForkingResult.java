package example.qa.forking;

public class ForkingResult {

	final String jobId;
	final long lowerBound;
	final long upperBound;
	final long result;
	
	public static boolean isResult(Object blob) {
		if (!(blob instanceof String)) {
			return false;
		}
		String[] parts = ((String)blob).split("\t");
		return (parts.length > 0) && "RESULT".equals(parts[0]);
	}

	public static ForkingResult fromExternalString(String blob) {
		String[] parts = blob.split("\t");
		String jobId = parts[1];
		long lowerBound = Long.parseLong(parts[2]);
		long upperBound = Long.parseLong(parts[3]);
		long result = Long.parseLong(parts[4]);
		
		return new ForkingResult(jobId, lowerBound, upperBound, result);
	}
	
	public ForkingResult(String jobId, long lowerBound, long upperBound, long result) {
		this.jobId = jobId;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.result = result;
	}

	public String toExtrenalString() {
		return "RESULT"+"\t"+jobId+"\t"+lowerBound+"\t"+upperBound+"\t"+result;
	}
}
