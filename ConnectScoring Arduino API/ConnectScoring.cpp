/*
  ConnectScoring.cpp - Library for connecting pinsetters to automatic scoring
  Created by Adam Walker, 12/11/2017
  Used in conjunction with scoring license
*/

#include "ConnectScoring.h"
    
//Constructor
ConnectScoring::ConnectScoring()
{
  	numFailedAttempts = 0;
	lastCmd = "";

	Serial.begin(9600);
	Serial.setTimeout(25);
}

//Verifies that the command is good based on the hash.
boolean ConnectScoring::verifyCmd(String cmd) //R
{
	char hash = cmd.charAt(cmd.length() - HASH_LOC);
	String cmdInner = cmd.substring(1, cmd.length() - HASH_LOC);
	return (hash == genHash(cmdInner));
}//end verifyCmd()

//Generates a basic hash used to verify commands
char ConnectScoring::genHash(String cmd)
{
	int totalAscii = 1;
	for(int i = 0; i < cmd.length(); i++)
	{
		totalAscii += ((int)cmd.charAt(i) * (i+1));
	}//end for
	//Serial.print("Totascii:");
	//Serial.println(totalAscii);
	return (char)(totalAscii % HASH_PRIME + FIRST_HASH);
}//end genHash()

//Sends a command to the scoring system.
//Not advised to use this since it's called by other functions.
void ConnectScoring::sendCmd(String cmd)
{
	Serial.println(BEG + cmd + genHash(cmd) + TERMINATOR); 
}//end sendCmd()

//Resends the last command.
void ConnectScoring::doLastCmd()
{
	sendCmd(lastCmd);
}//end doLastCmd()

//Sends a score based on pinfall data.
void ConnectScoring::sendScore(boolean pins[])
{
	String cmd = "S";

	for(int i = 0; i < NUM_PINS; i++)
	{
		if(pins[i])
			cmd += "1";
		else cmd += "0";
	}

	sendCmd(cmd);
	
	////MAKE THING THAT WAITS FOR COMMAND
}//end sendScore()

//Clears previous commands, sends score, and returns what cycle to do next.
//0: Reset, 1: Respot, 2: Gutter
int ConnectScoring::sendScoreCycle(boolean pins[])
{
	Serial.flush();
	
	sendScore(pins);
	
	String cmd;
	for(int i = 0; i < 50; i++)
	{
		cmd = readSerial();
		if(cmd != "")
			break;
		delay(10);
	}
	
	if(isRealCmd(cmd))
	{
		if(cmd.charAt(TYPE_LOC) == 'C')
			return cmd.charAt(ARG_LOC);
	}
	else doResend();
}

//Used command sent from scoring was unsuccessful; asks for a resend.
void ConnectScoring::doResend() //S
{
	if(++numFailedAttempts < MAX_FAILED_ATTEMPTS)
	{
		sendCmd("0");
	}
	else
	{
		numFailedAttempts = 0;
	}
}

//Sees if the command corresponds to an actual command
boolean ConnectScoring::isRealCmd(String cmd)
{
	if(verifyCmd(cmd))
	{
		for(int i = 0; i < NUM_CMDS; i++)
		{
			if(CMD_CODES[i] == cmd.charAt(TYPE_LOC))
				return true;
		}//end for
	}
	else
		return false;
	return false;
}

//Reads the command from serial. Otherwise returns empty string.
String ConnectScoring::readSerial()
{
	if(Serial.available() > 0)
	{
		return Serial.readString(); 
	}
	return "";
}

//Generates a random number of pins knocked down and tests sending score.
void ConnectScoring::genTestScoring()
{
	boolean pins[10];
	int score = random(10);
	int i = 0;
	for(i = 0; i < score; i++)
	{
		pins[i] = true;
	}
    for(i = score; i < 10; i++)
    {
		pins[i] = false; 
    }
	sendScore(pins);
}

//Generates a random number of pins knocked down and tests sending score with cycle output.
int ConnectScoring::genTestScoringCycle()
{
	boolean pins[10];
	int score = random(10);
	int i = 0;
	for(i = 0; i < score; i++)
	{
		pins[i] = true;
	}
    for(i = score; i < 10; i++)
    {
		pins[i] = false; 
    }
	return sendScoreCycle(pins);
}