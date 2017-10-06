import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

/**
 * Displays a gui for the user to see the scores of player(s)
 * 
 * @author Adam Walker P5
 */
public class ScoreBoard 
{
	/**Screen width*/
	public final int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	/**Screen height*/
	public final int SCREEN_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	/**Number of columns: one for shot, one for each total*/
	public final int NUM_ROWS = 2;
	/**column height*/
	protected final int COL_HEIGHT = 70;
	/**height of lower bar*/
	protected final int NAME_HEIGHT = COL_HEIGHT * 60 / (185 / 2);
	/**font height*/
	protected final int FONT_SIZE = (int)(COL_HEIGHT * .75);
	/**font height for the player name*/
	protected final int NAME_FONT_SIZE = (int)(NAME_HEIGHT * .75);
	/**width of player name*/
	protected final int NAME_WIDTH = 400;
	/**width of total label*/
	protected final int TOT_WIDTH = NAME_FONT_SIZE * 4;
	/**margin for the scoreboard scores (percent on each side)*/
	public final double X_MARGIN = .10;
	/**X Position for the player's name*/
	protected final int NAME_X = 80;
	/**Player name box width*/
	private final int NAME_BOX_WIDTH = (int)(SCREEN_WIDTH * .4);
	/**Ratio of the size of the player name screen to the image's actual size*/
	protected final double PLAYER_NAME_SIZE_RATIO = NAME_BOX_WIDTH / 1151.0;
	/**Player name box height*/
	private final int NAME_BOX_HEIGHT = (int)(PLAYER_NAME_SIZE_RATIO * 729);
	/**Font for all shots*/
	protected final Font SHOT_FONT = new Font("Arial", Font.BOLD, FONT_SIZE);
	/**Font for player names*/
	protected final Font NAME_FONT = new Font("Arial Narrow", Font.BOLD, NAME_FONT_SIZE);
	/**Player name text field width and height*/
	protected final int PLAYER_NAME_FIELD_WIDTH = (int)(908 * PLAYER_NAME_SIZE_RATIO), PLAYER_NAME_FIELD_HEIGHT = (int)(97 * PLAYER_NAME_SIZE_RATIO);
	/**Player name text field initial x and y position for player 1*/
	protected final int PLAYER_NAME_FIELD_INIT_X = (int)(110 * PLAYER_NAME_SIZE_RATIO), PLAYER_NAME_FIELD_INIT_Y = (int)(107 * PLAYER_NAME_SIZE_RATIO);
	/**Menu width for buttons*/
	private int MENU_WIDTH = 300;
	/**Menu width for arrows*/
	private int MENU_ARR_WIDTH = 100;
	/**Menu button how much to increment when fading and moving in/out a button*/
	private int MENU_BTN_FADE = 5, MENU_BTN_MOVE = 5;
	/**Menu button final x location*/
	private int MENU_BTN_X_LOC = 100;
	/**Frame itself*/
	private JFrame window;
	/**All panels*/
	private JLayeredPane windowLayers;
	/**Stores all active backgrounds for changing whose turn it is*/
	private ArrayList<JPanel> activeBackgrounds;
	/**Menu panel*/
	private JPanel menuPanel;
	/**Background Panel*/
	private JPanel backgroundPanel;
	/**Change player name panel*/
	private JPanel playerNamePanelWBtn, playerNamePanel;
	/**Players' names*/
	private ArrayList<String> myPlayerNames;
	/**All player panes (includes scoring, backgrounds)*/
	private ArrayList<JLayeredPane> players;
	/**Text fields with player names in the update player names menu*/
	private JTextField [] playerNamesInMenu;
	/**Labels that stores all player names and totals*/
	private ArrayList<JLabel> totalLbls = new ArrayList<JLabel>(), playerNameLbls = new ArrayList<JLabel>();
	/**current player's turn*/
	private int currTurn = -1;
	/**Array of Player scores from RunScoring*/
	private ArrayList<Player> playerArr;
	/**x location of the current sliding menu*/
	private int xLocMenuSlide;
	/**All buttons on the menu*/
	private JButton [] menuButtons;

