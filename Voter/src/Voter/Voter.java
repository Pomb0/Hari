package Voter;

import Voter.Results.VersionResult;
import Voter.Results.VoterResult;
import server.InsulinDoseCalculator;
import server.ServiceIdentifier;
import server.ServiceManager;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class Voter implements VoterInterface{
	private ServiceIdentifier[] serviceIdentifierArray;
	private InsulinDoseCalculator[] calculators;
	private VersionResult[] versionResults;
	private int versionCount = 0;
	private int offset = 0;
	private long startTime;
	private long timeout;
	private boolean timedOut = false;
	private LinkedBlockingQueue<VersionResult> queue;
	private Thread mainThread;
	private Thread timerThread;
	private Thread[] voterThreads;
	private Map<Integer, Integer> histogram = new LinkedHashMap<Integer, Integer>();


	public Voter(ServiceIdentifier[] serviceIdentifierArray, int versionCount, long timeout) {
		startTime = System.currentTimeMillis();
		this.serviceIdentifierArray = serviceIdentifierArray;
		this.versionCount = versionCount;
		this.timeout = timeout;
		this.mainThread = Thread.currentThread();
		calculators = new InsulinDoseCalculator[versionCount];
		versionResults = new VersionResult[versionCount];
		queue = new LinkedBlockingQueue<VersionResult>();
		voterThreads = new Thread[versionCount];
	}


	@Override
	public VoterResult mealtimeInsulinDose(final int carbohydrateAmount, final int carbohydrateToInsulinRatio, final int preMealBloodSugar, final int targetBloodSugar, final int personalSensitivity) {
		prepareVoting();
		for(int i=0;i<versionCount; i++){
			final int _id = i;
			voterThreads[i] = new Thread(
					new VoterRunnable(_id, versionResults, serviceIdentifierArray, calculators, this) {
						@Override
						public int getResult() throws Exception {
							return calculators[_id].mealtimeInsulinDose(carbohydrateAmount, carbohydrateToInsulinRatio, preMealBloodSugar, targetBloodSugar, personalSensitivity);
						}
					}
			);
			voterThreads[i].start();
		}
		return getVotedResult().setMethod("mealtimeInsulinDose()");
	}

	@Override
	public VoterResult backgroundInsulinDose(final int bodyWeight) {
		prepareVoting();
		for(int i=0;i<versionCount; i++){
			final int _id = i;
			voterThreads[i] = new Thread(
					new VoterRunnable(_id, versionResults, serviceIdentifierArray, calculators, this) {
						@Override
						public int getResult() throws Exception {
							return calculators[_id].backgroundInsulinDose(bodyWeight);
						}
					}
			);
			voterThreads[i].start();
		}
		return getVotedResult().setMethod("backgroundInsulinDose()");
	}

	@Override
	public VoterResult personalSensitivityToInsulin(final int physicalActivityLevel, final int[] physicalActivitySamples, final int[] bloodSugarDropSamples) {
		prepareVoting();
		for(int i=0;i<versionCount; i++){
			final int _id = i;
			voterThreads[i] = new Thread(
					new VoterRunnable(_id, versionResults, serviceIdentifierArray, calculators, this) {
						@Override
						public int getResult() throws Exception {
							return calculators[_id].personalSensitivityToInsulin(physicalActivityLevel, physicalActivitySamples, bloodSugarDropSamples);
						}
					}
			);
			voterThreads[i].start();
		}
		return getVotedResult().setMethod("personalSensitivityToInsulin");
	}


	private VoterResult getVotedResult(){
		int count = 0;
		int bestCount=-1, value=-1;
		int majority = versionCount/2+1;
		List<VersionResult> individual = new LinkedList<VersionResult>();
		VersionResult version;

		VoterResult voterResult = new VoterResult().setVersionCount(versionCount);

		while(!isTimedOut() && count < versionCount){
			try {
				version = queue.take();
				count++;
				individual.add(version);
				countOcorrence(version.getResult());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Set<Map.Entry<Integer, Integer>> results = histogram.entrySet();

		for(Map.Entry<Integer, Integer> i : results){
			if(i.getValue()>bestCount){
				bestCount = i.getValue();
				value = i.getKey();
			}
		}

		System.out.println("BestCount: " + bestCount + "\n Value: "  + value + "\n\n");

		voterResult
				.setResult(value)
				.setVersionResults(individual);
		if(bestCount>=majority) voterResult.setSuccessful(true);
		else voterResult.setSuccessful(false);

		return voterResult.setRunTime(System.currentTimeMillis() - startTime);
	}

	private void countOcorrence(Integer result){
		if(!histogram.containsKey(result)) histogram.put(result, 0);
		histogram.put(result, histogram.get(result)+1);
	}

	private void prepareVoting(){
		timerThread = new Thread(new TimerRunnable(timeout, this));
		timerThread.start();
		prepareServiceSet(offset);

	}

	private int prepareServiceSet(int offset){
		int nulls = 0;
		for(int i=0; i<versionCount; i++){
			calculators[i] = ServiceManager.ServiceClientFactory(serviceIdentifierArray[i+offset]);
			if(calculators[i]==null) nulls++;
		}
		return versionCount-nulls;
	}


	public synchronized boolean isTimedOut() {
		return this.timedOut;
	}

	public synchronized void setTimedOut(boolean timedOut) {
		this.timedOut = timedOut;
	}


	public BlockingQueue<VersionResult> getQueue() {
		return queue;
	}


	public Thread[] getVoterThreads() {
		return voterThreads;
	}

	public Thread getTimerThread() {
		return timerThread;
	}

	public Thread getMainThread() {
		return mainThread;
	}
}
