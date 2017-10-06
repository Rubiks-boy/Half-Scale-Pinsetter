/**
 * This class decrypts, interprets, and prepares commands to send or that were received.
 * 
 * @author Adam Walker P5
 */
public class CommsCommands
{
	/*
	 * ALL COMMANDS ARE IN THE FOLLOWING FORMAT:
	 * 
	 * [BEG(1)][CMD][HASH(1)][TERM(1)]
	 * where (n) signifies size n bytes
	 */

	/**All errors, in english translations*/
	public static final String [] ERROR_CODES = {"Out of range", "Pin loading timeout"};
	//0 = fatal. 1 = semi-fatal. 2 = non-fatal
	/**The fatality level of errors*/
	public static final int [] ERROR_FATALITY = {1, 0};
	/**Beginning character of all commands*/
	public static final char BEG = '}';
	/**Ending character of all commands*/
	public static final char TERMINATOR = '~';
	/**Location of the character of the command type*/
	public static final int TYPE_LOC = 1;
	/**Location that argument characters start*/
	public static final int ARG_LOC = 2;
	/**Maximum times a command will attempt to resend if it fails*/
	public static final int MAX_FAILED_ATTEMPTS = 5;
	/**Location (from the right) of the hash code location*/
	public static final int HASH_LOC = 2;
	/**First ascii character possible for the hash code*/
	public static final char FIRST_HASH = '!';
	/**Last ascii character possible for the hash code*/
	public static final char LAST_HASH = '|';
	/**Prime number used for the hash code*/
	private static final int HASH_PRIME = 89;

	/**Previous command, in the event of being asked for a resend*/
	private static String lastCmd;
	/**How many times the command has been sent thus far*/
	private static int numFailedAttempts;
	
	/**All commands possible to send/receive*/
	public final static char [] CMD_CODES = {'C', 'S', 'E', 'P', 'Q', 'D', 'L', 'M', '0'};
	/**English meanings of all commands*/
	public final static String [] CMD_MEANINGS = {"Cycle", "Scores", "Error", "Pow Set", "Pow Msg", "Cycle Done", "Light Set", "Light Msg", "Resend"};

	/**"Constructor" - initializes static variables*/
	static
	{
		numFailedAttempts = 0;
		lastCmd = new String();
	}//end CommsCommands() static "constructor"

