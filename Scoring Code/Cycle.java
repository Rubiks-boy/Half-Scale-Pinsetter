//GUI Imports:
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import com.fazecast.jSerialComm.*;

/**
 * This class does all communications with an Arduino,
 * including opening up the serial port and sending commands.
 * 
 * @author Adam Walker P5
 */
public class Cycle
{
	/**Serial port arduino is on*/
	private  static SerialPort arduinoPort;
	/**dropdown for user to choose the desired port*/
	private static JComboBox<String> comboPorts;
	/**Commands to sned to the Arduino - output stream*/
	private static PrintWriter write;
	/**List of ports of the arduino*/
	private static SerialPort [] serialPorts;
	/**Where the command is typed in*/
	private static JTextField cmdBox;
	/**Scoring system*/
	private static RunScoring scoring;
	/**Player names on settings window*/
	private static JTextField [] playerNames;
	/**Maximum number of players supported*/
	public static final int MAX_PLAYERS = 6;
	/**Window to ask for names*/
	private static JFrame settingsWindow;

	/**
	 * Sets up the scoreboard.
	 * 
	 * @param int numPlayers - how many in the game
	 */
	public static void setupScoring(int numPlayers)
	{
		scoring = new RunScoring(numPlayers);
	}//end setupScoring()

	/**
	 * Sets up the scoreboard with 1 player.
	 */
	public static void setupScoring()
	{
		scoring = new RunScoring();
	}//end setupScoring()

	/**
	 * Gets the player names and sets up the scoreboard
	 */
	/*public static void askSettings()
	{
		//make frame
		settingsWindow = new JFrame("Game Setup");
		settingsWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		settingsWindow.setLayout(null);
		settingsWindow.setSize(215, 315);

		//add player names boxes
		playerNames = new JTextField[MAX_PLAYERS];
		for(int i = 0; i < MAX_PLAYERS; i++)
		{
			String lblTitle = "Player " + (i+1) + ": ";
			JLabel playerHeader = new JLabel(lblTitle);
			playerHeader.setBounds(15, i*30 + 15, 75, 20);
			playerNames[i] = new JTextField();
			playerNames[i].setBounds(85, i*30 + 15, 100, 20);
			settingsWindow.add(playerHeader);
			settingsWindow.add(playerNames[i]);
		}//end for

		//add a confirm box that will begin scoring
		JButton accept = new JButton("Begin Game");
		accept.setBounds(0, 260, 215, 25);
		accept.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int numPlayers = 0;
				ArrayList<String> players = new ArrayList<String>();
				for(int i = 0; i < MAX_PLAYERS; i++)
				{
					if(!playerNames[i].getText().equals(""))
					{
						players.add(playerNames[i].getText());
						numPlayers++;
					}//end if
				}//end for
				scoring = new RunScoring(numPlayers, players);
				settingsWindow.setVisible(false);
			}//end actionPerformed()
		});
		settingsWindow.add(accept);
		
		settingsWindow.setVisible(true);
	}//end askSettings()*/

	/**
	 * Tells RunScoring to get the player names and set up scoreboard
	 */
	public static void askSettings()
	{
		scoring = new RunScoring();
		
	}//end askSettings()
	
	/**
	 * Sets up serial data to run. Waits until a
	 * connection is established.
	 */
	public static void setupSerialData()
	{
		Thread setupSerialData = new Thread() 
		{
			@Override
			public void run()
			{
				try
				{
					//wait for port to open
					while(arduinoPort == null || !arduinoPort.isOpen())
					{
						sleep(100);
					}
				}catch(Exception e){}
				try
				{
					sleep(100);
				}catch(Exception e){}
				System.out.println("no longer sleeping");
				Cycle.setupWriteSerial();
				Cycle.promptGrippers();
				//Cycle.printSerial();
				Cycle.askSettings();
				Cycle.readSerial();
			}//end run()
		};//end Thread setupSerialData

		setupSerialData.run();
	}//end setupSerialData()

