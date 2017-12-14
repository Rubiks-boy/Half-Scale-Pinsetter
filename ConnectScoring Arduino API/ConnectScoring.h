/*
  ConnectScoring.h - Library for connecting pinsetters to automatic scoring
  Created by Adam Walker, 12/11/2017
  Used in conjunction with scoring license
*/
#ifndef ConnectScoring_h
#define ConnectScoring_h

#include "Arduino.h"

const int HASH_PRIME = 89;
const String BEG = "}";
const String TERMINATOR = "~";
const int TYPE_LOC = 1;
const int ARG_LOC = 2;
const int MAX_FAILED_ATTEMPTS = 5;
const int HASH_LOC = 2;
const char FIRST_HASH = '!';
const char LAST_HASH = '|';
const int NUM_PINS = 10;
const int NUM_CMDS = 10;
const char CMD_CODES[10] = {'C', 'B', 'E', 'P', 'Q', 'D', 'L', 'M', '0', '1'};
const String CMD_MEANINGS[10] = {"Cycle", "Scores", "Error", "Pow Set", "Pow Msg", "Cycle Done", "Light Set", "Light Msg", "Resend", "Verify Connection"};
		
class ConnectScoring
{
	public:
		String readCommand();
		ConnectScoring();
		void sendCmd(String cmd);
		void doLastCmd();
		void sendScore(boolean pins[]);
		void doResend();
		String readSerial();
		void genTestScoring();
		boolean isConnected();
    
	private:
		String lastCmd;
		int numFailedAttempts;
		boolean verifyCmd(String cmd);
		char genHash(String cmd);
		boolean isRealCmd(String cmd);
		String readSerialUntilCommandReceived();
};
#endif
