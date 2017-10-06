#include <Servo.h> 

const int POW_PIN = 13;
const int LIGHT_PIN = 12;
const int NUM_PINS = 10;
const int SERVO_PINS[] = {22, 23, 24, 25, 26, 27, 28, 29, 30, 31};
const int SCORING_PINS[] = {32, 33, 34, 35, 36, 37, 38, 39, 40, 41};

const int GRIPPER_TEMP_OPEN_POS = 105;
const int GRIPPER_OPEN_POS = 95;
const int GRIPPER_TEMP_CLOSE_POS = 0;
const int GRIPPER_CLOSE_POS = 5;

const String ERROR_CODES[] = {"Out of range", "Pin loading timeout"};
//0 = fatal. 1 = semi-fatal. 2 = non-fatal
const int ERROR_FATALITY[] = {1, 0};

const int HASH_PRIME = 89;
const String BEG = "}";
const String TERMINATOR = "~";
const int TYPE_LOC = 1;
const int ARG_LOC = 2;
const int MAX_FAILED_ATTEMPTS = 5;
const int HASH_LOC = 2;
const char FIRST_HASH = '!';
const char LAST_HASH = '|';

String lastCmd;
int numFailedAttempts;
Servo gripperServo[NUM_PINS];
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
	Serial.print("Command:");
	Serial.println(cmd.substring(1));

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
			changePow(args);
			break;
		case 6:
			changeLight(args);
			break;
		case 8: 
			doLastCmd();
			break;
		default: 
			Serial.print("Couldnotinterpretcommand:");
			Serial.println(cmd.substring(1));
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
	String cmdInner = cmd.substring(1, cmd.length() - HASH_LOC);
	Serial.println(cmdInner);
	Serial.println((int)hash);
	Serial.println((int)genHash(cmdInner));
	if(hash != genHash(cmdInner)) Serial.println("Verificationfailed");
	return (hash == genHash(cmdInner));
}//end verifyCmd()

void doResend() //S
{
	if(++numFailedAttempts < MAX_FAILED_ATTEMPTS)
	{
		Serial.println("Commandsentwasbad.Askingforresend.");
		sendCmd("0");
	}
	else
	{
		Serial.print("Commandsent");
		Serial.print(MAX_FAILED_ATTEMPTS);
		Serial.println("timesincorrectly.");
		numFailedAttempts = 0;
	}
}

void changeLight(String args) //R
{
	Serial.print("Lightstateisnow: ");
	if(args.charAt(0) == '1')
	{
		digitalWrite(LIGHT_PIN, HIGH);
		sendCmd("M1");
	}
	else
	{
		digitalWrite(LIGHT_PIN, LOW);
		sendCmd("M0");
	}
}

void performCycle(String args) //R
{
	Serial.print("Cycle");
	char cycleType = args.charAt(0);
	Serial.println(cycleType);

	switch(cycleType)
	{
	case '0':
		performResetCycle();
		break;
	case '1':
		performRespotCycle();
		break;
	case '2':
		performGutterCycle();
		break;
	case '3':
		boolean pins[NUM_PINS];
		for(int i = 1; i < args.length(); i++)
		{
			if(args.charAt(i) == '1')
				pins[i-1] = true;
			else pins[i-1] = false;
		}
		performSelectiveResetCycle(pins);
		break;
	default:
		Serial.println("[Unknowncycletype]");
	}
}

void changePow(String args)  //R
{
	if(args.charAt(0) == '1')
	{
		digitalWrite(POW_PIN, HIGH);
		sendCmd("Q1");
	}
	else
	{
		digitalWrite(POW_PIN, LOW);
		sendCmd("Q0");
	}
}

void reportError(String errorCode) //R
{
	String cmd = "E" + errorCode;
	sendCmd(cmd);
}

void cycleDoneMsg(int cycle)
{
	String cmd = "D" + cycle;
	sendCmd(cmd);
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

	sendCmd(cmd);
}//end sendScore()

void performResetCycle()//S
{
	Serial.println("Reset");
        openGrippers();
}//end performResetCycle()

void performSelectiveResetCycle(boolean pins[])//S
{
	Serial.println("Selective Reset");
	String pin = "";
	for(int i = 0; i < NUM_PINS; i++)
	{
		if(pins[i]) pin += '1';
		else pin += '0';
	}
	Serial.println(pin);
        //table down
        openGrippers(pins);
        /*delay(1000);
        closeGrippers(pins);*/
}//end performSelectiveResetCycle()

void performRespotCycle()//S
{
	Serial.println("Respot");
	/*String cmd = "C1";
	sendCmd(cmd);
	lastCmd = cmd;*/
}//end performRespotCycle()

void performGutterCycle()//S
{
	Serial.println("Gutter");
	/*String cmd = "C2";
	sendCmd(cmd);
	lastCmd = cmd;*/
}//end performGutterCycle()

void changePowState(boolean state)//S
{
	String cmd = "P";
	if(state)
		cmd += "1";
	else cmd += "0";
	sendCmd(cmd);
	lastCmd = cmd;
}//end changePowState()

void changeLightState(boolean state) //S
{
	String cmd = "L";
	if(state)
		cmd += "1";
	else cmd += "0";
	sendCmd(cmd);
	lastCmd = cmd;
}//end changeLightState()

void doLastCmd()
{
	sendCmd(lastCmd);
}//end doLastCmd()

char genHash(String cmd)
{
	int totalAscii = 1;
	for(int i = 0; i < cmd.length(); i++)
	{
		totalAscii += ((int)cmd.charAt(i) * (i+1));
	}//end for
	Serial.print("Totascii:");
	Serial.println(totalAscii);
	return (char)(totalAscii % HASH_PRIME + FIRST_HASH);
}//end genHash()

void readScoringAndSend()
{
	boolean pins[10];
	
	for(int i = 0; i < NUM_PINS; i++)
	{
		if(digitalRead(SCORING_PINS[i]) == HIGH)
		{
			pins[i] = true;
		}//end if
		else
		{
			pins[i] = false;
		}//end else
	}//end for
	
	sendScore(pins);
}//end readScoringAndSend()

void sendCmd(String cmd)
{
	Serial.println(BEG + cmd + genHash(cmd) + TERMINATOR); 
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
	Serial.println("Open Grippers");
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
	Serial.println("Open Grippers\n");
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
	Serial.println("Close Grippers"); 
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
	Serial.write("Close Grippers"); 
}

void setup() 
{ 
	numFailedAttempts = 0;
	lastCmd = "";

	for(int i = 0; i < NUM_PINS; i++)
	{
		pinMode(SERVO_PINS[i], OUTPUT);
		gripperServo[i].attach(SERVO_PINS[i]);
	}

	pinMode(POW_PIN, OUTPUT);
	pinMode(LIGHT_PIN, OUTPUT);

	Serial.begin(9600);
	Serial.setTimeout(25);
        Serial.println("Arduino on");
        digitalWrite(POW_PIN, HIGH);
	closeGrippers();
} 

void loop() 
{ 
	if(Serial.available() > 0)
	{
		String command = Serial.readString(); 
		//Serial.print("Receivedcommand:");
		//Serial.println(command.substring(1));
		interpretCommand(command);
	}
	delay(10);
} 
