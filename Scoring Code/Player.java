import java.util.ArrayList;

/**
 * This stores a complete player, including the 10 frame game.
 * @author Adam Walker P5
 */
public abstract class Player implements Comparable<NormalPlayer>
{
	/** Whether to log each score after each roll*/
	protected final boolean PRINT_SCORES = true;
	/**Number of frames per game*/
	public final static int NUM_FRAMES = 10;
	/**Stores the 10 frames*/
	protected ArrayList<NormalFrame> game;
	/**Which ball the game is on*/
	protected boolean isSecondBall;
	/**Whether the game is completed*/
	protected boolean gameCompleted;
	/**index of which player this player is*/
	protected int myPlayerNum;
	/**Scoreboard object*/
	protected ScoreBoard myScoreBoard;

	/**
	 * Constructor - sets up the game and default data
	 */
	public Player()
	{
		game = new ArrayList<NormalFrame>();
		isSecondBall = false;
		gameCompleted = false;
		myPlayerNum = 0;
		myScoreBoard = new ScoreBoard();
	}//end Player() constructor

	/**
	 * Constructor - sets up the game and default data
	 * 
	 * @param playerNum - index of which player this player is
	 */
	public Player(int playerNum, ScoreBoard scoreBoard)
	{
		game = new ArrayList<NormalFrame>();
		isSecondBall = false;
		gameCompleted = false;
		myPlayerNum = playerNum;
		myScoreBoard = scoreBoard;
	}//end Player() constructor

	/**
	 * Adds a score to the game
	 * pre - score must be >0, <10
	 * @param score - how many additional pins were knocked down
	 * post - score added to the game
	 * @return true if the frame is now completed
	 */
	public boolean addScore(int score)
	{
		boolean noProblems = true;
		if(gameCompleted) 
			return true;
		if(!isSecondBall)
		{
			NormalFrame nextFrame;
			if(game.size() == NUM_FRAMES - 1)
				nextFrame = (NormalFrame) new TenthFrame();
			else if(game.size() == NUM_FRAMES) 
			{//tenth frame

				int ballNum = game.get(NUM_FRAMES - 1).getScores().size();

				if(ballNum > 0)
				{//third ball, tenth frame or second ball, tenth frame
					boolean retVal = false;
					try 
					{
						if(ballNum == TenthFrame.TENTH_NUM_SCORES - 1)
						{
							((TenthFrame) game.get(NUM_FRAMES - 1)).setThirdBall(score);
							gameCompleted = true;
							retVal = true;
						}//end if
						else
						{
							((TenthFrame) game.get(NUM_FRAMES - 1)).setSecondBall(score);
						}
					} catch(Exception e) 
					{
						System.out.println(e.getMessage());
						noProblems = false;
					}//end catch
					Cycle.newSet();
					if(PRINT_SCORES) printScores();
					updateScores();
					return retVal && noProblems;
				}//end inner if
				else nextFrame = new NormalFrame();
			}//end inner else
			else
				nextFrame = new NormalFrame();


			try 
			{
				nextFrame.setFirstBall(score);
				game.add(nextFrame);
			}catch(Exception e) 
			{
				System.out.println(e.getMessage());
				noProblems = false;
			}//end catch

			if(score < 0 || score == BowlingFrame.MAX_SCORE) 
			{
				Cycle.newSet();
				if(PRINT_SCORES) printScores();
				if(noProblems)
				{
					isSecondBall = false;
					updateScores();
					return game.size() < NUM_FRAMES;
				}
				else return false;
			}//end if
			else 
			{
				Cycle.standingPins();
				isSecondBall = true;
				if(PRINT_SCORES) printScores();
				updateScores();
				return false;
			}//end else
		}//end if
		else
		{//second ball cycle
			try 
			{
				System.out.println("Second ball");
				game.get(game.size() - 1).setSecondBall(score);
			}catch(Exception e) 
			{
				System.out.println(e.getMessage());
				noProblems = false;
			}//end catch
			Cycle.newSet();
			if(noProblems)
				isSecondBall = false;
			if(PRINT_SCORES) printScores();
			updateScores();
			return noProblems && (game.size() < NUM_FRAMES 
					|| game.get(game.size() - 1).getFirstBall() + score < BowlingFrame.MAX_SCORE);
		}//end else
	}//end addScore()

	/**
	 * Calculates the score based on the pinfall data
	 * @param pins - individual pinfall data
	 * @return score
	 */
	public static int calcScore(boolean [] pins)
	{
		int score = pins.length;
		for(boolean pin : pins)
			if(pin) score--;
		return score;
	}//end calcScore()

