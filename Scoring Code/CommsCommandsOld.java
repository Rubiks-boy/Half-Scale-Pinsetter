import java.util.ArrayList;
/**
 * This class decrypts, interprets, and prepares commands to send or that were received.
 * 
 * @author Adam Walker P5
 * @deprecated
 */
public class CommsCommandsOld
{
	/*
	 * ALL COMMANDS ARE IN THE FOLLOWING FORMAT:
	 * 
	 * [BEG(1)][LEN(2)][CMD][TERM(1)]
	 * where (n) signifies size n bytes
	 */

	public static final String [] ERROR_CODES = 
		{
				"Out of range",
				"Pin loading timeout"
		};
	//0 = fatal. 1 = semi-fatal. 2 = non-fatal
	public static final int [] ERROR_FATAL = 
		{
				1, 0
		};
	public static final char BEG = '`';
	public static final char TERMINATOR = '~';
	public static final int LEN_BYTES = 2;
	public static final int TYPE_LOC = 3;
	public static final int ARG_LOC = 4;
	public static final int BYTES_NO_ARGS = 5;
	public static final int MAX_FAILED_ATTEMPTS = 5;

	public static class CMD
	{
		public char cmdType;
		public String cmdDebug;
		public boolean isConfirm;
		public boolean cmdAfter;

		public CMD(char type, String debug, boolean confirm, boolean after)
		{
			cmdType = type;
			cmdDebug = debug;
			isConfirm = confirm;
			cmdAfter = after;
		}//end CMD() constructor

		public CMD(CMD old)
		{
			this.cmdType = old.cmdType;
			this.cmdDebug = old.cmdDebug;
			this.isConfirm = old.isConfirm;
			this.cmdAfter = old.cmdAfter;
		}//end CMD() constructor
	}//end class CMD

	private static ArrayList<CMD> cmds = new ArrayList<CMD>();
	private static char lastCmdType;
	private static String lastCmdArgs;
	private static int numFailedAttempts;

	static
	{
		cmds.add(new CMD('C', "Cycle", false, false));
		cmds.add(new CMD('B', "Scores", false, false));
		cmds.add(new CMD('E', "Error", false, false));
		cmds.add(new CMD('P', "Pow Set", false, false));
		cmds.add(new CMD('Q', "Pow Msg", false, false));
		cmds.add(new CMD('D', "Cycle Done", false, false));
		cmds.add(new CMD('L', "Light Set", false, false));
		cmds.add(new CMD('M', "Light Msg", false, false));

		cmds.add(new CMD('0', "CS", true, true));
		cmds.add(new CMD('1', "CC", true, true));
		cmds.add(new CMD('2', "CG", true, false));
		cmds.add(new CMD('3', "CB", true, true));
		cmds.add(new CMD('4', "CT", true, false));
	}//end CommsCommandsOld() constructor

	public static CMD findCMD(char cmdType)
	{
		for(int i = 0; i < cmds.size(); i++)
		{
			if(cmds.get(i).cmdType == cmdType)
				return new CMD(cmds.get(i));
		}
		return null;
	}//end findCMD

	/**
	 * Interprets the command and calls the corresponding action 
	 * associated with the command.
	 * 
	 * @param CMD cmdPassed - found command that correlates
	 * @param char type - what the first command is
	 * @param char type2 - 2nd command (if first command requires
	 * 	second command after it as an argument)
	 * @param String args - arguments (if command requires it)
	 */
	public static void interpretCommand(CommsCommandsOld.CMD cmdPassed, 
			char type, char type2, String args)
	{
		System.out.println("Type: " + type);
		System.out.println("Type2: " + type2);
		System.out.println("Args: " + args);

		int i;
		for(i = 0; i < cmds.size(); i++)
		{
			if(cmds.get(i).cmdType == type)
				break;
		}//end for

		switch(i)
		{
			case 1: 
				scores(args);
				break;
			case 2: 
				error(args);
				break;
			case 4: 
				powMsg(args);
				break;
			case 5: 
				cycleDone(args);
				break;
			case 7: 
				lightMsg(args);
				break;
			case 8: 
				cSend(type2, args);
				break;
			case 10: 
				cGood();
				break;
			case 11: 
				cBad(type2, args);
				break;
			default: 
				System.out.println("Could not interpret command: " + i + " " + type + " " + type2 + " " + args);
		}//end switch
	}//end interpretCommand()

	private static void cTimeout(char cmdAfter, String args) 
	{
		System.out.println("Command failed too many times. CMD: " + lastCmdType + " " + lastCmdArgs);
		numFailedAttempts = 0;
	}

