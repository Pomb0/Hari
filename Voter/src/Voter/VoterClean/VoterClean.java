package Voter.VoterClean;

import Voter.Results.VersionResult;
import Voter.Results.VoterResult;
import Voter.VoterInterface;
import server.InsulinDoseCalculator;
import server.ServiceIdentifier;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jaime on 18/05/2015.
 * Clean, simpler implementation
 */
public class VoterClean implements VoterInterface {
	private Integer versionCount;
	private Long startTime, endTime;
	private ServiceIdentifier[] serviceIdentifiers;
	private LinkedBlockingQueue<VersionResult> resultsQueue = new LinkedBlockingQueue<>();
	private VoterCleanRunnable runnable;
	private Thread[] threads = new Thread[0];
	private Thread timerThread, mainThread;
	private Map<Integer, Integer> histogram = new TreeMap<>();
	private boolean isRunning = true;

	public VoterClean(ServiceIdentifier[] serviceIdentifiers, Integer versionCount, Long timeout) {
		this.startTime = System.currentTimeMillis();
		this.endTime = this.startTime+timeout;
		this.mainThread = Thread.currentThread();
		timerThread = new Thread(new VoterCleanTimeoutRunnable(this));
		timerThread.start();
		this.serviceIdentifiers = serviceIdentifiers;
		this.versionCount = versionCount;
	}

	public synchronized void stopEverything(){
		this.setIsRunning(false);
		try {
			for (Thread i : threads) if (i != null && i.isAlive()) i.interrupt();
			if (timerThread.isAlive()) timerThread.interrupt();
		}catch (SecurityException e){System.out.println("Security Exception Interrupting a thread.");}
	}

	private void addVote(int vote){
		Integer occurrences = histogram.get(vote);
		if(occurrences==null) occurrences = 0;
		histogram.put(vote, occurrences+1);
	}

	private int getElected(){
		Map.Entry<Integer, Integer> best = new AbstractMap.SimpleEntry<>(-1, -1);
		for(Map.Entry<Integer, Integer> i : histogram.entrySet()) if(i.getValue() > best.getValue()) best = i;
		if(best.getValue()<((versionCount/2)+1)) return -1;
		return best.getKey();
	}

	private VoterResult getVotedResult(){
		int offset = 0;
		setIsRunning(true);
		threads = new Thread[serviceIdentifiers.length];
		VoterResult voterResult;
		do{
			voterResult = tryVotingResult(offset);
		}while (  (!voterResult.isSuccessful()) && isRunning && (offset=versionCount+offset) <= (serviceIdentifiers.length - versionCount));
		stopEverything();
		return voterResult.setRunTime(System.currentTimeMillis() - startTime);
	}

	private VoterResult tryVotingResult(int offset){
		histogram.clear();
		resultsQueue.clear();
		VoterResult voterResult = new VoterResult().setVersionCount(versionCount).setSuccessful(false);
		List<VersionResult> results = new LinkedList<>();
		VersionResult tmpResult;
		//Start Threads
		for(int i=offset; i<versionCount+offset && isRunning(); i++){
			try {
				threads[i] = new Thread(((VoterCleanRunnable)runnable.clone()).setServiceIdentifier(serviceIdentifiers[i]));
				threads[i].start();
			} catch (CloneNotSupportedException e) {e.printStackTrace();}
		}

		int resultsCount = 0;
		try{
			while(isRunning() && resultsCount<versionCount){
				tmpResult = resultsQueue.poll(endTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
				resultsCount++;
				if(tmpResult!=null){
					results.add(tmpResult);
					if(tmpResult.isSuccessful()) addVote(tmpResult.getResult());
				}
			}
		}catch (Exception e){ System.out.println("Exception( " + e.getClass().getName() + " ) while reading from queue: " + e.getMessage());}
		voterResult.setResult(getElected());
		if(resultsCount >= ((versionCount/2)+1) && voterResult.getResult()!=-1) voterResult.setSuccessful(true);
		voterResult.setVersionResults(results);
		return voterResult;
	}


	@Override
	public VoterResult mealtimeInsulinDose(final int carbohydrateAmount, final int carbohydrateToInsulinRatio, final int preMealBloodSugar, final int targetBloodSugar, final int personalSensitivity) {
		runnable = new VoterCleanRunnable(this) {
			@Override
			public int callService(InsulinDoseCalculator service) {
				return service.mealtimeInsulinDose(carbohydrateAmount, carbohydrateToInsulinRatio, preMealBloodSugar, targetBloodSugar, personalSensitivity);
			}
		};
		return getVotedResult().setMethod("mealtimeInsulinDose");
	}

	@Override
	public VoterResult backgroundInsulinDose(final int bodyWeight) {
		runnable = new VoterCleanRunnable(this) {
			@Override
			public int callService(InsulinDoseCalculator service) {
				return service.backgroundInsulinDose(bodyWeight);
			}
		};
		return getVotedResult().setMethod("backgroundInsulinDose");
	}

	@Override
	public VoterResult personalSensitivityToInsulin(final int physicalActivityLevel, final int[] physicalActivitySamples, final int[] bloodSugarDropSamples) {
		runnable = new VoterCleanRunnable(this) {
			@Override
			public int callService(InsulinDoseCalculator service) {
				return service.personalSensitivityToInsulin(physicalActivityLevel,physicalActivitySamples, bloodSugarDropSamples);
			}
		};
		return getVotedResult().setMethod("personalSensitivityToInsulin");
	}

	/**
	 * Motherfucking getters and setters!
	 * They look sooooo ugly :'(
	 * */
 	public LinkedBlockingQueue<VersionResult> getResultsQueue() {
		return resultsQueue;
	}

	public Long getStartTime() {
		return startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized VoterClean setIsRunning(boolean isRunning) {
		this.isRunning = isRunning;
		return this;
	}

}
