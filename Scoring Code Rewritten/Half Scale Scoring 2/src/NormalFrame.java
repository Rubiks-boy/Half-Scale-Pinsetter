/**
 * This interface is for any bowling frame's common information.
 * @author Adam Walker
 */
public class NormalFrame {
	/**Maximum score (in a strike) / number of pins*/
	public final static int NUM_PINS = 10;
	/**Number of throws per frame*/
	public final static int NUM_THROWS = 2;
	/**Shots the person threw*/
	protected int [] scores;
	
	/**
	 * Constructor - sets up scores and default information
	 */
	public NormalFrame() 
	{
		scores = new int[NUM_THROWS];
		//add -1 flags for all of the scores
		for(int i = 0; i < NUM_THROWS; i++)
			scores[i] = -1;
	}//end NormalFrame()
	
	/**
	 * Getter for first ball score
	 */
	public int getFirstBall()
	{
			return scores[0];
	}//end getFirstBall();

	/**
	 * Getter for second ball score
	 */
	public int getSecondBall()
	{
			return scores[1];
	}//end getSecondBall()
	
	/**
	 * Sets the first ball of the frame
	 * 
	 * pre - score must be in range (>0, <=10)
	 * @param pinsStanding - number of standing pins
	 * @throws Exception if score is illogical
	 * @return true if frame is done 
	 */
	public boolean setFirstBall(int pinsStanding) throws Exception
	{
		if(pinsStanding >= 0 && pinsStanding < NUM_PINS) 
		{
			scores[0] = NUM_PINS - pinsStanding;
			return (pinsStanding == 0);
		}//end if

		throw new Exception("ERROR: SET_SCORING: Ball1 score invalid range.");
	}//end setFirstBall()
	
	/**
	 * Sets the second ball of the frame
	 * 
	 * pre - score must be in range (>0, score + first ball score <=10)
	 * @param pinsStanding - number of standing pins
	 * @throws Exception if score is illogical
	 * @return true if frame is done 
	 */
	public boolean setSecondBall(int pinsStanding) throws Exception 
	{
		//make sure ball1 has already happened
		if(scores[0] == -1) 
			throw new Exception("ERROR: SET_SCORING: Ball2 set before Ball1 set.");

		//ball1 isn't a strike; shooting at a spare
		if(scores[0] != NUM_PINS) 
		{
			if(pinsStanding >= 0 && pinsStanding <= NUM_PINS - scores[0]) 
			{
				scores[1] = NUM_PINS - scores[0] - pinsStanding;
				return true;
			}//end inner if
			else if(pinsStanding >= 0 && pinsStanding > NUM_PINS - scores[0])
			{
				System.out.println("WARNING: SET_SCORING: More pins standing after Ball2 than Ball1");
				scores[1] = 0;
				return true;
			}
		}//end outer if
		else 
		{
			throw new Exception("ERROR: SET_SCORING: Ball2 on top of Strike");
		}//end outer else

		throw new Exception("ERROR: SET_SCORING: Ball2 score invalid range.");
	}//end setSecondBall()
}//end BowlingFrame