	private static void cBad(char cmdAfter, String args) 
	{
		if(++numFailedAttempts < MAX_FAILED_ATTEMPTS)
		{
		System.out.println("Command sent was bad. Interpreting resend.");
		interpretCommand(findCMD(cmdAfter), cmdAfter, '\0', args);
		}
		else
		{
			cTimeout(cmdAfter, args);
		}
	}

	private static void cGood() 
	{
		interpretCommand(findCMD(lastCmdType), lastCmdType, '\0', lastCmdArgs);
	}
	
	private static void cConfirm(char cmdAfter, String args)
	{
		String len = "" + (args.length() + 1);
		if(len.length() == 1)
			len = " " + len;
		Cycle.sendCmd("`" + (len) + "1" + cmdAfter + args + "~");
	}

	private static void cSend(char cmdAfter, String args) 
	{
		lastCmdType = cmdAfter;
		lastCmdArgs = args;
		cConfirm(cmdAfter, args);
	}

	private static void lightMsg(String args) 
	{
		System.out.println("Light state is now: " + (args.charAt(0) == 1 ? "ON" : "OFF"));
	}

	private static void cycleDone(String args) 
	{
		System.out.print("Cycle ");
		int cycleType = Integer.parseInt("" + args.charAt(0));
		switch(cycleType)
		{
			case 0:
				System.out.print("Reset");
				break;
			case 1:
				System.out.print("Respot");
				break;
			case 2:
				System.out.print("Gutter");
				break;
			case 3:
				System.out.print("Selective Reset");
				break;
			default:
				System.out.print("[Unknown cycle type]");
		}
		System.out.println(" is completed.");
	}

	private static void powMsg(String args) 
	{
		System.out.print("Power state changed to: ");
		System.out.println(args.charAt(0) == '1' ? "ON" : "OFF");
	}

	private static void error(String args) 
	{
		System.out.print("Error reported: ");
		int errorCode = Integer.parseInt(args);
		if(errorCode < ERROR_CODES.length)
			System.out.println(errorCode + " (" + ERROR_CODES[errorCode] + ")");
		if(ERROR_FATAL[errorCode] == 0)
			System.out.println("Error fatal. Machine had to power off.");
		else if(ERROR_FATAL[errorCode] == 1)
			System.out.println("Error not fatal. Bowling pauses but machine remains on.");
		else System.out.println("Error not fatal. Bowling can continue.");
	}

	private static void scores(String args) 
	{
		//verify scores are accurate
		boolean odd = (args.charAt(0) == '1' ? true : false);
		int score = Integer.parseInt(args.substring(11), 10);

		//scoring representation with booleans.
		boolean [] pinScore = new boolean[BowlingFrame.MAX_SCORE];
				
		boolean testOdd = false;
		int testScore = 0;
		
		for(int i = 1; i < args.length(); i++)
		{
			if(args.charAt(i) == '1')
			{
				testOdd = !testOdd;
				pinScore[i] = true;
			}else pinScore[i] = false;
		}
		for(int i = 1; i <= BowlingFrame.MAX_SCORE; i++)
		{
			if(args.charAt(i) == '1')
				testScore++;
		}
		
		if(odd != testOdd || score != testScore)
		{
			System.out.println("Score sent is invalid.");
			return;
		}
		
		System.out.println("Adding score: " + score);
		System.out.println("Pins: " + args.substring(1, BowlingFrame.MAX_SCORE + 1));
		
		Cycle.addScore(pinScore);
	}

	///SEND COMMANDS
	public static void doResetCycle()
	{
		Cycle.sendCmd(BEG + "020C0" + TERMINATOR);
	}//end doResetCycle()
	
	public static void doResetCycle(boolean[] pins)
	{
		String pinStr = new String();
		int score = 0;
		for(boolean pin : pins)
		{
			if(pin)
			{
				pinStr += "1";
				score++;
			}
			else pinStr += "0";
		}//end for
		
		Cycle.sendCmd(BEG + "120C3" + pinStr + (score == BowlingFrame.MAX_SCORE ? "10" : "0" + score) + TERMINATOR);
	}//end doResetCycle()
	
	public static void doRespotCycle()
	{
		Cycle.sendCmd(BEG + "020C1" + TERMINATOR);
	}//end doRespotCycle()
	
	public static void doGutterCycle()
	{
		Cycle.sendCmd(BEG + "020C2" + TERMINATOR);
	}//end doGutterCycle()
	
