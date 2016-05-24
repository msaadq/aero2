/*
 * BT_Communication.c
 *
 * Created: 5/16/2016 5:31:24 PM
 *  Author: Saad
 */ 

 // Includes
 #include "bt_communication.h"

// Default Username and Password
static const char* DEFAULT_USERNAME = "username";
static const char* DEFAULT_PASSWORD = "password";

// All the available Out commands
static const char* O_COM_AUT = "OAUT"; // Authenticate
static const char* O_COM_PAS = "OPAS"; // Set Password
static const char* O_COM_NSG = "ONSG"; // Enable / Disable Smog Sensor
static const char* O_COM_SSG = "OSSG"; // Request Smog Data

// All the available In commands
static char* I_COM_AUT = "IAUT"; // Authentication Status
static char* I_COM_PAS = "IPAS"; // Password Status
static char* I_COM_NSG = "INSG"; // Smog Sensor Status
static char* I_COM_SSG = "ISSG"; // Received Smog Data

// Booleans representing program status
bool btAvailable = false;
bool autCorrect = false;
bool smogAvailable = false;

// Complete Received Command
char * receivedString;
char * receivedParameters;

/**
 * Initialize Blue tooth
 * Starts the BT routine by enabling the ADC and USART
 * arg: None
 * exception: None
 * return: No return value
 */

void Init_BT()
{
	USART0_Init(BAUD_PRESCALER);
	Init_ADC();
}

/**
 * Bluetooth Routine
 * Starts Receiving the commands from the external application and Authenticates using Provided Username and Password
 * arg: None
 * exception: None
 * return: No return value
 */

void BT_Routine(void) {
	
	USART_putstring("READY!");
	
	while(1) {
		receivedString = USART_getstring('\n');
		
		_delay_ms(1);
		
		receivedParameters = strtok(receivedString, ":"); 
		receivedParameters = strtok(NULL, "\n");  
			
		if(Compare_Command(receivedString, O_COM_AUT)) {
			Aut_Routine();				
		} else if (Compare_Command(receivedString, O_COM_PAS)) {
			Pass_Routine();		
		} else if (Compare_Command(receivedString, O_COM_NSG)) {
			Smog_Enable();		
		} else if (Compare_Command(receivedString, O_COM_SSG)) {
			Req_Smog_Data();	
		} else {
			Send_Command("IINV","COMMAND_REJECTED");		
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

void Aut_Routine(void) {
	if(receivedString[5] == '1') {
		btAvailable = true;
		Send_Command(I_COM_AUT, "1");
		
	} else if (receivedString[5] == '0') {
		btAvailable = false;
		Send_Command(I_COM_AUT, "0");		
	}
}

/**
 * Password Routine
 * Checks for correct password
 * arg: None
 * exception: None
 * return: No return value
 */

void Pass_Routine(void) {
	bool passVerified = true;
	
	for(int i = 0; DEFAULT_PASSWORD[i] != '\0'; i++) {
		if(receivedParameters[i] != DEFAULT_PASSWORD[i]) {
			passVerified = false;
		}
	}
	
	if(passVerified) {
		autCorrect = true;
		Send_Command(I_COM_PAS, "1");
		
	} else {
		autCorrect = false;
		Send_Command(I_COM_PAS, "0");
	}
}

/**
 * Smog Sensor Enabler / Disabler
 * Disables or enables the Smog Sensor
 * arg: None
 * exception: None
 * return: No return value
 */

void Smog_Enable(void) {
	if(autCorrect & receivedString[5] == '1') {	
		Enable_Sensors();
		smogAvailable = true;
		Send_Command(I_COM_NSG, "1");
	} else {
		Disable_Sensors();
		smogAvailable = false;
		Send_Command(I_COM_NSG, "0");
	}
}

/**
 * Request Smog Data
 * Deals with the smog data requests and sends values accordingly
 * arg: None
 * exception: None
 * return: No return value
 */

void Req_Smog_Data(void) {
	if(autCorrect & smogAvailable) {
		char smogData[3];
		int iSmogData = 0;
		
		for(int i = 0; i < 1000; i++) {
			iSmogData += Get_Sensor_Data();
		}	
		iSmogData /= 1000;
		
		sprintf(smogData, "%d", iSmogData);
		       
		Send_Command(I_COM_SSG, smogData);
		
	} else {
		Send_Command(I_COM_NSG, "0");	
	}
}


/**
 * Send Command
 * Sends the command to application in the default format
 * arg: The String command and String parameters
 * exception: None
 * return: No return value
 */

void Send_Command(char * command, char * parameters) {
	
	char *finalCommand = malloc(strlen(command)+strlen(parameters)+2);
	
	strcpy(finalCommand, command);
	strcat(finalCommand, ":");
	strcat(finalCommand, parameters);

	USART_putstring(finalCommand);	
	free(finalCommand); // deallocates the string and frees the memory
}

/**
 * Compare Command
 * Compares String to contain the pre-defined commands
 * arg: 2 Strings
 * exception: None
 * return: bool
 */

bool Compare_Command(char *receivedCommand, const char *sampleCommand) {
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


