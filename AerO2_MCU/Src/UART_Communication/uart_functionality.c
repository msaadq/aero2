// Includes

#include "uart_functionality.h"

/**
 * UART Transmit
 * Sends a character via UART
 * arg: Character data
 * exception: None
 * return: No return value
 */

void uartTransmit(char toSend){
	
	uint8_t data = (uint8_t) toSend;
	
	HAL_UART_Transmit(&huart1, &data, sizeof(data),1);
	
	while (HAL_UART_GetState(&huart1) != HAL_UART_STATE_READY);
}

/**
 * UART Receive
 * Receives a character via UART
 * arg: None
 * exception: None
 * return: Character Data
 */

char uartReceive(void){
	
	uint8_t byteData = 0;
	
	while(byteData == 0) {
		HAL_UART_Receive(&huart1, &byteData, sizeof(byteData), 1);
	}
	
	while (HAL_UART_GetState(&huart1) != HAL_UART_STATE_READY);
	
	char toReceive = (char) byteData;
	
	return toReceive;
}

/**
 * Send String
 * Sends a String via UART
 * arg: String Data
 * exception: None
 * return: No return value
 */

void sendString(char *s) {
	
	for(int i = 0; s[i] != '\0'; i++ ) {
		uartTransmit(s[i]);
	}
	
	uartTransmit('\n');
}

/**
 * Read String
 * Reads a String via UART
 * arg: None
 * exception: None
 * return: String Data
 */

char * readString(void) {
	static char stringData[25];

	HAL_Delay(1);

	for(int i = 0; ; i++) {
		stringData[i] = uartReceive();
		if(stringData[i] == '\n') { break; }
	}
	
	return stringData;
}
