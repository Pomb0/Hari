package Voter.VoterClean;

import Voter.Results.VersionResult;
import server.InsulinDoseCalculator;
import server.ServiceIdentifier;
import server.ServiceManager;

/**
 * Created by Jaime on 18/05/2015.
 *
 */
public abstract class VoterCleanRunnable implements Runnable, Cloneable{
	private ServiceIdentifier serviceIdentifier;
	private InsulinDoseCalculator service;
	private VoterClean voter;

	public VoterCleanRunnable(VoterClean voter) {
		this.voter = voter;
	}

	@Override
	public void run() {
		Long startTime = System.currentTimeMillis();
		VersionResult result = new VersionResult().setServiceURI(serviceIdentifier.getWsdlLocation().toString());
		int res = -1;
		if(service!=null && voter.isRunning()) {
			res = callService(service);
			if(res!=-1)result.setSuccessful(true);
			else result.setSuccessful(false);
		}else{
			result.setSuccessful(false);
		}
		result.setResult(res);
		result.setRunTime(System.currentTimeMillis() - startTime);
		try{
			if(voter.isRunning())voter.getResultsQueue().offer(result);
		}catch (Exception e){
			System.out.println("Could not add a result to the queue.");
		}
	}

	public abstract int callService(InsulinDoseCalculator service);

	public VoterCleanRunnable setServiceIdentifier(ServiceIdentifier serviceIdentifier) {
		this.serviceIdentifier = serviceIdentifier;
		if(voter.isRunning()) this.service = ServiceManager.ServiceClientFactory(serviceIdentifier);
		return this;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
