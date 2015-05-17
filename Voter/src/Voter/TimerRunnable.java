package Voter;

/**
 * Created by Jaime on 17/05/2015.
 */
public class TimerRunnable implements Runnable {
	long sleepTime;
	long end;
	Voter voter;

	public TimerRunnable(long sleepTime, Voter voter) {
		this.sleepTime = sleepTime;
		this.voter = voter;
	}

	@Override
	public void run() {
		this.end = System.currentTimeMillis()+sleepTime;
		while(System.currentTimeMillis() < end) {
			try {
				Thread.sleep(end-System.currentTimeMillis());
			} catch (InterruptedException e){
				System.out.println("INTERRUPTED THE TIMER.");
			}
		}
		stopEverything();
	}
	private void stopEverything(){
		voter.setTimedOut(true);
		Thread[] voters = voter.getVoterThreads();
		Thread main = voter.getMainThread();

		for(Thread i : voters) if(i!=null && i.isAlive()) i.interrupt();
		if(main!=null && main.isAlive()) main.interrupt();
	}
}
