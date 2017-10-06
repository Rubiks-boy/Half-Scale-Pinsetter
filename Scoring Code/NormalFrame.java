import java.util.ArrayList;

/**
 * Tenth frame of a bowling game, including support for 3 rolls.
 * @author Adam Walker P5
 */
public class NormalFrame implements BowlingFrame
{		
	/**Shots the person threw*/
	protected ArrayList<Integer> scores;

	/**
	 * Constructor - sets up scores and default information
	 */
	public NormalFrame() 
	{
		scores = new ArrayList<Integer>();
		//add -1 flags for all of the scores
		for(int i = 0; i < NUM_SCORES; i++)
			scores.add(-1);
	}//end NormalFrame()

	/**
	 * Sets the first ball of the frame
	 * 
	 * pre - score must be in range (>0, <=10)
	 * @param score - first ball score
	 * @throws Exception if score is illogical
	 */
	public void setFirstBall(int score) throws Exception
	{
		if(score >= 0 && score <= MAX_SCORE) 
		{
			scores.set(0,  score);
			return;
		}//end if

		throw new Exception("Error: Ball1 score invalid range.");
	}//end setFirstBall)_

	/**
	 * Sets the second ball of the frame
	 * 
	 * pre - score must be in range (>0, score + first ball score <=10)
	 * @param score - second ball score
	 * @throws Exception if score is illogical
	 */
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
			throw new Exception("Error: Ball2 on top of Strike");
		}//end outer else

		throw new Exception("Ball2 score invalid range.");
	}//end setSecondBall()

	/**
	 * Getter for first ball score
	 */
	public int getFirstBall()
	{
		if(scores.size() >= 1)
			return scores.get(0);
		else return -1;
	}//end getFirstBall();

	/**
	 * Getter for second ball score
	 */
	public int getSecondBall()
	{
		if(scores.size() >= 2)
			return scores.get(1);
		else return -1;
	}//end getSecondBall()

	/**
	 * Calculates the total of the game thus far
	 * 
	 * @param prevFrameTot - Total of Ninth frame
	 * @return int total score
	 */
	public int calcTot(int prevFrameTot) 
	{
		int tot = prevFrameTot;
		for(Integer curr : scores)
			if(curr != -1)
				tot += curr;


		return tot;
	}//end calcTot()

	/**
	 * Calculates the total of the game thus far
	 * 
	 * @param prevFrameTot - Total of Ninth frame
	 * @param nextBall - first ball of next frame score
	 * @return int total score
	 */
	public int calcTot(int prevFrameTot, int nextBall) 
	{
		int tot = prevFrameTot;
		for(Integer curr : scores)
			if(curr != -1)
				tot += curr;

		if(this.isSpare())
			tot += nextBall;

		return tot;
	}//end calcTot()

	/**
	 * Calculates the total of the game thus far
	 * 
	 * Pre - nextBall and nextNextBall <=10
	 * @param prevFrameTot - Total of Ninth frame
	 * @param nextBall - first ball of next frame score
	 * @param nextNextBall - two throws after the frame
	 * @return int total score
	 */
	public int calcTot(int prevFrameTot, int nextBall, int nextNextBall) 
	{
		int tot = prevFrameTot;
		for(Integer curr : scores)
			if(curr != -1)
				tot += curr;

		if(this.isStrike())
			tot += nextBall + nextNextBall;
		else if(this.isSpare())
			tot += nextBall;

		return tot;
	}//end calcTot()

	/**
	 * Whether the frame is a spare
	 * @return boolean - true if spare
	 */
	public boolean isSpare() {return (scores.get(0) + scores.get(1) == MAX_SCORE);}

	/**
	 * Whether the frame is a strike
	 * @return boolean - true if strike
	 */
	public boolean isStrike() {return (scores.get(0) == MAX_SCORE);}

	/**
	 * Gets the scores with X for strikes, / for spares, and - for gutters.
	 * 
	 * @return ArrayList<Character> with the scores, each element being one throw.
	 */
	public ArrayList<Character> getScores() 
	{
		ArrayList<Character> charScores = new ArrayList<Character>();

		for(int i = 0; i < NUM_SCORES; i++) 
		{
			//end if score hasn't been set yet
			if(scores.get(i) == -1) 
				break;
			else if(scores.get(i).intValue() == 0) 
			{
				//represent gutter with '-'
				charScores.add('-');
			}//end if
			else if(i != 0 && scores.get(i).intValue() + scores.get(i-1).intValue() == MAX_SCORE) 
			{
				//spare
				charScores.add('/');
			}//end if
			else if(scores.get(i).intValue() == MAX_SCORE) 
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
}//end class NormalFrame
