/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef __uart_functionality_H
#define __uart_functionality_H
#ifdef __cplusplus
	extern "C" {
#endif

/* Includes ------------------------------------------------------------------*/
#include "stm32f1xx_hal.h"
#include "usart.h"

/**
 * UART Transmit
 * Sends a character via UART
 * arg: Character data
 * exception: None
 * return: No return value
 */
		
void uartTransmit(char data);

/**
 * UART Receive
 * Receives a character via UART
 * arg: None
 * exception: None
 * return: Character Data
 */

char uartReceive(void);

/**
 * Send String
 * Sends a String via UART
 * arg: String Data
 * exception: None
 * return: No return value
 */

void sendString(char *s);

/**
 * Read String
 * Reads a String via UART
 * arg: None
 * exception: None
 * return: String Data
 */

char * readString(void);
		
#ifdef __cplusplus
}

#endif
#endif /*__ usart_functionality_H */
