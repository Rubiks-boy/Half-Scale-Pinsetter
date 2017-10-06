import java.util.ArrayList;

/**
 * This is in control of updating the GUI and keeping track of all players.
 * @author Adam Walker P5
 */
public class RunScoring
{
	/**All of the players in the game*/
	private ArrayList<Player> myPlayers;
	/**GUI object*/
	private ScoreBoard myGui;
	/**Current Player's turn (index of that player)*/
	private int myCurrTurn;
	
	/**
	 * Constructor - defaults at one player and default names
	 */
	//public RunScoring() {this(1, new ArrayList<String>());}
	
	/**
	 * Constructor - prompts user with new game
	 */
	public RunScoring()
	{
		myGui = new ScoreBoard();
		myPlayers = new ArrayList<Player>();
		myGui.sendPlayers(myPlayers);
		myCurrTurn = 0;
		
		setTurn(0);
	}
	
	/**
	 * Constructor - default the names of the players
	 * 
	 * @param int numPlayers - how many people are playing the game
	 */
	public RunScoring(int numPlayers) {this(numPlayers, new ArrayList<String>());}
	
	/**
	 * Constructor - set up the gui and players
	 * 
	 * @param int numPlayers - how many people are playing the game
	 * @param ArrayList<String> playerNames - list of players' names
	 */
	public RunScoring(int numPlayers, ArrayList<String> playerNames)
	{
		myGui = new ScoreBoard(numPlayers, playerNames);
		myPlayers = new ArrayList<Player>();
		myGui.sendPlayers(myPlayers);
		myCurrTurn = 0;
		
		for(int i = 0; i < myGui.getNumPlayers(); i++)
		{
			myPlayers.add(new NormalPlayer(i, myGui));
		}//end for
		
		setTurn(0);
	}//end constructor RunScoring()
	
	/**
	 * Sets the current turn to be the index specified
	 * 
	 * @param int playerTurn - player number's turn (starts at 0)
	 */
	public void setTurn(int playerTurn)
	{
		if(playerTurn < myGui.getNumPlayers() && playerTurn >= 0)
		{
			myCurrTurn = playerTurn;
			myGui.setTurn(playerTurn);
		}//end if
	}//end setTurn
	
	/**
	 * Sets the next player's turn
	 */
	public void nextTurn()
	{
		System.out.println("DEBUG: NUMPLAYERS: " + myGui.getNumPlayers());
		int newTurn = myCurrTurn + 1;
		if(newTurn >= myGui.getNumPlayers())
			newTurn = 0;
		while(myPlayers.get(newTurn).gameIsComplete())
		{
			newTurn++;
			if(newTurn >= myGui.getNumPlayers())
				newTurn = 0;
			if((myCurrTurn + 1 >= myGui.getNumPlayers() && newTurn == 0) || newTurn == myCurrTurn + 1 )
			{
				promptNewGame();
				return;
			}
		}//end while

		setTurn(newTurn);
	}//end nextTurn()
	
	/**
	 * Sets to the first possible turn (ex. player isn't skipped, game isn't complete and player is playing)
	 */
	public void setFirstTurn()
	{
		int firstTurn = 0;
		while(myPlayers.get(firstTurn).gameIsComplete())
		{
			firstTurn++;
			if(firstTurn >= myGui.getNumPlayers())
			{
				System.out.println("Couldn't find a first player to play.");
				promptNewGame();
				return;
			}//end if
		}//end while
		
		setTurn(firstTurn);
	}//end setFirstTurn()
	
	/**
	 * Adds a score to the current player going
	 * 
	 * @param int score - how many pins were knocked down
	 */
	public void addScore(int score)
	{
		if(myPlayers.get(myCurrTurn).addScore(score))
			nextTurn();
	}//end addScore()
	
	/**
	 * Adds a score to the current player going based on the pinfall.
	 * 
	 * @param boolean [] pins - true for pins standing
	 */
	public void addScore(boolean [] pins)
	{
		System.out.println("Score is being added");
		if(myPlayers.get(myCurrTurn).addScore(pins))
			nextTurn();
	}//end addScore()
	
	/**
	 * Prompts the player to start a new game
	 */
	public void promptNewGame()
	{
		System.out.println("Starting new game");
		myGui.setTurn(-1);
		
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
		//newGame();
	}//end promptNewGame()
	
	/**
	 * Resets all players' scores
	 */
	public void newGame()
	{
		for(int i = 0; i < myGui.getNumPlayers(); i++)
		{
			myPlayers.get(i).resetGame(i, myGui);
			myPlayers.get(i).updateScores();
		}//end for
		
		setFirstTurn();
	}//end newGame()
	
	/**
	 * Displays the menu for the user
	 */
	public void displayMenu()
	{
		myGui.displayMenu();
	}//end displayMenu()
}//end class AllPlayers