	/**
	 * This class contains all of the scoreboard objects for one player
	 * and the ability to update the graphics associated with the player.
	 * @author Adam Walker P5
	 */
	private class PlayerScores
	{
		/**Shots the player threw*/
		private ArrayList<JLabel> myShots;
		/**Frame totals of the shots thrown*/
		private ArrayList<JLabel> myTotals;
		/**Total*/
		private JLabel myTotalLbl;

		/**
		 * Constructor
		 * @param shots - labels the scoreboard of the shots thrown
		 * @param tots - labels of the scoreboard of the frame totals
		 */
		public PlayerScores(ArrayList<JLabel> shots, ArrayList<JLabel> tots, JLabel totalLabel)
		{
			myShots = shots;
			myTotals = tots;
			myTotalLbl = totalLabel;
		}//end playerScores() constructor

		/**
		 * Updates the scoring graphics with new scoring information.
		 * @param game - Frames of the new game data to display.
		 */
		public void updateScoresGraphic(ArrayList<NormalFrame> game)
		{
			int currIndex = 0;
			for(int fr = 0; fr < game.size(); fr++)
			{
				ArrayList<Character> currFrame = game.get(fr).getScores();
				int i;
				for(i = 0; i < currFrame.size(); i++)
				{
					myShots.get(currIndex).setText("" + currFrame.get(i));
					currIndex++;
				}//end inner for for individual shots

				if(i == 1) //only one score
				{
					myShots.get(currIndex).setText("");
					currIndex++;
				}//end if
			}//end for for frames

			for(; currIndex < Player.NUM_FRAMES * NUM_ROWS + 1; currIndex++)
			{
				myShots.get(currIndex).setText("");
			}

			window.repaint();
		}//end updateScoreGraphic()

		/**
		 * Updates the scoring graphics with new scoring information.
		 * @param game - Totals of the new game data to display.
		 */
		public void updateTotalsGraphic(ArrayList<Integer> frameTots) 
		{
			int fr = 0;
			for(fr = 0; fr < frameTots.size(); fr++)
			{
				myTotals.get(fr).setText("" + frameTots.get(fr));
			}//end for
			for(; fr < Player.NUM_FRAMES; fr++)
			{
				myTotals.get(fr).setText("");
			}//end for

			window.repaint();
		}//end updateTotalsGraphic()

		/**
		 * Updates the total label with the correct total
		 * @param int tot - total so far
		 */
		public void updateTotal(int tot)
		{
			myTotalLbl.setText("Tot " + tot);
			window.repaint();
		}//end updateTotal()
	}//end playerScores class

	/**Player scores*/
	private ArrayList<PlayerScores> playerScores;

	/**
	 * Constructor - make window, assume 1 player
	 */
	//public ScoreBoard() {this(1, new ArrayList<String>());}

	/**
	 * Constructor - figure out how many players and their names
	 */
	public ScoreBoard()
	{
		this(1, new ArrayList<String>());

		promptPlayers();
	}//end ScoreBoard