	/**
	 * Interprets the command and calls the corresponding action 
	 * associated with the command.
	 * 
	 * @param String cmd - Cmd to interpret
	 */
	public static void interpretCommand(String cmd)
	{
		System.out.println("Command: " + cmd);
		
		if(verifyCmd(cmd))
		{
			//find the type of command it is
			int i;
			for(i = 0; i < CMD_CODES.length; i++)
			{
				if(CMD_CODES[i] == cmd.charAt(TYPE_LOC))
					break;
			}//end for

			String args = cmd.substring(ARG_LOC, cmd.length() - HASH_LOC);
			
			//run the corresponding action
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
				doLastCmd();
				break;
			default: 
				System.out.println("Could not interpret command: " + i + " " + cmd);
				doResend();
			}//end switch
		}
		else
		{
			//command was unsuccessful
			System.out.println("Command was unsuccessful");
			doResend();
		}
	}//end interpretCommand()

	/**
	 * Verifies the command with the hash code that should exist
	 * @param cmd - command to verify
	 * @return true if command matches hash code
	 */
	private static boolean verifyCmd(String cmd) //R
	{
		char hash = cmd.charAt(cmd.length() - HASH_LOC);
		String cmdInner = cmd.substring(1, cmd.length() - HASH_LOC);
		if(hash != genHash(cmdInner)) System.out.println("Verificationfailed");
		return (hash == genHash(cmdInner));
	}//end verifyCmd()
	
	/**
	 * Asks for the arduino to resend the last command, if it hasn't been sent too many times already
	 */
	private static void doResend() //S
	{
		if(++numFailedAttempts < MAX_FAILED_ATTEMPTS)
		{
			System.out.println("Command sent was bad. Asking for resend...");
			Cycle.sendCmd(BEG + "0" + genHash("0") + TERMINATOR);
		}//end if
		else
		{
			System.out.println("Command sent " + MAX_FAILED_ATTEMPTS + " times incorrectly.");
			numFailedAttempts = 0;
		}//end else
	}//end doResend()

	/**
	 * Responds to the light state reported changed by the arduino.
	 * @param args - light state. "1" for on, "0" for off
	 */
	private static void lightMsg(String args) //R
	{
		System.out.println("Light state is now: " + (args.charAt(0) == '1' ? "ON" : "OFF"));
	}//end lightMsg()

	/**
	 * Responds to the cycle being reported finished by the arduino
	 * @param args - cycle type, with pins if applicable
	 */
	private static void cycleDone(String args) //R
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
		}//end switch
		System.out.println(" is completed.");
	}//end cycleDone()

	/**
	 * Responds to the power state reported changed by the arduino.
	 * @param args - power state. "1" for on, "0" for off
	 */
	private static void powMsg(String args)  //R
	{
		System.out.print("Power state changed to: ");
		System.out.println(args.charAt(0) == '1' ? "ON" : "OFF");
	}//end powMsg()

	/**
	 * Responds to an error reported from the arduino
	 * @param args - error code
	 */
	private static void error(String args) //R
	{
		System.out.print("Error reported: ");
		int errorCode = Integer.parseInt(args);
		if(errorCode < ERROR_CODES.length)
			System.out.println(errorCode + " (" + ERROR_CODES[errorCode] + ")");
		if(ERROR_FATALITY[errorCode] == 0)
			System.out.println("Error fatal. Machine had to power off.");
		else if(ERROR_FATALITY[errorCode] == 1)
			System.out.println("Error not fatal. Bowling pauses but machine remains on.");
		else System.out.println("Error not fatal. Bowling can continue.");
	}//end error()

	/**
	 * Decodes and adds a new score to the scoreboard
	 * @param args - which pins are up/down ("1" for up, "0" for down)
	 */
	private static void scores(String args)//R
	{
		//scoring representation with booleans.
		boolean [] pinScore = new boolean[BowlingFrame.MAX_SCORE];

		for(int i = 0; i < args.length(); i++)
		{
			if(args.charAt(i) == '1')
			{
				pinScore[i] = true;
			}else pinScore[i] = false;
		}//end for

		System.out.println("Adding score");
		System.out.println("Pins: " + args.substring(0, BowlingFrame.MAX_SCORE));

		//Add to scoreboard
		Cycle.addScore(pinScore);
	}//end scores()

	///SEND COMMANDS
	/**
	 * Tells the arduino to perform a reset cycle.
	 */
	public static void doResetCycle()//S
	{
		String cmd = "C0";
		Cycle.sendCmd(BEG + cmd + genHash(cmd) + TERMINATOR);
		lastCmd = cmd;
	}//end doResetCycle()

	/**
	 * Tells the arduino to perform a selective reset cycle.
	 * @param pins - "1" if pin should be set, "0" if not
	 */
	public static void doResetCycle(boolean[] pins)//S
	{
		String pinStr = new String();
		for(boolean pin : pins)
		{
			if(pin)
				pinStr += "1";
			else pinStr += "0";
		}//end for

		String cmd = "C3" + pinStr;
		Cycle.sendCmd(BEG + cmd + genHash(cmd) + TERMINATOR);
		lastCmd = cmd;
	}//end doResetCycle()

	/**
	 * Tells the arduino to perform a respot cycle.
	 */
	public static void doRespotCycle()//S
	{
		String cmd = "C1";
		Cycle.sendCmd(BEG + cmd + genHash(cmd) + TERMINATOR);
		lastCmd = cmd;
	}//end doRespotCycle()

	/**Tells the arduino to perform a gutter/short cycle (no pins knocked over)*/
	public static void doGutterCycle()//S
	{
		String cmd = "C2";
		Cycle.sendCmd(BEG + cmd + genHash(cmd) + TERMINATOR);
		lastCmd = cmd;
	}//end doGutterCycle()

	/**
	 * Tells the arduino to change the pinsetter power state.
	 * @param args - power state. "1" for on, "0" for off
	 */
	public static void changePowState(boolean state)//S
	{
		String cmd = "P" + (state ? "1" : "0");
		Cycle.sendCmd(BEG + cmd + genHash(cmd) + TERMINATOR);
		lastCmd = cmd;
	}//end changePowState()

	/**
	 * Tells the arduino to change the pinsetter light state.
	 * @param args - light state. "1" for on, "0" for off
	 */
	public static void changeLightState(boolean state) //S
	{
		String cmd = "L" + (state ? "1" : "0");
		Cycle.sendCmd(BEG + cmd + genHash(cmd) + TERMINATOR);
		lastCmd = cmd;
	}//end changeLightState()

	/**
	 * Resends the previous command.
	 */
	public static void doLastCmd()
	{
		System.out.println("Sending command: " + lastCmd);
		Cycle.sendCmd(BEG + lastCmd + genHash(lastCmd) + TERMINATOR);
	}//end doLastCmd()
	
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
			//System.out.println("During for loop ascii: " + totalAscii);
		}//end for

		//System.out.println("Totalascii(pc):" + totalAscii);
		//System.out.println((char)(totalAscii % HASH_PRIME + FIRST_HASH));
		return (char)(totalAscii % HASH_PRIME + FIRST_HASH);
	}//end genHash()
	
	/**
	 * Sends a custom command that does not fit into a category above.
	 * (Experimental)
	 * @param cmd - to send, neglecting hash, beg, or terminating characters
	 */
	public static void customCmdSend(String cmd)
	{
		lastCmd = cmd;
		System.out.println("Sending command: " + cmd);
		Cycle.sendCmd(BEG + cmd + genHash(cmd) + TERMINATOR);
	}//end customCmdSend
}//end class CommsCommands