	public static void changePowState(boolean state)
	{
		Cycle.sendCmd(BEG + "020P" + (state ? "1" : "0") + TERMINATOR);
	}//end changePowState()
	
	public static void changeLightState(boolean state)
	{
		Cycle.sendCmd(BEG + "020L" + (state ? "1" : "0") + TERMINATOR);
	}//end changeLightState()

}//end class CommsCommandsOld

/**
 * Constructor - make window
 * 
 * @param int numPlayers - number of players in the game
 * @param ArrayList<String> playerNames - list of all the players' names
 */
/*public ScoreBoard(int numPlayers, ArrayList<String> playerNames)
{
	//make window
	window = new JFrame("Bowling Scoring");
	window.setUndecorated(true);
	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	window.setLayout(new FlowLayout());
	window.setExtendedState(JFrame.MAXIMIZED_BOTH);
	window.getContentPane().setBackground(Color.GRAY);
	window.setVisible(true);

	//background image
	JPanel backgroundPanel = new JPanel()
			{
				@Override
				public void paintComponent(Graphics g)
				{
					super.paintComponent(g);
					 BufferedImage img;
					try {
						img = ImageIO.read(new File("src/Resources/background.png"));

						g.drawImage(img, 0, 0, (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(), null);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			};

	backgroundPanel.setPreferredSize(new Dimension( (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),  (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
	backgroundPanel.setLayout(new FlowLayout());
	window.add(backgroundPanel);

	//add players to the window
	ArrayList<JPanel> players = new ArrayList<JPanel>();
	playerScores = new ArrayList<PlayerScores>();

	for(int i = 0; i < numPlayers; i++)
	{
		//name of current player in loop.
		String currName = String.format("Player %d", i+1);
		if(playerNames.size() > i)
			currName = playerNames.get(i);

		//panel for player
		JPanel currPlayer = new JPanel()
		{
			@Override
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);

				 this.setOpaque(false);

				 BufferedImage img;
				try {
					img = ImageIO.read(new File("src/Resources/activePlayer.png"));

					g.drawImage(img, 0, 0, (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), COL_HEIGHT * 3, null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};

		GridBagLayout layout = new GridBagLayout();
		//int [] colWidths = new int[Player.NUM_FRAMES * NUM_ROWS + 1];
		//for(int j = 0; j < Player.NUM_FRAMES * NUM_ROWS + 1; j++)
		//{
		//	colWidths[j] = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / (Player.NUM_FRAMES * NUM_ROWS + 1));
		//}
		//layout.columnWidths = colWidths;
		currPlayer.setLayout(layout);
		//currPlayer.setBackground(Color.BLUE);

		//postion in grid
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = Player.NUM_FRAMES * NUM_ROWS + 1;
		//add player name label
		JLabel playerName = new JLabel(currName, SwingConstants.CENTER);
		playerName.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
		currPlayer.add(playerName, gbc);

		ArrayList<JLabel> shots = new ArrayList<JLabel>();
		ArrayList<JLabel> tots = new ArrayList<JLabel>();

		//add scoreboard: first add the shot cells
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		for(int j = 0; j < Player.NUM_FRAMES * NUM_ROWS + 1; j++)
		{
			JLabel currShot = new JLabel("", SwingConstants.CENTER);
			currShot.setPreferredSize(new Dimension((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / (Player.NUM_FRAMES * NUM_ROWS + 1)), COL_HEIGHT));
			currShot.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
			currPlayer.add(currShot, gbc);
			gbc.gridx++;
			shots.add(currShot);
		}//end inner for

		//add the totals cells
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		for(int j = 0; j < Player.NUM_FRAMES - 1; j++)
		{
			JLabel currTot = new JLabel("", SwingConstants.CENTER);
			currTot.setPreferredSize(new Dimension((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / (Player.NUM_FRAMES * NUM_ROWS + 1)), COL_HEIGHT));
			currTot.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
			currPlayer.add(currTot, gbc);
			gbc.gridx += NUM_ROWS;
			tots.add(currTot);
		}//end inner for
		gbc.gridwidth = 3;
		JLabel currTot = new JLabel("", SwingConstants.CENTER);
		currTot.setPreferredSize(new Dimension((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / (Player.NUM_FRAMES * NUM_ROWS + 1)), COL_HEIGHT));
		currTot.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
		//JLabel test = currTot;
		currPlayer.add(currTot, gbc);
		tots.add(currTot);

		backgroundPanel.add(currPlayer);
		players.add(currPlayer);

		playerScores.add(new PlayerScores(shots, tots));
	}//end for
}//end ScoreBoard() constructor*/
