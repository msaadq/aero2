
#include "sensor.h"

uint32_t Get_Sensor_Value(ADC_HandleTypeDef hadc){
	
	uint32_t adc_value;		
	HAL_ADC_Start(&hadc);
	adc_value = HAL_ADC_GetValue(&hadc);
	HAL_ADC_Stop(&hadc);
	
	return adc_value;
}