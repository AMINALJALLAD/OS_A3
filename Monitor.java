

/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */
	enum Situation {thinking, eating, hungry, talking}
	public static Situation state[];
	public static int piNumberOfPhilosophers;
	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers)
	{
	//	chopsticks = new boolean[4];
		this.piNumberOfPhilosophers =piNumberOfPhilosophers; 
		intializer();
		//chopsticks = piNumberOfPhilosophers;
		// TODO: set appropriate number of chopsticks based on the # of philosophers
	}
	
	public void intializer() {
		state = new Situation[piNumberOfPhilosophers];
		for(int i =0 ; i < piNumberOfPhilosophers; i++) {
			System.out.println(i);
			state[i] = Situation.thinking;
		}
	}

	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final int piTID)
	{
		state[(piTID+piNumberOfPhilosophers-1)% piNumberOfPhilosophers] =Situation.hungry; // My state is hungry e.g. TID = 1 ( the index (1+3) %4 =4  => 4 %4 = 0 ) as the first thread is 1
	//	print(piTID, 'a');
		test(piTID);
	//	System.out.println("Debug: from pickup " + piTID + "  " + state[(piTID+piNumberOfPhilosophers-1)% piNumberOfPhilosophers]);
		if(!state[(piTID+piNumberOfPhilosophers-1)% piNumberOfPhilosophers].equals(Situation.eating)) {
			try {
		//		System.out.println("Debug: I'm not able to eat now " + piTID + ", so I have to wait.");
				this.wait(); // I have to wait as one or both of my neighbours are eating 
			}catch(InterruptedException e) {
				e.printStackTrace();  // to trace the cause of the exception for debugging purposes 
			}
			
		}
	//	print(piTID, 'a');
	}
	
	public synchronized void test(final int piTID) { //
	//	System.out.println("Debug: " + piTID +  " Check in test function the after " + (piTID %piNumberOfPhilosophers) + "and the before is " + ((piTID+piNumberOfPhilosophers-2)% piNumberOfPhilosophers));
		if( (! state[piTID %piNumberOfPhilosophers].equals(Situation.eating) ) && (! state[(piTID+piNumberOfPhilosophers - 2)%piNumberOfPhilosophers].equals(Situation.eating) ) && (state[(piTID+piNumberOfPhilosophers-1)% piNumberOfPhilosophers].equals(Situation.hungry)) ){ //piTID %piNumberOfPhilosophers the next thread and (piTID+piNumberOfPhilosophers - 2)%4 the previous thread
			state[(piTID+piNumberOfPhilosophers-1)% piNumberOfPhilosophers ] =Situation.eating; // I can eat now
		}											
		
	}
	
//	public synchronized void print(final int piTID, char c) { // function for debugging 
//		System.out.println("This thread is " + piTID);
//		if(c == 'a') {
//			System.out.println("It is from pickup");
//		}else{
//			System.out.println("It is from putdown!!!!!!!!!!");
//		}
//		for(int i=0; i < piNumberOfPhilosophers; i++) {
//			System.out.println("The state is " + state[i]);
//		}
//	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID)
	{
		state[(piTID+piNumberOfPhilosophers-1)% piNumberOfPhilosophers ] =Situation.thinking; // finish eating
//		print(piTID, 'b');
	//	System.out.println("Debug: from put down" + piTID + "  " + state[(piTID+piNumberOfPhilosophers-1)% piNumberOfPhilosophers]); 
		this.notify();
		test((piTID ) %piNumberOfPhilosophers); // test the next thread 
	//	test((piTID+piNumberOfPhilosophers -2) %piNumberOfPhilosophers); // test the previous thread
	}

	/**
	 * Only one philopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized boolean requestTalk(final int piTID)
	{
		boolean able = true;   // initially I assume that I can talk
		state[(piTID+piNumberOfPhilosophers - 1)%piNumberOfPhilosophers] = Situation.talking; // My state is talking right now as
		//if(!state[(piTID+piNumberOfPhilosophers - 1)%piNumberOfPhilosophers].equals(Situation.eating) ) { // My state is not eating
			int start = ((piTID+piNumberOfPhilosophers)% piNumberOfPhilosophers); //start loop from the next thread
		//	System.out.println("Debug : from requst , will start from here " + start + " where this is a thread is " + piTID);
			for(int i= start ; i < start + piNumberOfPhilosophers ;i++) {  
				if(state[(piTID+piNumberOfPhilosophers - 1)%piNumberOfPhilosophers].equals(Situation.talking)) {// in order to skip my state
					continue;
				}
				if(state[i % piNumberOfPhilosophers].equals(Situation.talking)) {
					System.out.println(i % piNumberOfPhilosophers + " here will stop the for loop");
					able= false;  // someone else is talking, so I can't
					break;
				}
			}
		//}else {
		//	able = false;  // My state is eatin, so I can't talk
		//}
		if(!able) {
			try {
		//		System.out.println("Debug: " + piTID  +  " YOU can't talk now. Someone else is talking please wait.");
				wait(); // wait someone else is talking right now 
			}catch(InterruptedException e) {
				e.printStackTrace();  // to trace the cause of the exception for debugging purposes 
			}
		}
		return able;
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk(final int piTID)
	{
		state[(piTID+piNumberOfPhilosophers - 1)%piNumberOfPhilosophers] = Situation.thinking; // I finished talking, and my state goes back to thinking
	//	System.out.println("Debug: ." + piTID + " End talking.");
		notify(); // tell any wait to talk that you can talk right now 
	}
}

// EOF
