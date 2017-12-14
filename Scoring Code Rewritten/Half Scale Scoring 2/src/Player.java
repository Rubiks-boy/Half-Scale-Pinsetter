/**
 * This stores a complete player, including the 10 frame game.
 * @author Adam Walker P5
 */
public class Player
{
	/**Number of frames per game*/
	public final static int NUM_FRAMES = 10;
	/**Stores the 10 frames*/
	protected NormalFrame[] game;
	/**Current frame the game is on*/
	protected int frameIndex;
	/**Whether the game is completed*/
	protected boolean gameCompleted;
	/**Scoreboard GUI*/
	protected ScoreBoard.PlayerScoresGUI gui;
	/**Name of player*/
	protected String playerName;

	/**
	 * Constructor - sets up the game and default data
	 */
	public Player()
	{
		gui = null;
		frameIndex = 0;
		gameCompleted = false;
		game = new NormalFrame[NUM_FRAMES];
		for (int i = 0; i < game.length - 1; i++)
			game[i] = new NormalFrame();
		game[NUM_FRAMES - 1] = new TenthFrame();
	}//end Player() constructor

	/**
	 * Adds the score to the player's game based on which pins are up and down.
	 * @param pins - individual pinfall data
	 * @return true if turn is over
	 */
	public boolean addScore(boolean [] pins)
	{
		boolean frameOver = false;
		
		//calculate num of pins standing
		int pinsStanding = 0;
		for(int i = 0; i < pins.length; i++)
			if(pins[i])
				pinsStanding++;
		
		System.out.println(pinsStanding + " PINS STANDING");
		try
		{
			if(frameIndex == NUM_FRAMES - 1)
			{//tenth frame
				if(game[frameIndex].getFirstBall() == -1)
				{
					game[frameIndex].setFirstBall(pinsStanding);
				}
				else if(game[frameIndex].getSecondBall() == -1)
				{
					if(game[frameIndex].setSecondBall(pinsStanding))
					{
						gameCompleted = true;
						frameOver = true;
					}
				}
				else 
				{
					if(((TenthFrame)(game[frameIndex])).setThirdBall(pinsStanding))
					{
						gameCompleted = true;
						frameOver = true;
					}
				}
			}//end if
			else
			{//frame 1-9
				if(game[frameIndex].getFirstBall() == -1)
				{
					if(game[frameIndex].setFirstBall(pinsStanding))
					{
						frameIndex++;
						frameOver = true;
					}
				}
				else 
				{
					if(game[frameIndex].setSecondBall(pinsStanding))
					{
						frameIndex++;
						frameOver = true;
					}
				}
			}//end else
		}catch(Exception e) {
			System.out.println("ERROR: ADDING_SCORE");
			System.out.println(e.getMessage());
			updateScoreboard();
			return false;
		}//end catch
		
		updateScoreboard();
		return frameOver;
	}//end addScore()

	/**
	 * Resets all scores for the player
	 * 
	 * post - all scores deleted
	 */
	public void resetGame()
	{	
		System.out.println("DEBUG: PLAYER_RESET_GAME");
		for (int i = 0; i < game.length - 1; i++)
			game[i] = new NormalFrame();
		game[NUM_FRAMES - 1] = new TenthFrame();
		gameCompleted = false;
		frameIndex = 0;
		updateScoreboard();
	}//end resetGame()

	/**
	 * Returns whether the game is done
	 */
	public boolean gameIsComplete() {return gameCompleted;}

	/**
	 * Calculates frame totals but removes the total for strings of strikes
	 * 
	 * @return int[] with each frame's total
	 * 	(-1's for totals not completed yet)
	 */
	private int[] calcFrameTotals()
	{
		int[] tots = calcFrameTotalsNoStrikeCorrect();

		//game is over
		if(tots[NUM_FRAMES - 1] != -1)
			return tots;

		//find the last location with a total
		int lastTotIndex = -1;
		for(int i = NUM_FRAMES - 1; i >= 0; i--)
		{
			if(tots[i] != -1)
			{
				lastTotIndex = i;
				break;
			}
		}//end for

		if(lastTotIndex != -1 && game[lastTotIndex].getFirstBall() == NormalFrame.NUM_PINS)
		{
			if(lastTotIndex == NUM_FRAMES - 2)
			{
				if(game[lastTotIndex + 1].getFirstBall() == NormalFrame.NUM_PINS &&
						game[lastTotIndex + 1].getSecondBall() == NormalFrame.NUM_PINS)
				{
					tots[lastTotIndex] = -1;
					lastTotIndex--;
				}
			}//end if for 9th frame

			for(int i = lastTotIndex; i >= 0; i--)
			{
				if(game[lastTotIndex + 1].getFirstBall() == NormalFrame.NUM_PINS &&
						game[lastTotIndex + 2].getFirstBall() == NormalFrame.NUM_PINS)
				{
					tots[lastTotIndex] = -1;
					lastTotIndex--;
				}//end if
			}//end for
		}//end outer if

		return tots;
	}//end calcFrameTotals()

