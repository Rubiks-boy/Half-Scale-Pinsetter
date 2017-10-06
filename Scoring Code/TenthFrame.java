import java.util.ArrayList;

/**
 * Tenth frame of a bowling game, including support for 3 rolls.
 * @author Adam Walker P5
 */
public class TenthFrame extends NormalFrame 
{
	/**Number of potential throws*/
	public final static int TENTH_NUM_SCORES = 3;

	/**
	 * Constructor - sets up scores and default information
	 */
	public TenthFrame() 
	{
		super();
		scores.add(-1);
	}//end TenthFrame() constructor

	/**
	 * Sets the second ball of the frame
	 * 
	 * pre - score must be in range (>0, score + first ball score <=10 if shooting spare)
	 * @param score - second ball score
	 * @throws Exception if score is illogical
	 */
	@Override
	public void setSecondBall(int score) throws Exception 
	{
		//make sure ball1 has already happened
		if(scores.get(0) == -1) 
			throw new Exception("Error: Ball2 set before Ball1 set.");

		//ball1 isn't a strike; shooting at a spare
		if(scores.get(0) != MAX_SCORE) 
		{
			if(score >= 0 && score + scores.get(0) <= MAX_SCORE) 
			{
				scores.set(1, score);
				return;
			}//end inner if
		}//end outer if
		else 
		{
			//ball1 is a strike
			if(score >= 0 && score <= MAX_SCORE)
			{
				scores.set(1, score);
				return;
			}//end inner if
		}//end outer else

		throw new Exception("Ball2 score invalid range.");
	}//end setSecondBall()

	/**
	 * Sets the third ball of the frame
	 * 
	 * pre - score must be in range (>0, <=10)
	 * @param score - third ball score
	 * @throws Exception if score is illogical 
	 * 	or third ball can't exist
	 */
	public void setThirdBall(int score) throws Exception 
	{
		//make sure it's logical to set the third ball
		if(scores.get(0) == -1 || scores.get(1) == -1)
			throw new Exception("Error: Ball3 set before Ball1 or Ball2 set.");

		//bowler must have strikes or a spare to throw third ball
		if(scores.get(0) + scores.get(1) < MAX_SCORE)
			throw new Exception("Error: Tried to set 3rd ball on open 10th frame.");

		//ball2 was a strike or ball1 and ball2 form a spare
		if(scores.get(1) == MAX_SCORE || scores.get(0) + scores.get(1) == MAX_SCORE) 
		{
			if(score >= 0 && score <= MAX_SCORE) 
			{
				scores.set(2,  score);
				return;
			}//end inner if
		}//end outer if
		else 
		{ //ball1 was a strike, ball2 and ball3 form a frame
			if(score >= 0 && score + scores.get(1) <= MAX_SCORE) 
			{
				scores.set(2,  score);
				return;
			}//end inner if
		}//end else

		throw new Exception("Error: Ball3 score invalid range.");
	}//end setThirdBall()

	/**
	 * Gets the scores with X for strikes, / for spares, and - for gutters.
	 * 
	 * @return ArrayList<Character> with the scores, each element being one throw.
	 */
	public ArrayList<Character> getScores() 
	{
		ArrayList<Character> charScores = new ArrayList<Character>();

		for(int i = 0; i < TENTH_NUM_SCORES; i++) 
		{
			//end if score hasn't been set yet
			if(scores.get(i) == -1) 
				break;
			else if(scores.get(i) == 0) 
			{
				//represent gutter with '-'
				charScores.add('-');
			}//end if
			else if(i != 0 && scores.get(i) + scores.get(i-1) == MAX_SCORE) 
			{//spare
				//in order for 3rd to be a / first must be a X
				if(i == TENTH_NUM_SCORES - 1)
					if(scores.get(0) == MAX_SCORE)
						charScores.add('/');
					else
						charScores.add(new Character((char)('0' + scores.get(i))));
				else
					charScores.add('/');
			}//end if
			else if(scores.get(i) == MAX_SCORE) 
			{
				//represent strike with 'X'
				charScores.add('X');
			}//end if
			else 
			{
				charScores.add(new Character((char)('0' + scores.get(i))));
			}//end else
		}//end for

		return charScores;
	}//end getScores()
}//end class TenthFrame