	/**
	 * Adds the score to the scoreboard based on which pins are up and down.
	 * @param pins - individual pinfall data
	 * @return true if frame is completed.
	 */
	public boolean addScore(boolean [] pins)
	{
		if(gameCompleted)
			return true;

		int score = calcScore(pins);
		boolean retVal;

		//10th frame
		if(game.size() == NUM_FRAMES)
		{
			//must be 2nd or third ball
			int ballNum = game.get(NUM_FRAMES - 1).getScores().size();

			if(ballNum == 1)
			{
				//second ball 10th frame
				int firstBall = game.get(NUM_FRAMES - 1).getFirstBall();
				if(firstBall == BowlingFrame.MAX_SCORE)
				{
					//strike in first ball
					try
					{
						((TenthFrame) game.get(NUM_FRAMES - 1)).setSecondBall(score);
					}catch(Exception e)
					{
						System.out.println(e.getMessage());
					}//end catch
					
					retVal = false;
				}//first ball was a strike if
				else
				{
					//shooting for the spare
					try
					{
						if(score - firstBall >= 0)
							((TenthFrame) game.get(NUM_FRAMES - 1)).setSecondBall(score - firstBall);
						else ((TenthFrame) game.get(NUM_FRAMES - 1)).setSecondBall(0);
					}catch(Exception e)
					{
						System.out.println(e.getMessage());
					}//end catch

					if(game.get(NUM_FRAMES - 1).getSecondBall() + firstBall < BowlingFrame.MAX_SCORE)
					{
						gameCompleted = true;
						retVal = true;
					}//end inner if
					else retVal = false;
				}//shooting for the spare if
			}//end if 2nd 10th frame
			else
			{
				int firstBall = game.get(NUM_FRAMES - 1).getFirstBall();
				int secondBall = game.get(NUM_FRAMES - 1).getSecondBall();
				//third ball 10th frame
				if(secondBall == BowlingFrame.MAX_SCORE ||
						(secondBall + firstBall == BowlingFrame.MAX_SCORE && firstBall != BowlingFrame.MAX_SCORE))
				{
					//strike in second or spare in first and second
					try
					{
						((TenthFrame) game.get(NUM_FRAMES - 1)).setThirdBall(score);
					}catch(Exception e)
					{
						System.out.println(e.getMessage());
					}//end catch
				}//third ball is independent if
				else
				{
					try
					{
						if(score - secondBall >= 0)
							((TenthFrame) game.get(NUM_FRAMES - 1)).setThirdBall(score - secondBall);
						else ((TenthFrame) game.get(NUM_FRAMES - 1)).setThirdBall(0);
					}catch(Exception e)
					{
						System.out.println(e.getMessage());
					}//end catch
				}//third ball is shooting for the spare else

				gameCompleted = true;
				retVal = true;
			}//end else 3rd 10th frame
		}//end 10th frame if
		else
		{
			if(!isSecondBall)
			{
				//first ball in frames 1-10
				
				//create frame
				BowlingFrame nextFrame;
				if(game.size() == NUM_FRAMES - 1)
					nextFrame = new TenthFrame();
				else nextFrame = new NormalFrame();
				
				//add the first ball
				try 
				{
					nextFrame.setFirstBall(score);
					game.add((NormalFrame) nextFrame);
				}catch(Exception e) 
				{
					System.out.println(e.getMessage());
				}//end catch

				if(game.size() == NUM_FRAMES)
					retVal = false;
				else if(score < 0 || score == BowlingFrame.MAX_SCORE) 
				{
					isSecondBall = false;
					retVal = true;
				}//end if
				else 
				{
					isSecondBall = true;
					retVal = false;
				}//end else
			}//end first ball if
			else
			{
				//second ball
				
				int firstBallScore = game.get(game.size() - 1).getFirstBall();
				try 
				{
					if(score - firstBallScore > 0)
						game.get(game.size() - 1).setSecondBall(score - firstBallScore);
					else game.get(game.size() - 1).setSecondBall(0);
				}catch(Exception e) 
				{
					System.out.println(e.getMessage());
				}//end catch
				isSecondBall = false;

				retVal = true;
			}//end second ball else
		}//end other frame else
		
		if(retVal)
			Cycle.newSet();
		else Cycle.standingPins();
		if(PRINT_SCORES) printScores();
		updateScores();
		return retVal;
	}//end addScore()

