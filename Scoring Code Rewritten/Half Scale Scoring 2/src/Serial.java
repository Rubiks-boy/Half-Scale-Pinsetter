import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.Scanner;
import com.fazecast.jSerialComm.*;

/**
 * This class does all communications with an Arduino,
 * including opening up the serial port and sending formatted commands.
 * 
 * @author Adam Walker
 */
public class Serial 
{
	/**Beginning character of all commands*/
	public static final char BEG = '}';
	/**Ending character of all commands*/
	public static final char TERMINATOR = '~';

	/**Serial port arduino is on*/
	private  static SerialPort arduinoPort;
	/**dropdown for user to choose the desired port*/
	private static JComboBox<String> comboPorts;
	/**Commands to send to the Arduino - output stream*/
	private static PrintWriter write;
	/**List of ports of the arduino*/
	private static SerialPort [] serialPorts;
	/**Where the command is typed in*/
	private static JTextField cmdBox;

	/**Previous command, in the event of being asked for a resend*/
	private static String lastCmd;
	
	/**Run scoring - players / scoreboard*/
	public static RunScoring scoreRun;

	public static class SerialSetup
	{
		/**
		 * Sets up serial data to run. Waits until a
		 * connection is established to call other methods.
		 */
		public static void performSerialSetup()
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

					//Additional delay just in case
					try
					{
						sleep(100);
					}catch(Exception e){}

					setupAfterEstConnection();
				}//end run()
			};//end Thread setupSerialData

			setupSerialData.run();
		}//end performSerialSetup()

		/**
		 * Calls everything else once the port is connected.
		 */
		private static void setupAfterEstConnection()
		{
			scoreRun = new RunScoring(2);
			makeCustomCmdWindow();
			setupWriteSerial();
					
			Thread read = new Thread() {
				@Override
				public void run()
				{
					Serial.readSerial();
				}
			};
			read.start();
		}//end setupAfterEstConnection()

		/**
		 * Sets up the ability to send commands to the arduino
		 * Pre - port with arduino opened
		 */
		private static void setupWriteSerial()
		{
			if(arduinoPort.isOpen())
			{
				write = new PrintWriter(arduinoPort.getOutputStream());
			}//end if
		}//end setupWriteSerial()
	}//end SerialSetup class

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
					SerialSetup.performSerialSetup();
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
	 * Reads all serial data sent from the Arduino, 
	 * interprets cmds, prints to console.
	 */
	public static void readSerial()
	{
		Scanner serialData = new Scanner(arduinoPort.getInputStream());

		while(serialData.hasNext())
		{
			try
			{
				//get the next string until white space
				String cmdRaw = serialData.next();
				System.out.println("ARDUINO (RAW CMD): " + cmdRaw);

				ReceiveCommands.receiveCmd(cmdRaw);
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
	 * Prompts to send custom commands
	 * (Will not be used later - for testing only)
	 */
	public static void makeCustomCmdWindow()
	{
		//Set up JFrame window
		JFrame cmdWindow = new JFrame("Custom Command Send");

		cmdWindow.setLayout(new FlowLayout());
		cmdWindow.setSize(300, 100);
		cmdWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		cmdBox = new JTextField(10);
		JButton sendCmd = new JButton("Send Cmd");
		sendCmd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Serial.sendCustomCmd();
			}
		});

		//add them to the window. display the window
		cmdWindow.add(cmdBox);
		cmdWindow.add(sendCmd);
		cmdWindow.setVisible(true);
	}//end promptGrippers()

	/**
	 * Send the command in the text box to the arduino
	 * (For testing only)
	 * Pre - port must be opened
	 */
	public static void sendCustomCmd()
	{
		sendCmd(cmdBox.getText());
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
			System.out.println("SEND CMD: " + cmd);
			write.print(BEG + cmd + GenHash.genHash(cmd) + TERMINATOR);
			write.flush();
			lastCmd = cmd;
		}//end if
	}//end sendCmd()

	/**
	 * Resends the last command.
	 */
	public static void sendLastCmd()
	{
		sendCmd(lastCmd);
	}//end sendLastCmd()

	public static class GenHash
	{
		/**First ascii character possible for the hash code*/
		public static final char FIRST_HASH = '!';
		/**Last ascii character possible for the hash code*/
		public static final char LAST_HASH = '|';
		/**Prime number used for the hash code*/
		private static final int HASH_PRIME = 89;

		/**
		 * Generates the hash code for the command.
		 * @param cmd - to generate hash of (neglect beginning/terminating character)
		 * @return char with hash code
		 */
		public static char genHash(String cmd)
		{
			int totalAscii = 1;
			for(int i = 0; i < cmd.length(); i++)
			{
				totalAscii += ((int)cmd.charAt(i) * (i+1));
			}//end for

			return (char)(totalAscii % HASH_PRIME + FIRST_HASH);
		}//end genHash()

		/**
		 * Verifies the command with the hash code that should exist
		 * @param cmd - command to verify
		 * @return true if command matches hash code
		 */
		public static boolean verifyCmd(String cmd, char cmd_hash)
		{	
			if(cmd_hash != genHash(cmd)) 
				System.out.println("DEBUG: CMD_FAIL_VERIFICATION " + cmd);

			return (cmd_hash == genHash(cmd));
		}//end verifyCmd()
	}
}
