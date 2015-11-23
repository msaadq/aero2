
#include "usart_functionality.h"
#include "usart.h"

void UART_Transmit_int8(uint8_t data){
	
	HAL_UART_Transmit(&huart1,&data, sizeof(data),1);
}

void UART_Transmit_int32(uint32_t data){
		
	//adc_value is converted to uint8_t integer and size is divided by 4
	HAL_UART_Transmit(&huart1,(uint8_t*)(&data), sizeof(data)/4,1);
}

uint8_t UART_Receive(){
	
	uint8_t data;
	HAL_UART_Receive(&huart1,&data, sizeof(data),1); 
	
	return data;
}