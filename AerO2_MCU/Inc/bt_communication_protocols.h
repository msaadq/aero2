/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef __bt_communication_protocols_H
#define __bt_communication_protocols_H
#ifdef __cplusplus
 extern "C" {
#endif

/* Includes ------------------------------------------------------------------*/
#include "smog_sensor_interface.h"
#include "uart_functionality.h"
#include <stdbool.h>
#include <stdlib.h>
#include <string.h>

/**
 * Bluetooth Routine
 * Starts Receiving the commands from the external application and Authenticates using Provided Username and Password
 * arg: None
 * exception: None
 * return: No return value
 */

void btRoutine(void);

/**
 * Authentication Routine
 * Disables or enables the communication depending on the command parameters
 * arg: None
 * exception: None
 * return: No return value
 */

void autRoutine(void);

/**
 * User Routine
 * Checks for correct username
 * arg: None
 * exception: None
 * return: No return value
 */

void userRoutine(void);

/**
 * Password Routine
 * Checks for correct password
 * arg: None
 * exception: None
 * return: No return value
 */

void passRoutine(void);

/**
 * Smog Sensor Enabler / Disabler
 * Disables or enables the Smog Sensor
 * arg: None
 * exception: None
 * return: No return value
 */

void smogEnable(void);

/**
 * Air Quality Sensor Enabler / Disabler
 * Disables or enables the Air Quality Sensor
 * arg: None
 * exception: None
 * return: No return value
 */

void airQualityEnable(void);

/**
 * Request Smog Data
 * Deals with the smog data requests and sends values accordingly
 * arg: None
 * exception: None
 * return: No return value
 */

void reqSmogData(void);

/**
 * Request Air Quality Data
 * Deals with the smog data requests and sends values accordingly
 * arg: None
 * exception: None
 * return: No return value
 */

void reqAirQualityData(void);

/**
 * Send Command
 * Sends the command to application in the default format
 * arg: The String command and String parameters
 * exception: None
 * return: No return value
 */

void sendCommand(char * command, char * parameters);

/**
 * Command Builder
 * Contatenates the command with parameters
 * arg: 2 Strings to concatenate
 * exception: None
 * return: Concatenated Strings with ":" between them
 */

char * commandBuilder(char *command, char *parameters);

#ifdef __cplusplus
}
#endif
#endif /*__bt_communication_protocols_H */