	/**
	 * Prompts the user to choose a port.
	 * Pre - Arduino connected (though port list can be refreshed)
	 * 
	 * pre - setupSerialData() and setupScoring() must have run
	 */
	public static void startConnection()
	{
		//get current serial ports
		serialPorts = SerialPort.getCommPorts();

		//make JFrame to hold combobox and buttons
		JFrame portChooser = new JFrame("Choose Serial Port");
		portChooser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		portChooser.setLayout(new FlowLayout());
		portChooser.setSize(300, 100);

		//Make combobox to hold all the serial ports
		comboPorts = new JComboBox<String>();
		for(SerialPort currSP : serialPorts)
			comboPorts.addItem(currSP.getSystemPortName());
		comboPorts.setSelectedIndex(0);

		//Make button to use user's selection
		JButton btnChoose = new JButton("Open Port");
		btnChoose.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				arduinoPort = serialPorts[comboPorts.getSelectedIndex()];
				System.out.println(arduinoPort);

				portChooser.dispose();

				//try to open serial port
				if(arduinoPort.openPort())
				{
					System.out.println("Port Opened");
					arduinoPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
				}//end if
				else 
				{
					//try opening the port again.
					//open another port chooser
					System.out.println("Port could not be opened.");
					startConnection();
				}//end else
			}//end actionPerformed()
		});//end ActionListener implementation

		//make button to refresh all the serial ports available
		JButton btnRefreshPorts = new JButton("Refresh");
		btnRefreshPorts.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				comboPorts.removeAllItems();

				serialPorts = SerialPort.getCommPorts();
				for(SerialPort currSerial : serialPorts)
					comboPorts.addItem(currSerial.getSystemPortName());
			}//end actionPerformed()
		});//end ActionListener implementation

		//add the buttons and combobox to the screen
		portChooser.add(comboPorts);
		portChooser.add(btnChoose);
		portChooser.add(btnRefreshPorts);

		//make window visible
		portChooser.setVisible(true);
	}//end startConnection()

	/**
	 * (Placeholder) Will send command for arduino to set up 10 new pins.
	 */
	public static void newSet() 
	{
		System.out.println("DEBUG: Setting 10 new pins");
	}//end newSet()

	/**
	 * (Placeholder) Will send command for arduino to pick up pins, clear
	 * knocked over pins, and set down remaining pins.
	 */
	public static void standingPins() 
	{
		System.out.println("DEBUG: Respotting pins");
	}//end standingPins()

	/**
	 * Prints out any serial information send from the arduino.
	 * Post - prints to console.
	 */
	public static void printSerial()
	{	
		System.out.println("Serial Printing");
		Scanner serialData = new Scanner(arduinoPort.getInputStream());
		while(serialData.hasNextLine())
		{
			System.out.println("ARDUINO: " + serialData.nextLine());
		}//end while
		serialData.close();
	}//end printSerial()

	/**
	 * Reads all serial data sent from the Arduino
	 * 
	 * Post - prints data to console
	 */
	public static void readSerial()
	{
		Scanner serialData = new Scanner(arduinoPort.getInputStream());

		System.out.println("Begin Serial Read");
		while(serialData.hasNext())
		{
			System.out.println("Begin Serial Read");
			try
			{
				//get the next string until white space
				String cmdRaw = serialData.next();
				System.out.println("ARDUINO (RAW CMD): " + cmdRaw);

				//remove everything before ` and after ~
				int begLoc = cmdRaw.indexOf(CommsCommands.BEG);
				String cmd = cmdRaw.substring(begLoc, cmdRaw.indexOf(CommsCommands.TERMINATOR, begLoc) + 1);

				CommsCommands.interpretCommand(cmd);
			}catch(Exception e)
			{
				System.out.println(e.getMessage());
			}//end catch
		}//end while

		serialData.close();
	}//end readSerial()

	/**
	 * Whether connection has been made
	 * @return true if arduino is currently connected
	 */
	public static boolean portIsOpen() {return arduinoPort.isOpen();}

	/**
	 * Sets up the ability to send commands to the arduino
	 * Pre - port with arduino opened
	 */
	public static void setupWriteSerial()
	{
		if(arduinoPort.isOpen())
		{
			write = new PrintWriter(arduinoPort.getOutputStream());
		}//end if
	}//end setupWriteSerial()

	/**
	 * Prompts to open and close grippers
	 * (Will not be used later - for testing only)
	 */
	public static void promptGrippers()
	{
		//Set up JFrame window
		System.out.println("prompt grippers");
		JFrame gripperWindow = new JFrame("Control Grippers");

		gripperWindow.setLayout(new FlowLayout());
		gripperWindow.setSize(300, 100);
		gripperWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//add a close and open button that call openGrippers() and closeGrippers()
		JButton closeBtn = new JButton("Close");
		closeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Cycle.closeGrippers();
			}//end actionPerformed()
		});//end ActionListener implementation
		JButton openBtn = new JButton("Open");
		openBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Cycle.openGrippers();
			}//end actionPerformed()
		});//end ActionListener implementation
		cmdBox = new JTextField(10);
		JButton sendCmd = new JButton("Send Cmd");
		sendCmd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Cycle.sendCmd();
			}
		});

		//add them to the window. display the window
		gripperWindow.add(openBtn);
		gripperWindow.add(closeBtn);
		gripperWindow.add(cmdBox);
		gripperWindow.add(sendCmd);
		gripperWindow.setVisible(true);
	}//end promptGrippers()

	/**
	 * Sends command for arduino to open grippers
	 * (For testing only)
	 * Pre - port must be opened
	 * Post - grippers on arduino side opened
	 */
	public static void openGrippers()
	{
		if(portIsOpen())
		{
			System.out.println("Sending command: C0");
			CommsCommands.customCmdSend("C0");
			write.flush();
		}//end if
	}//end openGrippers()

	/**
	 * Sends command for arduino to close grippers
	 * (For testing only)
	 * Pre - port must be opened
	 * Post - grippers on arduino side opened
	 */
	public static void closeGrippers()
	{
		if(portIsOpen())
		{
			System.out.println("Sending command: C4");
			CommsCommands.customCmdSend("C4");
			write.flush();
		}//end if
	}//end closeGrippers()

	/**
	 * Send the command in the text box to the arduino
	 * (For testing only)
	 * Pre - port must be opened
	 */
	public static void sendCmd()
	{
		if(portIsOpen())
		{
			String cmd = cmdBox.getText();
			CommsCommands.customCmdSend(cmd);
		}//end if
	}//end sendCmd()

	/**
	 * Send the command passed to the arduino
	 * 
	 * Pre - port must be opened
	 * @param String cmd
	 */
	public static void sendCmd(String cmd)
	{
		if(portIsOpen())
		{
			write.print(cmd);
			write.flush();
		}//end if
	}//end sendCmd()

	/**
	 * Adds the score to the scoreboard.
	 * @param score - with which pins are up/down
	 */
	public static void addScore(boolean [] score)
	{
		scoring.addScore(score);
	}//end addScore()
}//end class Cycle