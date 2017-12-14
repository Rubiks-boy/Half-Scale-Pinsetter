/**
 * Tenth frame of a bowling game, including support for 3 rolls.
 * @author Adam Walker
 */
public class TenthFrame extends NormalFrame 
{
	/**Number of potential throws*/
	public final static int TENTH_NUM_THROWS = 3;

	/**
	 * Constructor - sets up scores and default information
	 */
	public TenthFrame() 
	{
		scores = new int[TENTH_NUM_THROWS];
		
		for(int i = 0; i < TENTH_NUM_THROWS; i++)
			scores[i] = -1;
	}//end TenthFrame() constructor

	/**
	 * Sets the second ball of the frame
	 * 
	 * pre - score must be in range (>0, score + first ball score <=10 if shooting spare)
	 * @param pinsStanding - number of pins standing
	 * @throws Exception if score is illogical
	 * @return true if frame is done 
	 */
	@Override
	public boolean setSecondBall(int pinsStanding) throws Exception 
	{
		//make sure ball1 has already happened
		if(scores[0] == -1) 
			throw new Exception("ERROR: SET_SCORING_TENTH: Ball2 set before Ball1 set.");

		//ball1 isn't a strike; shooting at a spare
		if(scores[0] != NUM_PINS) 
		{
			if(pinsStanding >= 0 && pinsStanding <= NUM_PINS - scores[0]) 
			{
				scores[1] = NUM_PINS - scores[0] - pinsStanding;
				return (pinsStanding + scores[0] != NUM_PINS);
			}//end inner if
		}//end outer if
		else 
		{
			//ball1 is a strike
			if(pinsStanding >= 0 && pinsStanding <= NUM_PINS)
			{
				scores[1] = NUM_PINS - pinsStanding;
				return false;
			}//end inner if
		}//end outer else

		throw new Exception("ERROR: SET_SCORING_TENTH: Ball2 score invalid range.");
	}//end setSecondBall()

	/**
	 * Sets the third ball of the frame
	 * 
	 * pre - score must be in range (>0, <=10)
	 * @param pinsStanding - number of pins standing
	 * @throws Exception if score is illogical 
	 * 	or third ball can't exist
	 * @return true if frame is done 
	 */
	public boolean setThirdBall(int pinsStanding) throws Exception 
	{
		//make sure it's logical to set the third ball
		if(scores[0] == -1 || scores[1] == -1)
			throw new Exception("ERROR: SET_SCORING_TENTH: Ball3 set before Ball1 or Ball2 set.");

		//bowler must have strikes or a spare to throw third ball
		if(scores[0] + scores[1] < NUM_PINS)
			throw new Exception("ERROR: SET_SCORING_TENTH: Tried to set 3rd ball on open 10th frame.");

		//ball2 was a strike (or spare) or ball1 and ball2 form a spare
		if(scores[1] == NUM_PINS || scores[0] + scores[1] == NUM_PINS) 
		{
			if(pinsStanding >= 0 && pinsStanding <= NUM_PINS) 
			{
				scores[2] = NUM_PINS - pinsStanding;
				return true;
			}//end inner if
		}//end outer if
		else 
		{ //ball1 was a strike, ball2 and ball3 form a frame
			if(pinsStanding >= 0 && pinsStanding <= NUM_PINS - scores[1]) 
			{
				scores[2] = NUM_PINS - scores[1] - pinsStanding;
				return true;
			}//end inner if
			else if(pinsStanding >= 0 && pinsStanding > NUM_PINS - scores[1])
			{
				System.out.println("WARNING: SET_SCORING_TENTH: More pins standing after Ball3 than Ball2");
				scores[1] = 0;
				return true;
			}
		}//end else

		throw new Exception("ERROR: SET_SCORING_TENTH: Ball3 score invalid range.");
	}//end setThirdBall()
	
	/**
	 * Getter for third ball score
	 */
	public int getThirdBall()
	{
		return scores[2];
	}//end getSecondBall()
}//end class TenthFrame
