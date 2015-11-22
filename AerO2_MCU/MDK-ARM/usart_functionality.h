/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef __usart_functionality_H
#define __usart_functionality_H
#ifdef __cplusplus
	extern "C" {
#endif

/* Includes ------------------------------------------------------------------*/
#include "stm32f1xx_hal.h"
		
void UART_Transmit_int8(UART_HandleTypeDef, uint8_t data);
void UART_Transmit_int32(UART_HandleTypeDef, uint32_t data);
uint8_t UART_Receive(UART_HandleTypeDef);

#ifdef __cplusplus
}

#endif
#endif /*__ usart_functionality_H */