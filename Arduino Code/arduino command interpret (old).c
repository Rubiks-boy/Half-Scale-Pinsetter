#include <Servo.h> 
 
  const int GRIPPER_TEMP_OPEN_POS = 105;
 const int GRIPPER_OPEN_POS = 95;
  const int GRIPPER_TEMP_CLOSE_POS = 0;
 const int GRIPPER_CLOSE_POS = 5;
 const int POW_PIN = 13;
 const int LIGHT_PIN = 12;
 const int NUM_PINS = 1;
 const int SERVO_PINS[] = {8};
 Servo gripperServo[NUM_PINS];

	/*
	 * ALL COMMANDS ARE IN THE FOLLOWING FORMAT:
	 * 
	 * [BEG(1)][CMD][HASH(1)][TERM(1)]
	 * where (n) signifies size n bytes
	 */

	 const String ERROR_CODES[] = {"Out of range", "Pin loading timeout"};
	//0 = fatal. 1 = semi-fatal. 2 = non-fatal
	 const int ERROR_FATALITY[] = {1, 0};
	 const char BEG = '}';
	 const char TERMINATOR = '~';
	 const int TYPE_LOC = 1;
	 const int ARG_LOC = 2;
	 const int MAX_FAILED_ATTEMPTS = 5;
	 const int HASH_LOC = 2;
	 const char FIRST_HASH = '!';
	 const char LAST_HASH = '|';

	  String lastCmd;
	  int numFailedAttempts;
	
const int NUM_CMDS = 9;
	 const char CMD_CODES[] = {'C', 'B', 'E', 'P', 'Q', 'D', 'L', 'M', '0'};
	 const String CMD_MEANINGS[] = {"Cycle", "Scores", "Error", "Pow Set", "Pow Msg", "Cycle Done", "Light Set", "Light Msg", "Resend"};

	/**
	 * Interprets the command and calls the corresponding action 
	 * associated with the command.
	 * 
	 * @param String cmd - Cmd to interpret
	 */
	 void interpretCommand(String cmd)
	{
		Serial.print("Command: ");
Serial.println(cmd);
		
		if(verifyCmd(cmd))
		{
			//find the type of command it is
			int i;
			for(i = 0; i < NUM_CMDS; i++)
			{
				if(CMD_CODES[i] == cmd.charAt(TYPE_LOC))
					break;
			}//end for

			String args = cmd.substring(ARG_LOC, cmd.length() - HASH_LOC);
			
			//run the corresponding action
			switch(i)
			{
			case 0:
			performCycle(args);
			break;
			case 3:
			break;
			case 6:
			changeLight(args);
			break;
			case 8: 
				doLastCmd();
				break;
			default: 
				Serial.print("Could not interpret command: ");
				Serial.println(cmd);
				doResend();
			}//end switch
		}
		else
		{
			//command was unsuccessful
			doResend();
		}
	}//end interpretCommand()

	  boolean verifyCmd(String cmd) //R
	{
		char hash = cmd.charAt(cmd.length() - HASH_LOC);
		String cmdInner = cmd.substring(1, cmd.length() - 1 - HASH_LOC);
		
		return (hash == genHash(cmdInner));
	}//end verifyCmd()
	
	  void doResend() //S
	{
		if(++numFailedAttempts < MAX_FAILED_ATTEMPTS)
		{
			Serial.println("Command sent was bad. Asking for resend.");
			sendCmd(BEG + "0" + TERMINATOR);
		}
		else
		{
			Serial.print("Command sent ");
Serial.print(MAX_FAILED_ATTEMPTS);
Serial.println(" times incorrectly.");
		}
	}

	  void changeLight(String args) //R
	{
		Serial.print("Light state is now: ");
  if(args.charAt(0) == 1))
  {
  	digitalWrite(LIGHT_PIN, HIGH);
  	String cmd = BEG + "M1" + genHash("M1") + TERMINATOR;
  }
  else
  {
	digitalWrite(LIGHT_PIN, LOW);
  	String cmd = BEG + "M0" + genHash("M0") + TERMINATOR;
  	}
	}

	  void performCycle(String args) //R
	{
		Serial.print("Cycle ");
		int cycleType = parseInt(args.charAt(0));
		boolean pins[MAX_PINS];
		for(int i = 1; i < args.length(); i++)
		{
			if(args.charAt(i) == "1")
				pins[i] = true;
			else pins[i] = false;
		}
		
		switch(cycleType)
		{
		case 0:
			Serial.println("Reset");
			performResetCycle();
			break;
		case 1:
			Serial.println("Respot");
			performRespotCycle();
			break;
		case 2:
			Serial.println("Gutter");
			performGutterCycle();
			break;
		case 3:
			Serial.println("Selective Reset");
			performSelectiveResetCycle();
			break;
		default:
			Serial.println("[Unknown cycle type]");
		}
	}

	  void changePow(String args)  //R
	{
  if(args.charAt(0) == 1))
  {
  	digitalWrite(POW_PIN, HIGH);
  	String cmd = BEG + "Q1" + genHash("Q1") + TERMINATOR;
  }
  else
  {
	digitalWrite(POW_PIN, LOW);
  	String cmd = BEG + "Q0" + genHash("Q0") + TERMINATOR;
  	}
	}

	  void reportError(String error) //R
	{
		String cmd = "E" + error;
		sendCmd(BEG + cmd + genHash(cmd) + TERMINATOR);
	}

	  void sendScore(boolean pins[])//R
	{
		String cmd = "S";
		
		for(int i = 0; i < NUM_PINS; i++)
		{
			if(pins[i])
				cmd += "1";
			else cmd += "0";
		}
		
		sendCmd(BEG + cmd + genHash(cmd) + TERMINATOR);
	}

	 void performResetCycle()//S
	{
		
	}//end performResetCycle()

	 void performSelectiveResetCycle(String args)//S
	{
		
	}//end performSelectiveResetCycle()

	 void doRespotCycle()//S
	{
		String cmd = "C1";
		sendCmd(BEG + cmd + genHash(cmd) + TERMINATOR);
		lastCmd = cmd;
	}//end doRespotCycle()

	 void doGutterCycle()//S
	{
		String cmd = "C2";
		sendCmd(BEG + cmd + genHash(cmd) + TERMINATOR);
		lastCmd = cmd;
	}//end doGutterCycle()

	 void changePowState(boolean state)//S
	{
		String cmd = "P" + (state ? "1" : "0");
		sendCmd(BEG + cmd + genHash(cmd) + TERMINATOR);
		lastCmd = cmd;
	}//end changePowState()

	 void changeLightState(boolean state) //S
	{
		String cmd = "L" + (state ? "1" : "0");
		sendCmd(BEG + cmd + genHash(cmd) + TERMINATOR);
		lastCmd = cmd;
	}//end changeLightState()

	 void doLastCmd()
	{
		sendCmd(BEG + lastCmd + genHash(lastCmd) + TERMINATOR);
	}//end doLastCmd()
	
