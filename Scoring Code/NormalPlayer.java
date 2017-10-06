import java.util.ArrayList;
import java.util.Iterator;

/**
 * This stores a complete player, including the 10 frame game.
 * @author Adam Walker P5
 */
public class NormalPlayer extends Player
{
	/**
	 * Constructor - sets up the game and default data
	 */
	public NormalPlayer(){super();}

	/**
	 * Constructor - sets up the game and default data
	 * 
	 * @param playerNum - index of which player this player is
	 */
	public NormalPlayer(int playerNum, ScoreBoard scoreBoard)
	{
		super(playerNum, scoreBoard);
	}//end Player() constructor

	/**
	 * Prints the entire game onto the screen
	 * 
	 * post - System.out changed
	 */
	public void printScores()
	{
		Iterator<NormalFrame> gameIter = game.iterator();

		while(gameIter.hasNext())
			System.out.print(gameIter.next().getScores() + " ");
		System.out.println();
	}//end printScores()

	/**
	 * Generates the totals of frames bowled so far
	 * 
	 * @return ArrayList<Integer> with each frame's total
	 * 	(this will vary in size based on number of frames bowled)
	 */
	public ArrayList<Integer> calcFrameTotalsNoStrikeCorrect()
	{
		ArrayList<Integer> tots = new ArrayList<Integer>();

		int total = 0;

		for(int fr = 0; fr < game.size(); fr++)
		{
			BowlingFrame curr = game.get(fr);

			if(curr instanceof TenthFrame)
			{
				if(curr.getScores().size() == TenthFrame.TENTH_NUM_SCORES ||
						(curr.getScores().size() == NormalFrame.NUM_SCORES && 
						!((TenthFrame) curr).isStrike() && !((TenthFrame) curr).isSpare()))
					total = curr.calcTot(total);
				else break;
			}//end if
			else if(((NormalFrame) curr).isStrike() || curr.getScores().size() == NormalFrame.NUM_SCORES)
			{//frame is complete
				if(((NormalFrame) curr).isStrike())
				{
					int nextBall, nextNextBall;
					if(fr < game.size() - 1)
					{
						nextBall = game.get(fr+1).getFirstBall();
						if(nextBall == -1)
							break;
						nextNextBall = game.get(fr+1).getSecondBall();
						if(nextBall == BowlingFrame.MAX_SCORE && fr < game.size() - 2)
							nextNextBall = game.get(fr+2).getFirstBall();
						if(nextNextBall == -1) 
							break;
					}
					else break;
					total = ((NormalFrame) curr).calcTot(total, nextBall, nextNextBall);
				}
				else if(((NormalFrame) curr).isSpare())
				{
					int nextBall;
					if(fr < game.size() - 1)
					{
						nextBall = game.get(fr+1).getFirstBall();
						if(nextBall == -1)
							break;
					}
					else break;
					total = ((NormalFrame) curr).calcTot(total, nextBall);
				}
				else
				{
					total = curr.calcTot(total);
				}
			}//end else
			else break;

			tots.add(total);
		}//end for

		return tots;
	}//end calcFrameTotals()

	/**
	 * Calculates frame totals but removes the total for strings of strikes
	 * 
	 * @return ArrayList<Integer> with each frame's total
	 * 	(this will vary in size based on number of frames bowled)
	 */
	public ArrayList<Integer> calcFrameTotals()
	{
		ArrayList<Integer> tots = calcFrameTotalsNoStrikeCorrect();

		//remove if there is a string of strikes without end
		System.out.println("Game size: " + game.size());
		System.out.println("Totals size: " + tots.size());

		//string of strikes just ended
		if(tots.size() > 0 && game.get(tots.size() - 1).isStrike())
		{
			if(game.size() == tots.size() + 2 && !game.get(tots.size() + 1).isStrike())
				return tots;
			if(game.size() == tots.size() + 1 && !game.get(tots.size()).isStrike())
				return tots;

			for(int i = tots.size() - 1; i >= 0; i--)
			{
				if(i == NUM_FRAMES - 1)
					break;
				//tenth frame
				else if(i == NUM_FRAMES - 2 && game.size() == NUM_FRAMES)
				{
					TenthFrame frame = (TenthFrame)game.get(NUM_FRAMES - 1);

					if(frame.isStrike() && frame.getSecondBall() != -1)
					{
						if(frame.getSecondBall() == NormalFrame.MAX_SCORE)
							tots.remove(i);
						else break;
					}
				}//end if 
				else if(game.get(i).isStrike())
					tots.remove(i);
				else break;
			}//end for
		}
		return tots;
	}//end calcFrameTotals()

	/**
	 * Generates the total of the game
	 * 
	 * @return int of the score total
	 */
	@Override
	public int calcTotal()
	{
		ArrayList<Integer> tots = calcFrameTotalsNoStrikeCorrect();

		int total = 0;
		if(tots.size() > 0)
		{
			total = tots.get(tots.size() - 1);
		}//end if
		else total = 0;

		if(game.size() > tots.size())
		{
			if(game.get(tots.size()).isSpare())
				total += BowlingFrame.MAX_SCORE;
			else if(game.get(tots.size()).isStrike())
			{
				if(game.size() == tots.size() + 2)
					total += BowlingFrame.MAX_SCORE + 2 * game.get(tots.size() + 1).getFirstBall();
				else total += BowlingFrame.MAX_SCORE;
			}//end else
			else total += game.get(tots.size()).getFirstBall();
		}//end if

		return total;
	}//end calcTotal()
}//end class Player
