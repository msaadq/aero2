#include "bt_communication_protocols.h"

// Default Username and Password
static const char* DEFAULT_USERNAME = "username";
static const char* DEFAULT_PASSWORD = "password";

// All the available Out commands
static const char* O_COM_AUT = "OAUT"; // Authenticate
static const char* O_COM_USR = "OUSR"; // Set Username
static const char* O_COM_PAS = "OPAS"; // Set Password
static const char* O_COM_NSG = "ONSG"; // Enable / Disable Smog Sensor
static const char* O_COM_SSG = "OSSG"; // Request Smog Data
static const char* O_COM_BAT = "OBAT"; // Request Battery Percentage

// All the available In commands
static char* I_COM_AUT = "IAUT"; // Authentication Status
static char* I_COM_USR = "IUSR"; // Username Status
static char* I_COM_PAS = "IPAS"; // Password Status
static char* I_COM_NSG = "INSG"; // Smog Sensor Status
static char* I_COM_SSG = "ISSG"; // Received Smog Data
static char* I_COM_BAT = "IBAT"; // Received Battery Percentage

// Booleans representing program status
bool communicationEnded = false;
bool btAvailable = false;
bool authenticationStatus = false;
bool userCorrect = false;
bool passwordCorrect = false;
bool smogAvailable = false;
bool airQualityAvailable = false;

// Complete Received Command
char * receivedString;
char * receivedParameters;

/**
 * Bluetooth Routine
 * Starts Receiving the commands from the external application and Authenticates using Provided Username and Password
 * arg: None
 * exception: None
 * return: No return value
 */

void btRoutine(void) {
	
	sendString("Started BT Routine. Connect using 4 Letter Commands.");
	
	while(!communicationEnded) {
		receivedString = readString();
		
		HAL_Delay(2);
		
		receivedParameters = strtok(receivedString, ":"); 
		receivedParameters = strtok(NULL, "\n");  
			
		if(compareCommand(receivedString, O_COM_AUT)) {
			autRoutine();			
		} else if (compareCommand(receivedString, O_COM_USR)) {
			userRoutine();		
		} else if (compareCommand(receivedString, O_COM_PAS)) {
			passRoutine();		
		} else if (compareCommand(receivedString, O_COM_NSG)) {
			smogEnable();		
		} else if (compareCommand(receivedString, O_COM_SSG)) {
			reqSmogData();	
		} else if (compareCommand(receivedString, O_COM_BAT)) {
			reqBatteryStatus();	
		} else {
			sendCommand("IINV","COMMAND_REJECTED");		
		}
	}
}

/**
 * Authentication Routine
 * Disables or enables the communication depending on the command parameters
 * arg: None
 * exception: None
 * return: No return value
 */

void autRoutine(void) {
	if(receivedString[5] == '1') {
		btAvailable = true;
		sendCommand(I_COM_AUT, "1");
		
	} else if (receivedString[5] == '0') {
		btAvailable = false;
		sendCommand(I_COM_AUT, "0");
		
	}
}

/**
 * User Routine
 * Checks for correct username
 * arg: None
 * exception: None
 * return: No return value
 */

void userRoutine(void) {	
	bool userVerified = true;
	
	for(int i = 0; DEFAULT_USERNAME[i] != '\0'; i++) {
		if(receivedParameters[i] != DEFAULT_USERNAME[i]) {
			userVerified = false;
		}
	}
	
	if(btAvailable && userVerified) {
		userCorrect = true;
		sendCommand(I_COM_USR, "1");
		
	} else {
		userCorrect = false;
		sendCommand(I_COM_USR, "0");
		
	}
}

/**
 * Password Routine
 * Checks for correct password
 * arg: None
 * exception: None
 * return: No return value
 */

void passRoutine(void) {
	bool passVerified = true;
	
	for(int i = 0; DEFAULT_PASSWORD[i] != '\0'; i++) {
		if(receivedParameters[i] != DEFAULT_PASSWORD[i]) {
			passVerified = false;
		}
	}
	
	if(userCorrect && passVerified) {
		passwordCorrect = true;
		authenticationStatus = true;
		sendCommand(I_COM_PAS, "1");
		
	} else {
		passwordCorrect = false;
		authenticationStatus = false;
		sendCommand(I_COM_PAS, "0");
		
	}
}

/**
 * Smog Sensor Enabler / Disabler
 * Disables or enables the Smog Sensor
 * arg: None
 * exception: None
 * return: No return value
 */

void smogEnable(void) {
	if(authenticationStatus && receivedString[5] == '1') {
		smogAvailable = true;
		sendCommand(I_COM_NSG, "1");
		
		enableSmogSensor();
	} else {
		smogAvailable = false;
		sendCommand(I_COM_NSG, "0");
		
		disableSmogSensor();
	}
}

/**
 * Request Smog Data
 * Deals with the smog data requests and sends values accordingly
 * arg: None
 * exception: None
 * return: No return value
 */

void reqSmogData(void) {
	if(smogAvailable) {
		char smogData[3];
		int iSmogData = 0;
		
		for(int i = 0; i < 1000; i++) {
			iSmogData += getSmogSensorValue();
		}	
		iSmogData /= 1000;
		
		sprintf(smogData, "%d", iSmogData);
		       
		sendCommand(I_COM_SSG, smogData);
		
	} else {
		sendCommand(I_COM_NSG, "0");
		
	}
}

/**
 * Request Battery Status
 * Deals with the battery percentage status requests and sends data accordingly
 * arg: None
 * exception: None
 * return: No return value
 */

void reqBatteryStatus(void) {
	if(authenticationStatus) {
		char batteryPercentage[3];
		int iBatteryPercentage = 0;
		
		for(int i = 0; i < 1000; i++) {
			iBatteryPercentage += getBatteryPercentage();
		}	
		iBatteryPercentage /= 1000;
		
		sprintf(batteryPercentage, "%d", iBatteryPercentage);
		       
		sendCommand(I_COM_BAT, batteryPercentage);
		
	} else {
		sendCommand(I_COM_PAS, "0");
		
	}
}

/**
 * Send Command
 * Sends the command to application in the default format
 * arg: The String command and String parameters
 * exception: None
 * return: No return value
 */

void sendCommand(char * command, char * parameters) {
	char* finalCommand = commandBuilder(command, parameters);

	sendString(finalCommand);	
	free(finalCommand); // deallocates the string and frees the memory
}

/**
 * Command Builder
 * Contatenates the command with parameters
 * arg: 2 Strings to concatenate
 * exception: None
 * return: Concatenated Strings with ":" between them
 */

char * commandBuilder(char *s1, char *s2) {
    char *result = malloc(strlen(s1)+strlen(s2)+2);
    
    strcpy(result, s1);
		strcat(result, ":");
    strcat(result, s2);
	
    return result;
}

/**
 * Compare Command
 * Comapares String to contain the pre defined commands
 * arg: 2 Strings
 * exception: None
 * return: bool
 */

bool compareCommand(char *receivedCommand, const char *sampleCommand) {
    bool containsCommand = true;
    
    for(int i = 0; i < 4; i++) {
			if(receivedCommand == '\0') {
				containsCommand = false;
				break;
			}
			
			if(receivedCommand[i] != sampleCommand[i]) {
				containsCommand = false;
			}
		}
	
    return containsCommand;
}

