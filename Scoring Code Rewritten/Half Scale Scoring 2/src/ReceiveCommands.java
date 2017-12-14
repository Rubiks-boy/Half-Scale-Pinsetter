
/**
 * This class sends all commands based on the action
 * you want to perform.
 * @author Adam Walker
 */
public class ReceiveCommands 
{
	/**All commands possible to send/receive*/
	public final static char [] CMD_CODES = {'C', 'S', 'E', 'P', 'Q', 'L', 'M', '0', '1'};
	/**English meanings of all commands*/
	public final static String [] CMD_MEANINGS = {"Cycle", "Scores", "Error", "Pow Set", "Pow Msg", "Light Set", "Light Msg", "Resend", "Verify connection"};

	/**All errors, in english translations*/
	public static final String [] ERROR_CODES = {"Out of range", "Pin loading timeout"};

	/**Maximum times a command will attempt to resend if it fails*/
	public static final int MAX_FAILED_ATTEMPTS = 5;
	/**How many times the command has been sent thus far*/
	private static int numFailedAttempts;
	
	/**
	 * Takes in a command and performs the correct action.
	 * @param String cmd - cmd(s) to interpret
	 */
	public static void receiveCmd(String cmd)
	{
		System.out.print("REC CMD: ");
		
		while(cmd.indexOf(Serial.BEG) != -1 && cmd.indexOf(Serial.TERMINATOR) != -1)
		{
			//chop off the beginning up until a new cmd begins
			cmd = cmd.substring(cmd.indexOf(Serial.BEG) + 1);
			String cmdInner = cmd.substring(0, cmd.indexOf(Serial.TERMINATOR) - 1);
			char hash = cmd.charAt(cmd.indexOf(Serial.TERMINATOR) - 1);
			
			if(Serial.GenHash.verifyCmd(cmdInner, hash))
			{
				matchCmdAction(cmdInner);
			}
		}
	}//end receiveCmd()

	/**
	 * Matches the command to an action
	 * @param String cmd
	 */
	private static void matchCmdAction(String cmd)
	{
		char cmdType = cmd.charAt(0);
		String args = cmd.substring(1);
		
		//find the type of command it is
		int i;
		for(i = 0; i < CMD_CODES.length; i++)
		{
			if(CMD_CODES[i] == cmdType)
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
			MachineState.powMsg(args);
			break;
		case 6: 
			MachineState.lightMsg(args);
			break;
		case 7: 
			Serial.sendLastCmd();
			break;
		case 8:
			verifyConnection();
			break;
		default: 
			System.out.println("UNKNOWN CMD: " + cmd);
			askForResend();
		}//end switch
	}
	
	/**
	 * Decodes and adds a new score to the scoreboard
	 * @param args - which pins are up/down ("1" for up, "0" for down)
	 */
	private static void scores(String args)
	{
		//scoring representation with booleans.
		boolean [] pinScore = new boolean[NormalFrame.NUM_PINS];

		for(int i = 0; i < args.length(); i++)
		{
			if(args.charAt(i) == '1')
			{
				pinScore[i] = true;
			}else pinScore[i] = false;
		}//end for

		System.out.println("SCORE: PINS:" + args.substring(0, NormalFrame.NUM_PINS));

		Serial.scoreRun.addScore(pinScore);
	}//end scores()

	/**
	 * Responds to an error reported from the arduino
	 * @param args - error code
	 */
	private static void error(String args)
	{
		System.out.print("ERROR: ");
		int errorCode = Integer.parseInt(args);
		if(errorCode < ERROR_CODES.length)
			System.out.println(errorCode + " (" + ERROR_CODES[errorCode] + ")");
		else System.out.println();
	}//end error()

	private static class MachineState
	{
		/**
		 * Responds to the power state reported changed by the arduino.
		 * @param args - power state. "1" for on, "0" for off
		 */
		public static void powMsg(String args)  //R
		{
			System.out.print("POWER "+ (args.charAt(0) == '1' ? "On" : "Off"));
		}//end powMsg()

		/**
		 * Responds to the light state reported changed by the arduino.
		 * @param args - light state. "1" for on, "0" for off
		 */
		public static void lightMsg(String args) //R
		{
			System.out.println("LIGHT " + (args.charAt(0) == '1' ? "On" : "Off"));
		}//end lightMsg()
	}//end MachineState class

	/**
	 * Asks for the arduino to resend the last command, if it hasn't been sent too many times already
	 */
	private static void askForResend() //S
	{
		if(++numFailedAttempts < MAX_FAILED_ATTEMPTS)
		{
			System.out.println("SEND CMD: 0: RESEND");
			Serial.sendCmd("0");
		}//end if
		else
		{
			System.out.println("DEBUG: Ask for RESEND 5 times unsuccessful.");
			numFailedAttempts = 0;
		}//end else
	}//end doResend()

	/**
	 * Verifies connection with the arduino by sending a command '1'
	 */
	public static void verifyConnection()
	{
		System.out.println("VERIFY_CON");
		Serial.sendCmd("1");
	}
}
