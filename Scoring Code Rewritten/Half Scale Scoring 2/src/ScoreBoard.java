import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
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
	/**Font for all shots*/
	protected final Font SHOT_FONT = new Font("Arial", Font.BOLD, FONT_SIZE);
	/**Font for player names*/
	protected final Font NAME_FONT = new Font("Arial Narrow", Font.BOLD, NAME_FONT_SIZE);
	/**Player name text field width and height*/
	protected final int PLAYER_NAME_FIELD_WIDTH = (int)(908 * PLAYER_NAME_SIZE_RATIO), PLAYER_NAME_FIELD_HEIGHT = (int)(97 * PLAYER_NAME_SIZE_RATIO);
	/**Player name text field initial x and y position for player 1*/
	protected final int PLAYER_NAME_FIELD_INIT_X = (int)(110 * PLAYER_NAME_SIZE_RATIO), PLAYER_NAME_FIELD_INIT_Y = (int)(107 * PLAYER_NAME_SIZE_RATIO);
	/**Frame itself*/
	private JFrame window;
	/**All panels*/
	private JLayeredPane windowLayers;
	/**Stores all active backgrounds for changing whose turn it is*/
	private ArrayList<JPanel> activeBackgrounds;
	/**Background Panel*/
	private JPanel backgroundPanel;
	/**All player panes (includes scoring, backgrounds)*/
	private ArrayList<JLayeredPane> players;
	/**Labels that stores all player names and totals*/
	private ArrayList<JLabel> totalLbls = new ArrayList<JLabel>(), playerNameLbls = new ArrayList<JLabel>();
	/**Player scores*/
	private ArrayList<PlayerScoresGUI> playerScores;

	/**
	 * This class contains all of the scoreboard objects for one player
	 * and the ability to update the graphics associated with the player.
	 * @author Adam Walker
	 */
	public class PlayerScoresGUI
	{
		/**Shots the player threw*/
		private ArrayList<JLabel> myShots;
		/**Frame totals of the shots thrown*/
		private ArrayList<JLabel> myTotals;
		/**Total*/
		private JLabel myTotalLbl;
		/**Player name*/
		private JLabel myPlayerName;

		/**
		 * Constructor
		 * @param shots - labels the scoreboard of the shots thrown
		 * @param tots - labels of the scoreboard of the frame totals
		 */
		public PlayerScoresGUI(ArrayList<JLabel> shots, ArrayList<JLabel> tots, JLabel totalLabel, JLabel name)
		{
			myShots = shots;
			myTotals = tots;
			myTotalLbl = totalLabel;
			myPlayerName = name;
		}//end playerScores() constructor

		/**
		 * Updates the scoring graphics with new scoring information.
		 * @param game - Frames of the new game data to display.
		 */
		public void updateScoresGraphic(NormalFrame[] game)
		{
			for(int fr = 0; fr < Player.NUM_FRAMES; fr++)
			{
				NormalFrame currFrame = game[fr];
				char shot = ' ';
				switch(currFrame.getFirstBall())
				{
				case 0:
					shot = '-';
					break;
				case 10:
					shot = 'X';
					break;
				case 9:
					shot = '9';
					break;
				case 8:
					shot = '8';
					break;
				case 7:
					shot = '7';
					break;
				case 6:
					shot = '6';
					break;
				case 5:
					shot = '5';
					break;
				case 4:
					shot = '4';
					break;
				case 3:
					shot = '3';
					break;
				case 2:
					shot = '2';
					break;
				case 1:
					shot = '1';
					break;
				}

				char shot2 = ' ';
				if(currFrame.getFirstBall() + currFrame.getSecondBall() == NormalFrame.NUM_PINS)
				{
					shot2 = '/';
				}
				else
				{
					switch(currFrame.getSecondBall())
					{
					case 0:
						shot2 = '-';
						break;
					case 10:
						shot2 = 'X';
						break;
					case 9:
						shot2 = '9';
						break;
					case 8:
						shot2 = '8';
						break;
					case 7:
						shot2 = '7';
						break;
					case 6:
						shot2 = '6';
						break;
					case 5:
						shot2 = '5';
						break;
					case 4:
						shot2 = '4';
						break;
					case 3:
						shot2 = '3';
						break;
					case 2:
						shot2 = '2';
						break;
					case 1:
						shot2 = '1';
						break;
					}//end switch
				}
				
				myShots.get(fr * 2).setText("" + shot);
				myShots.get(fr * 2 + 1).setText("" + shot2);
				
				if(currFrame instanceof TenthFrame)
				{
					char shot3 = ' ';
					if(shot2 == '/' || shot2 == 'X')
					{	
						switch(((TenthFrame)currFrame).getThirdBall())
						{
						case 0:
							shot3 = '-';
							break;
						case 10:
							shot3 = 'X';
							break;
						case 9:
							shot3 = '9';
							break;
						case 8:
							shot3 = '8';
							break;
						case 7:
							shot3 = '7';
							break;
						case 6:
							shot3 = '6';
							break;
						case 5:
							shot3 = '5';
							break;
						case 4:
							shot3 = '4';
							break;
						case 3:
							shot3 = '3';
							break;
						case 2:
							shot3 = '2';
							break;
						case 1:
							shot3 = '1';
							break;
						}
					}
					else if(shot2 != '/' && currFrame.getSecondBall() + ((TenthFrame)currFrame).getThirdBall() == NormalFrame.NUM_PINS)
					{
						shot3 = '/';
					}
					myShots.get(fr *2 + 2).setText(""  + shot3);
				}
			}//end for for frames

			window.repaint();
		}//end updateScoreGraphic()

		/**
		 * Updates the scoring graphics with new scoring information.
		 * @param game - Totals of the new game data to display.
		 */
		public void updateTotalsGraphic(int [] frameTots) 
		{
			int fr = 0;
			for(fr = 0; fr < Player.NUM_FRAMES; fr++)
			{
				if(frameTots[fr] == -1)
					myTotals.get(fr).setText("");
				else myTotals.get(fr).setText("" + frameTots[fr]);
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

		/**
		 * Updates the total label with the correct total
		 * @param int tot - total so far
		 */
		public void updateName(String name)
		{
			myPlayerName.setText(name);
			window.repaint();
		}//end updateTotal()
	}//end playerScores class

	/**
	 * Hides the players section
	 */
	public void hidePlayers()
	{
		//remove all player's scores from being visible
		for(JLayeredPane player: players)
			player.setVisible(false);
	}//end promptPlayers()
	
	/**
	 * Shows the players section
	 */
	public void showPlayers()
	{
		//remove all player's scores from being visible
		for(JLayeredPane player: players)
			player.setVisible(false);
	}//end promptPlayers()

	/**
	 * Adds a new player to the screen.
	 * 
	 * @param int i - position to add player to
	 * @param String name - of player
	 * @return PlayerScoresGUI of player added
	 */
	public PlayerScoresGUI addPlayer(int i, String name) 
	{
		/////ADD NAME OF PLAYER
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
		JLabel playerName = new JLabel(name, SwingConstants.LEFT);
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

		PlayerScoresGUI scoresGUI = new PlayerScoresGUI(shots, tots, totalLabel, playerName);
		playerScores.add(i, scoresGUI);
		
		return scoresGUI;
	}
	
	/**
	 * Constructor - make window
	 * 
	 * @param int numPlayers - number of players in the game
	 * @param ArrayList<String> playerNames - list of all the players' names
	 */
	public ScoreBoard()
	{
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
		playerScores = new ArrayList<PlayerScoresGUI>();
	}//end ScoreBoard() constructor

	/**
	 * Makes a player the active player on the screen.
	 * 
	 * @param playerNum
	 */
	public void makePlayerActive(int playerNum)
	{
		makePlayersInactive();
		activeBackgrounds.get(playerNum).setVisible(true);
	}//end makePlayerActive()
	
	/**
	 * Makes all players no longer active
	 */
	public void makePlayersInactive()
	{
		for(int i = 0; i < activeBackgrounds.size(); i++)
			activeBackgrounds.get(i).setVisible(false);
	}//end makePlayersInactive()

	/**
	 * Returns the number of players
	 * 
	 * @return num of players
	 */
	public int getNumPlayers() {return playerScores.size();}
	
	
	/**
	 * displays the menu for the user
	 */
	/*public void displayMenu()
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
	}//end displayMenu()*/
	/*private class SlideMenu
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
	}//end class SlideMenu()*/
}//end class ScoreBoard