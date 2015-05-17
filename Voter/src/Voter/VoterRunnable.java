package Voter;

import Voter.Results.VersionResult;
import server.InsulinDoseCalculator;
import server.ServiceIdentifier;

import java.util.concurrent.BlockingQueue;

public abstract class VoterRunnable implements Runnable {
	private int id = 0;
	VersionResult[] versionResults;
	ServiceIdentifier[] serviceIdentifierArray;
	InsulinDoseCalculator[] calculators;
	BlockingQueue queue;
	Voter voter;


	public VoterRunnable(int id, VersionResult[] versionResults, ServiceIdentifier[] serviceIdentifierArray, InsulinDoseCalculator[] calculators, Voter voter) {
		this.id = id;
		this.serviceIdentifierArray = serviceIdentifierArray;
		this.calculators = calculators;
		this.voter = voter;
		this.queue = this.voter.getQueue();
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		VersionResult vres = new VersionResult()
				.setServiceURI(serviceIdentifierArray[id].getWsdlLocation().toString())
				.setRunTime(0l);
		int result;
		try{
			result = getResult();
		} catch (Exception e){
			System.out.println("INTERRUPTED THE THREAD WHILE GETTING THE RESULT.");
			result = -1;
		}
		if(result==-1) vres.setSuccessful(false);
		else vres.setSuccessful(true);
		vres
				.setRunTime(System.currentTimeMillis()-start)
				.setResult(result);
		try{
			queue.offer(vres);
		}catch (Exception e){
			System.out.println("INTERRUPTED THE THREAD.");
		}

	}

	public abstract int getResult() throws Exception;
}