	/**
	 * Generates the totals of frames bowled so far
	 * 
	 * @return int[] with each frame's total
	 * 	(-1's for totals not able to be calculated yet)
	 */
	private int[] calcFrameTotalsNoStrikeCorrect()
	{
		int[] tots = new int[NUM_FRAMES];

		int fr;

		for(fr = 0; fr < NUM_FRAMES; fr++)
		{
			NormalFrame curr = game[fr];

			if(fr == NUM_FRAMES - 1)
			{
				if(gameCompleted)
				{ 
					if(((TenthFrame)curr).getThirdBall() != -1)
						tots[fr] += ((TenthFrame)curr).getThirdBall();
					tots[fr] += tots[fr - 1] + curr.getSecondBall() + curr.getFirstBall();
				}
				else tots[fr] = -1;
			}//end if
			else if(curr.getFirstBall() == NormalFrame.NUM_PINS || curr.getSecondBall() != -1)
			{//frame is complete
				if(curr.getFirstBall() == NormalFrame.NUM_PINS)
				{
					int nextBall, nextNextBall;

					nextBall = game[fr+1].getFirstBall();
					if(nextBall == -1)
						break;

					if(nextBall == NormalFrame.NUM_PINS && fr < NUM_FRAMES - 2)
						nextNextBall = game[fr+2].getFirstBall();
					else nextNextBall = game[fr+1].getSecondBall();

					if(nextNextBall == -1) 
						break;

					tots[fr] = (fr == 0 ? 0 : tots[fr-1]) + curr.getFirstBall() + 
							+ nextBall + nextNextBall;
				}
				else if(curr.getFirstBall() + curr.getSecondBall() == NormalFrame.NUM_PINS)
				{
					int nextBall;

					nextBall = game[fr+1].getFirstBall();
					if(nextBall == -1)
						break;

					tots[fr] = (fr == 0 ? 0 : tots[fr-1]) + curr.getFirstBall() + 
							curr.getSecondBall() + nextBall;	
				}
				else
				{
					tots[fr] = (fr == 0 ? 0 : tots[fr-1]) + 
							curr.getFirstBall() + curr.getSecondBall();
				}
			}//end else
			else
				break;
		}//end for

		for(; fr < NUM_FRAMES; fr++)
			tots[fr] = -1;

		return tots;
	}//end calcFrameTotalsNoStrikeCorrect()

	/**
	 * Calculates the total of the game thus far
	 * (Excludes strings of strikes)
	 * @return total of game
	 */
	public int calcTotal()
	{
		int [] tots = calcFrameTotals();

		//find the last location with a total
		int lastTotIndex = -1;
		for(int i = NUM_FRAMES - 1; i >= 0; i--)
		{
			if(tots[i] != -1)
			{
				lastTotIndex = i;
				break;
			}
		}//end for

		if(lastTotIndex == -1) return 0;
		else return tots[lastTotIndex];
	}//end calcTotal()
	
	/**
	 * Sets the scoreboard for the player.
	 * @param ScoreBoard.PlayerScoresGUI with all player score sections/labels
	 */
	public void setScoreboard(ScoreBoard.PlayerScoresGUI lbls)/////
	{
		gui = lbls;
	}//end setScoreboard()
	
	/**
	 * Updates the scoreboard for the player, if the scoreboard exists.
	 */
	public void updateScoreboard()
	{
		if(gui != null)
		{
			System.out.println("DEBUG: PLAYER: Updating scoreboard");

			gui.updateScoresGraphic(game);
			gui.updateTotalsGraphic(calcFrameTotals());
			gui.updateTotal(calcTotal());
			gui.updateName(playerName);
		}
	}//end updateScoreboard()
	
	/**
	 * Updates the player's name
	 * @param new name of player
	 */
	public void setPlayerName(String name)
	{
		playerName = name;
		updateScoreboard();
	}//end setPlayerName
}//end class Player
