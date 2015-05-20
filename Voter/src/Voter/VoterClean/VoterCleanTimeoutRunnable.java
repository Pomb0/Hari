package Voter.VoterClean;

/**
 * Created by Jaime on 19/05/2015.
 *
 *
 */
public class VoterCleanTimeoutRunnable implements Runnable{
	private VoterClean voterClean;

	public VoterCleanTimeoutRunnable(VoterClean voterClean) {
		this.voterClean = voterClean;
	}

	@Override
	public void run() {
		while(voterClean.isRunning() && System.currentTimeMillis() < voterClean.getEndTime()) {
			try {
				Thread.sleep(voterClean.getEndTime()-System.currentTimeMillis());
			} catch (InterruptedException ignore){}
		}
		if(voterClean.isRunning()){
			System.out.println("Timeout: " + (System.currentTimeMillis() - voterClean.getStartTime()));
			voterClean.setIsRunning(false);
			voterClean.stopEverything();
		}
	}

}
