
#include "usart_functionality.h"

void UART_Transmit_int8(UART_HandleTypeDef huart, uint8_t data){
	
	HAL_UART_Transmit(&huart,&data, sizeof(data),1);
}

void UART_Transmit_int32(UART_HandleTypeDef huart, uint32_t data){
		
	//adc_value is converted to uint8_t integer and size is divided by 4
	HAL_UART_Transmit(&huart,(uint8_t*)(&data), sizeof(data)/4,1);
}

uint8_t UART_Receive(UART_HandleTypeDef huart){
	
	uint8_t data;
	HAL_UART_Receive(&huart,&data, sizeof(data),1); 
	
	return data;
}