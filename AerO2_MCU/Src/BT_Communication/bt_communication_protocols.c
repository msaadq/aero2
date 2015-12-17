#include "bt_communication_protocols.h"

// Default Username and Password
static const char* DEFAULT_USERNAME = "username";
static const char* DEFAULT_PASSWORD = "password";

// All the available Out commands
static const char* O_COM_AUT = "OAUT"; // Authenticate
static const char* O_COM_USR = "OUSR"; // Set Username
static const char* O_COM_PAS = "OPAS"; // Set Password
static const char* O_COM_NSG = "ONSG"; // Enable / Disable Smog Sensor
static const char* O_COM_NAQ = "ONAQ"; // Enable / Disable Air Quality Sensor
static const char* O_COM_SSG = "OSSG"; // Request Smog Data
static const char* O_COM_SAQ = "OSAQ"; // Request Air Quality Data

// All the available In commands
static char* I_COM_AUT = "IAUT"; // Authentication Status
static char* I_COM_USR = "IUSR"; // Username Status
static char* I_COM_PAS = "IPAS"; // Password Status
static char* I_COM_NSG = "INSG"; // Smog Sensor Status
static char* I_COM_NAQ = "INAQ"; // Air Quality Sensor Status
static char* I_COM_SSG = "ISSG"; // Received Smog Data
static char* I_COM_SAQ = "ISAQ"; // Received Air Quality Data

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
char receivedParameters[20];

/**
 * Bluetooth Routine
 * Starts Receiving the commands from the external application and Authenticates using Provided Username and Password
 * arg: None
 * exception: None
 * return: No return value
 */

void btRoutine(void) {
	
	sendString("Started");
	
	char receivedCommand[5];
	int i;
	
	while(!communicationEnded) {
		receivedString = readString();
		
		i = 0;
		
		HAL_Delay(1);
		
		for(; receivedString[i] != '\0' && i < 4; i++) {
			receivedCommand[i] = receivedString[i];
		}
		
		if(i < 4) { break; }
		else { i = 5; }
		
		for(; i < 25 && receivedString != '\0'; i++) {
			receivedParameters[i - 5] = receivedString[i];
		}
		
		if(strcmp(receivedCommand, O_COM_AUT) == 0) {
			autRoutine();
			
		} else if (strcmp(receivedCommand, O_COM_USR) == 0) {
			userRoutine();
			
		} else if (strcmp(receivedCommand, O_COM_PAS) == 0) {
			passRoutine();
			
		} else if (strcmp(receivedCommand, O_COM_NSG) == 0) {
			smogEnable();
			
		} else if (strcmp(receivedCommand, O_COM_NAQ) == 0) {
			airQualityEnable();

		} else if (strcmp(receivedCommand, O_COM_SSG) == 0) {
			reqSmogData();
			
		} else if (strcmp(receivedCommand, O_COM_SAQ) == 0) {
			reqAirQualityData();
			
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
		
	} else {
		smogAvailable = false;
		sendCommand(I_COM_NSG, "0");
		
	}
}

/**
 * Air Quality Sensor Enabler / Disabler
 * Disables or enables the Air Quality Sensor
 * arg: None
 * exception: None
 * return: No return value
 */

void airQualityEnable(void) {
	if(authenticationStatus && receivedString[5] == '1') {
		airQualityAvailable = true;
		sendCommand(I_COM_NAQ, "1");
		
	} else {
		airQualityAvailable = false;
		sendCommand(I_COM_NAQ, "0");
		
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
 * Request Air Quality Data
 * Deals with the smog data requests and sends values accordingly
 * arg: None
 * exception: None
 * return: No return value
 */

void reqAirQualityData(void) {
	if(airQualityAvailable) {
		char airQualityData[3];
		/*
		int iAirQualityData = 0;
		
		for(int i = 0; i < 100; i++) {
			iAirQualityData += getAirQualitySensorValue();
		}	
		iAirQualityData /= 100;
		
		sprintf(airQualityData, "%d", iAirQualityData);
		*/
		sendCommand(I_COM_SAQ, airQualityData);
		
	} else {
		sendCommand(I_COM_NAQ, "0");
		
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