char genHash(String cmd)
	{
		int totalAscii = 0;
		for(int i = 0; i < cmd.length(); i++)
		{
			totalAscii += (int)cmd.charAt(i);
		}//end for
		
		return (char)((char)(totalAscii % (int)(LAST_HASH - FIRST_HASH)) + FIRST_HASH);
	}//end genHash()


void sendCmd(String cmd)
{
 Serial.write(BEG + cmd + genHash(cmd) + TERMINATOR); 
}
 
 void openGrippers(boolean openPin[])
 {
   for(int i = 0; i < NUM_PINS; i++)
   {
     if(openPin[i])
       gripperServo[i].write(GRIPPER_TEMP_OPEN_POS);
   }
        delay(250);
      for(int i = 0; i < NUM_PINS; i++)
   {
     if(openPin[i])
       gripperServo[i].write(GRIPPER_OPEN_POS);
   }
      Serial.write("Open Grippers\n");
 }
 
 void openGrippers()
 {
   for(int i = 0; i < NUM_PINS; i++)
   {
       gripperServo[i].write(GRIPPER_TEMP_OPEN_POS);
   }
        delay(250);
      for(int i = 0; i < NUM_PINS; i++)
   {
       gripperServo[i].write(GRIPPER_OPEN_POS);
   }
      Serial.write("Open Grippers\n");
 }
 
 void closeGrippers(boolean closePin[])
 {
   for(int i = 0; i < NUM_PINS; i++)
   {
     if(closePin[i])
       gripperServo[i].write(GRIPPER_TEMP_CLOSE_POS);
   }
        delay(250);
      for(int i = 0; i < NUM_PINS; i++)
   {
     if(closePin[i])
       gripperServo[i].write(GRIPPER_CLOSE_POS);
   }
        Serial.write("Close Grippers\n"); 
 }
 
  void closeGrippers()
 {
   for(int i = 0; i < NUM_PINS; i++)
   {
       gripperServo[i].write(GRIPPER_TEMP_CLOSE_POS);
   }
        delay(250);
      for(int i = 0; i < NUM_PINS; i++)
   {
       gripperServo[i].write(GRIPPER_CLOSE_POS);
   }
        Serial.write("Close Grippers\n"); 
 }
 
void setup() 
{ 
  numFailedAttempts = 0;
		lastCmd = new String();

  for(int i = 0; i < NUM_PINS; i++)
  {
    pinMode(SERVO_PINS[i], OUTPUT);
      gripperServo[i].attach(SERVO_PINS[i]);
  }
  
  pinMode(POW_IND_PIN, OUTPUT);
  pinMode(LIGHT_PIN, OUTPUT);
  
    Serial.begin(9600);
  Serial.setTimeout(25);
  
  closeGrippers();
} 
 
void loop() 
{ 
  if(Serial.available() > 0)
  {
   String command = Serial.readString(); 
   
   if(command == "OG")
     openGrippers();
   else if(command == "CG")
     closeGrippers();
   else Serial.println("Command not recognized: " + command);
  }
  delay(10);
} 