	/**
	 * Displays the area to add/remove players
	 */
	public void promptPlayers()
	{
		//remove all player's scores from being visible
		for(JLayeredPane player: players)
			player.setVisible(false);

		if(playerNamePanelWBtn == null)
		{
			//make new panel to put player names in with the background from TCAS
			playerNamePanelWBtn = new JPanel(null);
			playerNamePanel = new JPanel(null)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void paintComponent(Graphics g)
				{
					super.paintComponent(g);
					BufferedImage img;
					try {
						img = ImageIO.read(new File("src/Resources/newGame.png"));

						g.drawImage(img, 0, 0, NAME_BOX_WIDTH, NAME_BOX_HEIGHT, null);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					setOpaque(false);
				}
			};
			playerNamePanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
			playerNamePanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);
			playerNamePanel.setPreferredSize(new Dimension(NAME_BOX_WIDTH, NAME_BOX_HEIGHT));
			playerNamePanel.setBounds((SCREEN_WIDTH - NAME_BOX_WIDTH) / 2, (SCREEN_HEIGHT - NAME_BOX_HEIGHT) / 2,NAME_BOX_WIDTH, NAME_BOX_HEIGHT);

			//add the text boxes for each player name
			playerNamesInMenu = new JTextField[Cycle.MAX_PLAYERS];
			int i;
			for(i = 0; i < Cycle.MAX_PLAYERS; i++)
			{
				JTextField currPlayer = new JTextField();

				if(myPlayerNames.size() > i)
				{
					currPlayer.setText(myPlayerNames.get(i));
				}

				currPlayer.setBounds(PLAYER_NAME_FIELD_INIT_X, PLAYER_NAME_FIELD_INIT_Y + (int)(i * 102 * PLAYER_NAME_SIZE_RATIO), PLAYER_NAME_FIELD_WIDTH, PLAYER_NAME_FIELD_HEIGHT);
				playerNamesInMenu[i] = currPlayer;
				playerNamePanel.add(currPlayer);
			}//end for

			//add a button to add players
			JButton acceptBtn = new JButton("Add Players");
			acceptBtn.setAlignmentX(JPanel.CENTER_ALIGNMENT);
			acceptBtn.setBounds(PLAYER_NAME_FIELD_INIT_X + (SCREEN_WIDTH - NAME_BOX_WIDTH) / 2, (SCREEN_HEIGHT - NAME_BOX_HEIGHT) / 2 + PLAYER_NAME_FIELD_INIT_Y + (int)(i * 102 * PLAYER_NAME_SIZE_RATIO), PLAYER_NAME_FIELD_WIDTH, PLAYER_NAME_FIELD_HEIGHT);
			acceptBtn.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					updatePlayers();
				}//end actionPerformed()
			});

			playerNamePanelWBtn.add(acceptBtn);
			playerNamePanelWBtn.add(playerNamePanel);
			playerNamePanelWBtn.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
			playerNamePanelWBtn.setOpaque(false);

			//add it to the top (z direction) of the window
			windowLayers.add(playerNamePanelWBtn, 5, 0);
			windowLayers.repaint();
		}//end if
		else
		{
			playerNamePanelWBtn.setVisible(true);

		}//end else
	}//end promptPlayers()

	/**
	 * Hides the Player names dialog & returns to normal scoring with the new players
	 */
	private void updatePlayers()
	{
		playerNamePanelWBtn.setVisible(false);

		int i = 0;
		myPlayerNames = new ArrayList<String>();
		while(!playerNamesInMenu[i].getText().equals(""))
		{
			myPlayerNames.add(playerNamesInMenu[i].getText());
			i++;
		}//end while

		while(players.size() > myPlayerNames.size())
			players.remove(players.size() - 1);

		while(playerArr.size() > myPlayerNames.size())
			playerArr.remove(players.size() - 1);

		for(i = 0; i < myPlayerNames.size(); i++)
		{
			if(playerNameLbls.size() > i)
				playerNameLbls.get(i).setText(myPlayerNames.get(i));
			else
				addPlayer(i);

			if(!(playerArr.size() > i))
				playerArr.add(new NormalPlayer(i, this));
		}//end for

		//make scores visible again
		for(JLayeredPane player: players)
			player.setVisible(true);

		//resets which players are active
		setTurn(currTurn);
	}//end hidePlayersMenu()

	/**
	 * Adds a new player to the screen.
	 * 
	 * @param int i - index of player's name and that's player's order (starting at 0)
	 */
	public void addPlayer(int i) 
	{
		//name of current player in loop.
		String currName = myPlayerNames.get(i);

		//panel for player
		JLayeredPane currPlayer = new JLayeredPane();

		//background image
		JPanel inactiveBackground = new JPanel()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);

				BufferedImage img;
				try {
					img = ImageIO.read(new File("src/Resources/inactivePlayer.png"));

					g.drawImage(img, 0, 0, (int) (SCREEN_WIDTH - X_MARGIN * SCREEN_WIDTH * 2), COL_HEIGHT * 2 + NAME_HEIGHT, null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				setOpaque(false);
			}
		};

		JPanel activeBackground = new JPanel()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);

				BufferedImage img;
				try {
					img = ImageIO.read(new File("src/Resources/activePlayer.png"));

					g.drawImage(img, 0, 0, (int) (SCREEN_WIDTH - X_MARGIN * SCREEN_WIDTH * 2), COL_HEIGHT * 2 + NAME_HEIGHT, null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				setOpaque(false);
			}
		};

		JPanel labels = new JPanel();
		labels.setPreferredSize(new Dimension((int) (SCREEN_WIDTH - X_MARGIN * SCREEN_WIDTH * 2), COL_HEIGHT * 2 + NAME_HEIGHT));
		labels.setBounds(0, 0, (int)(SCREEN_WIDTH - X_MARGIN * SCREEN_WIDTH * 2), COL_HEIGHT * 2 + NAME_HEIGHT);
		labels.setLayout(null);
		labels.setOpaque(false);
		labels.setVisible(true);

		currPlayer.setPreferredSize(new Dimension((int) (SCREEN_WIDTH - X_MARGIN * SCREEN_WIDTH * 2), COL_HEIGHT * 2 + NAME_HEIGHT));

		inactiveBackground.setPreferredSize(new Dimension((int) (SCREEN_WIDTH - X_MARGIN * SCREEN_WIDTH * 2), COL_HEIGHT * 2 + NAME_HEIGHT));
		inactiveBackground.setBounds(0, 0, (int)(SCREEN_WIDTH - X_MARGIN * SCREEN_WIDTH * 2), COL_HEIGHT * 2 + NAME_HEIGHT);
		inactiveBackground.setVisible(true);

		activeBackground.setPreferredSize(new Dimension((int) (SCREEN_WIDTH - X_MARGIN * SCREEN_WIDTH * 2), COL_HEIGHT * 2 + NAME_HEIGHT));
		activeBackground.setBounds(0, 0, (int)(SCREEN_WIDTH - X_MARGIN * SCREEN_WIDTH * 2), COL_HEIGHT * 2 + NAME_HEIGHT);
		activeBackgrounds.add(activeBackground);

		currPlayer.add(activeBackground, 2, 0);
		currPlayer.add(inactiveBackground, 1, 0);
		currPlayer.add(labels, 3, 0);

		double gridWidth = (SCREEN_WIDTH - X_MARGIN * SCREEN_WIDTH * 2) / (Player.NUM_FRAMES * NUM_ROWS + 1);

		ArrayList<JLabel> shots = new ArrayList<JLabel>();
		ArrayList<JLabel> tots = new ArrayList<JLabel>();

		//add scoreboard: first add the shot cells
		int xPos = (int) X_MARGIN * SCREEN_WIDTH;
		int yPos = 0;
		for(int j = 0; j < Player.NUM_FRAMES * NUM_ROWS + 1; j++)
		{
			JLabel currShot = new JLabel("", SwingConstants.CENTER);
			currShot.setFont(SHOT_FONT);
			currShot.setBounds(xPos, yPos, (int)gridWidth, COL_HEIGHT);
			labels.add(currShot);
			xPos = (int)((j+1) * gridWidth);
			shots.add(currShot);
		}//end inner for

		//add the totals cells
		xPos = (int) X_MARGIN * SCREEN_WIDTH;
		yPos += COL_HEIGHT;
		for(int j = 0; j < Player.NUM_FRAMES - 1; j++)
		{
			JLabel currTot = new JLabel("", SwingConstants.CENTER);
			currTot.setFont(SHOT_FONT);
			currTot.setBounds(xPos, yPos, (int)(gridWidth * 2), COL_HEIGHT);
			labels.add(currTot);
			xPos = (int)((j+1) * gridWidth * 2);
			tots.add(currTot);
		}//end inner for
		JLabel currTot = new JLabel("", SwingConstants.CENTER);
		currTot.setFont(SHOT_FONT);
		currTot.setBounds(xPos, yPos, (int)(gridWidth * 3), COL_HEIGHT);
		labels.add(currTot);
		tots.add(currTot);

		//add player name label
		yPos += COL_HEIGHT;
		JLabel playerName = new JLabel(currName, SwingConstants.LEFT);
		playerName.setFont(NAME_FONT);
		playerName.setBounds(NAME_X, yPos, NAME_WIDTH, NAME_HEIGHT);
		labels.add(playerName);

		//add total score label
		JLabel totalLabel = new JLabel("Tot 0", SwingConstants.RIGHT);
		totalLabel.setFont(NAME_FONT);
		totalLabel.setBounds((int) (SCREEN_WIDTH - X_MARGIN * SCREEN_WIDTH * 2) - NAME_X - TOT_WIDTH, yPos, TOT_WIDTH, NAME_HEIGHT);
		labels.add(totalLabel);

		playerNameLbls.add(i, playerName);
		totalLbls.add(i, totalLabel);

		backgroundPanel.add(currPlayer);
		players.add(i, currPlayer);

		playerScores.add(i, new PlayerScores(shots, tots, totalLabel));
	}

	/**
	 * Constructor - ignores names and leaves names as default
	 * 
	 * @param int numPlayers - number of players in the game
	 */
	public ScoreBoard(int numPlayers) {this(numPlayers, new ArrayList<String>());}

	/**
	 * Constructor - make window
	 * 
	 * @param int numPlayers - number of players in the game
	 * @param ArrayList<String> playerNames - list of all the players' names
	 */
	public ScoreBoard(int numPlayers, ArrayList<String> playerNames)
	{
		myPlayerNames = playerNames;
		//make window
		window = new JFrame("Bowling Scoring");
		windowLayers = new JLayeredPane();
		windowLayers.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		window.add(windowLayers);
		window.setUndecorated(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout(new FlowLayout());
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		window.getContentPane().setBackground(Color.GRAY);
		window.setVisible(true);

		//background image
		backgroundPanel = new JPanel()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				BufferedImage img;
				try {
					img = ImageIO.read(new File("src/Resources/background.png"));

					g.drawImage(img, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};

		backgroundPanel.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		backgroundPanel.setLayout(new FlowLayout());
		windowLayers.add(backgroundPanel, 2, 0);

		//add players to the window
		//ArrayList<JPanel> players = new ArrayList<JPanel>();
		players = new ArrayList<JLayeredPane>();
		activeBackgrounds = new ArrayList<JPanel>();
		playerScores = new ArrayList<PlayerScores>();

		for(int i = 0; i < numPlayers; i++)
		{
			//name of current player in loop.
			String currName = String.format("Player %d", i+1);
			if(playerNames.size() > i)
				currName = playerNames.get(i);

			myPlayerNames.add(currName);

			//add a player
			addPlayer(i);
		}//end for

		setTurn(0);

		promptPlayers();
	}//end ScoreBoard() constructor
	/**
	 * Updates the scores for a player on the GUI
	 * 
	 * @param playerNum - index of player
	 * @param game - scores the player got
	 */
	public void updateScores(int playerNum, ArrayList<NormalFrame> game)
	{
		playerScores.get(playerNum).updateScoresGraphic(game);
	}//end updateScores()

	/**
	 * Updates the totals for a player on the GUI
	 * 
	 * @param playerNum - index of player
	 * @param frameTots - totals the player got
	 */
	public void updateTotals(int playerNum, ArrayList<Integer> frameTots, int total) 
	{
		playerScores.get(playerNum).updateTotalsGraphic(frameTots);
		playerScores.get(playerNum).updateTotal(total);
	}//end updateTotals()

	/**
	 * Sets the turn on the GUI.
	 * 
	 * @param playerNum - index of player
	 */
	public void setTurn(int playerNum)
	{
		//set all other players inactive
		for(JPanel bgnd: activeBackgrounds)
		{
			bgnd.setVisible(false);
		}//end for

		//set the current player as active 
		if(playerNum >= 0)
			makePlayerActive(playerNum);

		currTurn = playerNum;
	}//end setTurn()

	/**
	 * Makes a player the active player and fades in the active image.
	 * 
	 * @param playerNum
	 */
	private void makePlayerActive(int playerNum)
	{

		/*int alpha = 0;

		Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask()
		{
			@Override
			public void run()
			{
				if(alpha < 255)
				{
					activeBackgrounds.get(playerNum).setVisible(true);
				}
			}//end run()
		}, 0, 100);*/


		activeBackgrounds.get(playerNum).setVisible(true);
	}//end makePlayerActive()

	/**
	 * displays the menu for the user
	 */
	public void displayMenu()
	{
		menuPanel = new JPanel();
		menuPanel.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		//menuPanel.setBackground(new Color(0,0,0,102));
		//menuPanel.setBackground(new Color(0,0,0,0));
		menuPanel.setOpaque(false);
		menuPanel.setVisible(true);
		menuPanel.repaint();
		windowLayers.add(menuPanel, 3, 0);
		//menuPanel.setBounds(0,0,MENU_WIDTH, SCREEN_HEIGHT);
		SlideMenu slide = new SlideMenu(1, MENU_WIDTH);
		slide.performSlide();
	}//end displayMenu()

	private class SlideMenu
	{
		private int endLoc;
		private int increment;
		private Graphics g;


		private boolean fadeButtons = false;
		private int currBtn = 0;

		private int origBtnFadeVal;
		private int btnFadeVal = 0;
		private int btnXVal = 0;
		//private int fade = 0;
		private ActionListener slideAction = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				System.out.println("increment");
				xLocMenuSlide += increment;
				if(fadeButtons)
				{
					///MENUBUTTONS
					if(currBtn >= menuButtons.length)
					{
						t.stop();
						return;
					}//end if
					
					menuButtons[currBtn].setBounds(btnXVal, 100 + currBtn*100, 50, 50);
					//menuButtons[currBtn].setBackground(new Color(0, 0, 255, 102));
					//menuButtons[currBtn].setContentAreaFilled(false);
					menuButtons[currBtn].setOpaque(false);
					btnFadeVal += MENU_BTN_FADE;
					btnXVal += MENU_BTN_MOVE;

					if(btnXVal == MENU_BTN_X_LOC)
					{
						currBtn++;
						btnXVal = 0;
						btnFadeVal = origBtnFadeVal;
					}//end if
				}//end if
				else if(xLocMenuSlide >= endLoc)
				{
					xLocMenuSlide = endLoc;
					fadeButtons = true;
				}//end else if
				else if(xLocMenuSlide > 0)
				{
					g.setColor(new Color(0,0,0,102));
					g.fillRect(xLocMenuSlide, 0, increment, SCREEN_HEIGHT);
					System.out.println("xLoc: " + xLocMenuSlide);
				}//end else if
			}//end actionPerformed()
		};
		private Timer t = new Timer(2, slideAction);

		public SlideMenu(int inc, int end)
		{
			int numBtnInc = MENU_BTN_X_LOC / MENU_BTN_MOVE;
			origBtnFadeVal = 255 - MENU_BTN_FADE * numBtnInc;
			if(origBtnFadeVal < 0) origBtnFadeVal = 0;

			endLoc = end;
			increment = inc;
			g = menuPanel.getGraphics();
			JPanel menuBtnPanel = new JPanel(null);
			menuBtnPanel.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
			menuButtons = new JButton[5];
			for(int i = 0; i < 5; i++)
			{
			menuButtons[i] = new JButton("Hello");
			menuBtnPanel.add(menuButtons[i]);
			}//end for
			menuPanel.add(menuBtnPanel);
		}//end SlideMenu()

		public void performSlide()
		{
			t.setRepeats(true);
			t.start();
		}//end performSlide()
	}//end class SlideMenu()

	/**
	 * gets the player names
	 * @return ArrayList<String> playerNames
	 */
	public ArrayList<String> getPlayerNames() {return myPlayerNames;}

	/**
	 * Returns the number of players
	 * 
	 * @return num of players
	 */
	public int getNumPlayers() {return myPlayerNames.size();}

	/**
	 * Allows access to the arraylist of players
	 * 
	 * @param ArrayList<Player> send - array from RunScoring
	 */
	public void sendPlayers(ArrayList<Player> send)
	{
		playerArr = send;
	}//end sendPlayers()
}//end class ScoreBoard
