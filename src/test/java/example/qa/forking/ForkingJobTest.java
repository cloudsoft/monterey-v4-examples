package example.qa.forking;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ForkingJobTest {

	@Test
	public void testJobSerializesDeserializes() throws Exception {
		ForkingJob job = new ForkingJob("id1", 1, 2, 3, 4);
		ForkingJob job2 = ForkingJob.fromExternalString(job.toExtrenalString());
		assertJobsEqual(job, job2);
	}
	
	@Test
	public void testResultSerializesDeserializes() throws Exception {
		ForkingResult result = new ForkingResult("id1", 1, 2, 3);
		ForkingResult result2 = ForkingResult.fromExternalString(result.toExtrenalString());
		Assert.assertEquals(result2.jobId, result.jobId);
		Assert.assertEquals(result2.lowerBound, result.lowerBound);
		Assert.assertEquals(result2.upperBound, result.upperBound);
		Assert.assertEquals(result2.result, result.result);
	}
	
	@Test
	public void testJobSplitsIntoWorkers() throws Exception {
		ForkingJob job = new ForkingJob("id1", 1, 2, 1, 128);
		List<ForkingJob> subJobs = job.split();
		
		Assert.assertEquals(subJobs.size(), 2);
		assertJobsEqual(subJobs.get(0), new ForkingJob("id1", 0, 0, 1, 64));
		assertJobsEqual(subJobs.get(1), new ForkingJob("id1", 0, 0, 65, 128));
	}
	
	@Test
	public void testJobSplitsIntoSubForkers() throws Exception {
		ForkingJob job = new ForkingJob("id1", 2, 4, 1, 128);
		List<ForkingJob> subJobs = job.split();
		
		Assert.assertEquals(subJobs.size(), 2);
		assertJobsEqual(subJobs.get(0), new ForkingJob("id1", 1, 2, 1, 64));
		assertJobsEqual(subJobs.get(1), new ForkingJob("id1", 1, 2, 65, 128));
	}
	
	@Test
	public void testCorrelatorDetectsDone() throws Exception {
		ForkingJob job = new ForkingJob("id1", 0, 0, 1, 128);
		ForkingResultCorrelator correlator = new ForkingResultCorrelator(job);
		correlator.addResult(new ForkingResult("id1", 1, 64, 1000));
		correlator.addResult(new ForkingResult("id1", 65, 128, 1001));
		Assert.assertTrue(correlator.isDone());
	}

	@Test
	public void testCorrelatorDetectsDoneWhenOutOfOrder() throws Exception {
		ForkingJob job = new ForkingJob("id1", 0, 0, 1, 128);
		ForkingResultCorrelator correlator = new ForkingResultCorrelator(job);
		correlator.addResult(new ForkingResult("id1", 65, 128, 1000));
		correlator.addResult(new ForkingResult("id1", 1, 64, 1000));
		Assert.assertTrue(correlator.isDone());
	}

	@Test
	public void testCorrelatorSumsResults() throws Exception {
		ForkingJob job = new ForkingJob("id1", 0, 0, 1, 128);
		ForkingResultCorrelator correlator = new ForkingResultCorrelator(job);
		correlator.addResult(new ForkingResult("id1", 1, 64, 1));
		correlator.addResult(new ForkingResult("id1", 65, 128, 2));
		Assert.assertEquals(correlator.getMergedResult(), 3);
	}

	@Test
	public void testCorrelatorNotDoneWhenEmpty() throws Exception {
		ForkingJob job = new ForkingJob("id1", 0, 0, 1, 128);
		ForkingResultCorrelator correlator = new ForkingResultCorrelator(job);
		Assert.assertFalse(correlator.isDone());
	}
	
	@Test
	public void testCorrelatorNotDoneWhenLowerBoundMissing() throws Exception {
		ForkingJob job = new ForkingJob("id1", 0, 0, 1, 128);
		ForkingResultCorrelator correlator = new ForkingResultCorrelator(job);
		
		correlator.addResult(new ForkingResult("id1", 2, 128, 1000));
		Assert.assertFalse(correlator.isDone());
	}

	@Test
	public void testCorrelatorNotDoneWhenUpperBoundMissing() throws Exception {
		ForkingJob job = new ForkingJob("id1", 0, 0, 1, 128);
		ForkingResultCorrelator correlator = new ForkingResultCorrelator(job);
		
		correlator.addResult(new ForkingResult("id1", 1, 127, 1000));
		Assert.assertFalse(correlator.isDone());
	}

	@Test
	public void testCorrelatorNotDoneWhenMiddleMissing() throws Exception {
		ForkingJob job = new ForkingJob("id1", 0, 0, 1, 128);
		ForkingResultCorrelator correlator = new ForkingResultCorrelator(job);
		
		correlator.addResult(new ForkingResult("id1", 1, 64, 1000));
		correlator.addResult(new ForkingResult("id1", 66, 128, 1000));
		Assert.assertFalse(correlator.isDone());
	}

	private void assertJobsEqual(ForkingJob job1, ForkingJob job2) {
		Assert.assertEquals(job1.jobId, job2.jobId);
		Assert.assertEquals(job1.depth, job2.depth);
		Assert.assertEquals(job1.numWorkers, job2.numWorkers);
		Assert.assertEquals(job1.lowerBound, job2.lowerBound);
		Assert.assertEquals(job1.upperBound, job2.upperBound);
	}
}