	/*public boolean addScore(boolean [] pins)
	{
		int score = calcScore(pins);

		if(gameCompleted) 
			return true;
		if(!isSecondBall)
		{
			BowlingFrame nextFrame;
			if(game.size() == NUM_FRAMES -1)
				nextFrame = new TenthFrame();
			else if(game.size() == NUM_FRAMES) 
			{//tenth frame

				int ballNum = game.get(NUM_FRAMES - 1).getScores().size();

				if(ballNum > 0)
				{//third ball, tenth frame or second ball, tenth frame
					System.out.println("Second/Third ball set");
					try 
					{
						int firstBallScore = game.get(NUM_FRAMES - 1).getFirstBall();

						if(ballNum == TenthFrame.TENTH_NUM_SCORES - 1)
						{
							int secondBallScore = game.get(NUM_FRAMES - 1).getSecondBall();
							if(secondBallScore != BowlingFrame.MAX_SCORE && secondBallScore + firstBallScore != BowlingFrame.MAX_SCORE)
							{

								if(score - secondBallScore > 0)
									((TenthFrame) game.get(NUM_FRAMES - 1)).setThirdBall(score - secondBallScore);
								else ((TenthFrame) game.get(NUM_FRAMES - 1)).setThirdBall(0);

							}
							else
							{
								((TenthFrame) game.get(NUM_FRAMES - 1)).setThirdBall(score);
							}

							gameCompleted = true;
						}//end if
						else
						{
							if(firstBallScore != BowlingFrame.MAX_SCORE)
							{
								if(score - firstBallScore > 0)
									((TenthFrame) game.get(NUM_FRAMES - 1)).setSecondBall(score - firstBallScore);
								else ((TenthFrame) game.get(NUM_FRAMES - 1)).setSecondBall(0);
							}
							else
							{
								((TenthFrame) game.get(NUM_FRAMES - 1)).setSecondBall(score);
							}
						}
					} catch(Exception e) 
					{
						System.out.println(e.getMessage());
					}//end catch
					Cycle.newSet();
					if(PRINT_SCORES) printScores();
					updateScores();
					return true;
				}//end inner if
				else nextFrame = new NormalFrame();
			}//end inner else
			else
				nextFrame = new NormalFrame();


			try 
			{
				nextFrame.setFirstBall(score);
				game.add(nextFrame);
			}catch(Exception e) 
			{
				System.out.println(e.getMessage());
			}//end catch

			if(score < 0 || score == BowlingFrame.MAX_SCORE) 
			{
				Cycle.newSet();
				isSecondBall = false;
				if(PRINT_SCORES) printScores();
				updateScores();
				return true;
			}//end if
			else 
			{
				Cycle.standingPins();
				isSecondBall = true;
				if(PRINT_SCORES) printScores();
				updateScores();
				return false;
			}//end else
		}//end if
		else
		{//second ball cycle
			int firstBallScore = game.get(game.size() - 1).getFirstBall();
			try 
			{
				if(score - firstBallScore > 0)
					game.get(game.size() - 1).setSecondBall(score - firstBallScore);
				else game.get(game.size() - 1).setSecondBall(0);
			}catch(Exception e) 
			{
				System.out.println(e.getMessage());
			}//end catch
			Cycle.newSet();
			isSecondBall = false;
			if(PRINT_SCORES) printScores();
			updateScores();
			return true;
		}//end else
	}//end addScore()*/

	/**
	 * updates the score graphics
	 */
	public void updateScores()
	{
		myScoreBoard.updateScores(myPlayerNum, game);
		myScoreBoard.updateTotals(myPlayerNum, calcFrameTotals(), calcTotal());
	}//end updateScores()

	/**
	 * Generates the totals of frames bowled so far
	 * 
	 * @return ArrayList<Integer> with each frame's total
	 * 	(this will vary in size based on number of frames bowled)
	 */
	public abstract ArrayList<Integer> calcFrameTotals();

	/**
	 * Generates the total of the game
	 * 
	 * @return int of the score total
	 */
	public abstract int calcTotal();

	/**
	 * Compares two players' total scores
	 */
	@Override
	public int compareTo(NormalPlayer p) 
	{
		return this.calcTotal() - p.calcTotal();
	}//end compareTo()
	
	/**
	 * Prints the entire game onto the screen
	 * 
	 * post - System.out changed
	 */
	public abstract void printScores();
	
	/**
	 * Resets all scores for the player
	 * 
	 * post - all scores deleted
	 */
	public void resetGame()
	{
		game = new ArrayList<NormalFrame>();
		isSecondBall = false;
		gameCompleted = false;
		myPlayerNum = 0;
		myScoreBoard = new ScoreBoard();
	}//end resetGame()
	
	/**
	 * Resets all scores for the player
	 * 
	 * @param int playerNum - index of player
	 * @param ScoreBoard - scoreboard used for other players
	 * 
	 * post - all scores deleted
	 */
	public void resetGame(int playerNum, ScoreBoard scoreBoard)
	{	
		game = new ArrayList<NormalFrame>();
		isSecondBall = false;
		gameCompleted = false;
		myPlayerNum = playerNum;
		myScoreBoard = scoreBoard;
	}//end resetGame()
	
	/**
	 * Returns whether the game is done
	 */
	public boolean gameIsComplete() {return gameCompleted;}
}//end class Player
