/*
 * BT_Communication.h
 *
 * Created: 5/16/2016 5:33:11 PM
 *  Author: Saad
 */ 


#ifndef BT_COMMUNICATION_H_
#define BT_COMMUNICATION_H_

/* Includes ------------------------------------------------------------------*/
#define F_CPU 8000000 // 1 MHz
#define BAUD 9600
#define BAUD_PRESCALER F_CPU / 16 / BAUD - 1

#include <stdbool.h>
#include <stdlib.h>
#include <string.h>
#include <util/delay.h>

#include "usart.h"
#include "sensor_interface.h"

/**
 * Initialize Blue tooth
 * Starts the BT routine by enabling the ADC and USART
 * arg: None
 * exception: None
 * return: No return value
 */

void Init_BT();

/**
 * Bluetooth Routine
 * Starts Receiving the commands from the external application and Authenticates using Provided Username and Password
 * arg: None
 * exception: None
 * return: No return value
 */

void BT_Routine(void);

/**
 * Authentication Routine
 * Disables or enables the communication depending on the command parameters
 * arg: None
 * exception: None
 * return: No return value
 */

void Aut_Routine(void);

/**
 * Password Routine
 * Checks for correct password
 * arg: None
 * exception: None
 * return: No return value
 */

void Pass_Routine(void);

/**
 * Smog Sensor Enabler / Disabler
 * Disables or enables the Smog Sensor
 * arg: None
 * exception: None
 * return: No return value
 */

void Smog_Enable(void);

/**
 * Air Quality Sensor Enabler / Disabler
 * Disables or enables the Air Quality Sensor
 * arg: None
 * exception: None
 * return: No return value
 */

void Req_Smog_Data(void);

/**
 * Request Battery Status
 * Deals with the battery percentage status requests and sends data accordingly
 * arg: None
 * exception: None
 * return: No return value
 */

void Send_Command(char * command, char * parameters);

/**
 * Compare Command
 * Compares String to contain the pre-defined commands
 * arg: 2 Strings
 * exception: None
 * return: bool
 */

bool Compare_Command(char *receivedCommand, const char *sampleCommand);


#endif /* BT_COMMUNICATION_H_ */