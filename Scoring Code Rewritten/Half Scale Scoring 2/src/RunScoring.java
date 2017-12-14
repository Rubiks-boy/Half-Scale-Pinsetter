import java.util.ArrayList;

/**
 * This is in control of updating the GUI and keeping track of all players.
 * @author Adam Walker
 */
public class RunScoring
{
	/**Current Player's turn (index of that player)*/
	private int myCurrTurn;
	/**Scoreboard object for the gui*/
	private ScoreBoard scoreboard;
	/**Players*/
	ArrayList<Player> myPlayers;
	
	/**
	 * Constructor - fills in one default player, "Player 1"
	 */
	public RunScoring() {this(1);}
	
	/**
	 * Constructor - fills in default players names "Player X"
	 */
	public RunScoring(int numPlayers)
	{

		myPlayers = new ArrayList<Player>();
		scoreboard = new ScoreBoard();
		
		
		myPlayers = new ArrayList<Player>();
		myCurrTurn = 0;
		
		for(int i = 0; i < numPlayers; i++)
		{
			myPlayers.add(new Player());
		}//end for
		
		for(int i = 0; i < numPlayers; i++)
		{
			//add a player
			myPlayers.get(i).setScoreboard(scoreboard.addPlayer(i, "Player " + (i+1)));
		}//end for
		
		scoreboard.makePlayerActive(0);
		setFirstTurn();
	}//end RunScoring() constructor
	
	/**
	 * Constructor - set up the gui and players
	 * 
	 * @param int numPlayers - how many people are playing the game
	 * @param ArrayList<String> playerNames - list of players' names
	 */
	public RunScoring(ArrayList<String> playerNames)
	{
		myPlayers = new ArrayList<Player>();
		scoreboard = new ScoreBoard();
		
		
		myPlayers = new ArrayList<Player>();
		myCurrTurn = 0;
		
		for(int i = 0; i < playerNames.size(); i++)
		{
			myPlayers.add(new Player());
		}//end for
		
		for(int i = 0; i < playerNames.size(); i++)
		{
			//add a player
			myPlayers.get(i).setScoreboard(scoreboard.addPlayer(i, playerNames.get(i)));
		}//end for
		
		scoreboard.makePlayerActive(0);
		setFirstTurn();
	}//end constructor RunScoring()
	
	/**
	 * Sets the current turn to be the index specified
	 * 
	 * @param int playerTurn - player number's turn (starts at 0)
	 */
	public void setTurn(int playerTurn)
	{
		if(playerTurn == -1)
		{
			myCurrTurn = -1;
			scoreboard.makePlayersInactive();
			System.out.println("WARNING: RUN_SCORING: No Active players");
			promptNewGame();
		}
		else if(playerTurn < myPlayers.size() && playerIsActive(playerTurn))
		{
			if(playerTurn >= 0)
			{
				myCurrTurn = playerTurn;
				scoreboard.makePlayerActive(playerTurn);
			}
		}//end if
	}//end setTurn
	
	/**
	 * Sets the next player's turn
	 */
	public void nextTurn()
	{
		System.out.println("NEXT ACTIVE: " + nextActive(myCurrTurn));
		setTurn(nextActive(myCurrTurn));
	}//end nextTurn()
	
	/**Finds the next active player index*/
	private int nextActive(int currTurn)
	{
		//make sure at least one is active
		int i;
		for(i = 0; i < myPlayers.size(); i++)
			if(playerIsActive(i))
				break;
		if(i == myPlayers.size())
		{
			return -1;
		}
		
		//set next active
		return nextActiveRecursive(currTurn);
	}//end nextActive()
	
	private int nextActiveRecursive(int newTurn)
	{
		newTurn++;
		if(newTurn >= myPlayers.size())
			newTurn = 0;
		if(playerIsActive(newTurn))
			return newTurn;
		else if(newTurn != myCurrTurn)
			return nextActive(newTurn);
		else return -1;
	}//end nextActiveRecursive()
	
	/**
	 * @param player index
	 * @return whether player is actively playing
	 */
	public boolean playerIsActive(int player)
	{
		/////Not skipped
		if(myPlayers.get(player).gameCompleted)
			return false;
		return true;
	}//end playerIsActive()
	
	/**
	 * Sets to the first possible turn (ex. player isn't skipped, game isn't complete and player is playing)
	 */
	public void setFirstTurn()
	{
		setTurn(nextActive(-1));
	}//end setFirstTurn()
	
	/**
	 * Adds a score to the current player going based on the pinfall.
	 * 
	 * @param boolean [] pins - true for pins standing
	 */
	public void addScore(boolean [] pins)
	{
		System.out.println("DEBUG: RUN_SCORING: Adding Score");
		if(myPlayers.get(myCurrTurn).addScore(pins))
			nextTurn();
	}//end addScore()
	
	/**
	 * Prompts the player to start a new game
	 */
	public void promptNewGame()
	{
		/////MAKE PROMPT, for now wait 5 sec and start new game.
		
		System.out.println("DEBUG: Starting new game");
		scoreboard.makePlayersInactive();
		
		Thread thread = new Thread()
				{
			public void run()
			{
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				newGame();
			}
				};
		thread.run();
	}//end promptNewGame()
	
	/**
	 * Resets all players' scores
	 */
	public void newGame()
	{
		for(int i = 0; i < myPlayers.size(); i++)
		{
			myPlayers.get(i).resetGame();
		}//end for
		
		setFirstTurn();
	}//end newGame()
}//end class AllPlayers