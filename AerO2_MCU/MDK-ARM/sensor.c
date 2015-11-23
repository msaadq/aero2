
#include "sensor.h"
#include "adc.h"

uint32_t Get_Sensor_Value(){
	
	uint32_t adc_value;		
	HAL_ADC_Start(&hadc1);
	adc_value = HAL_ADC_GetValue(&hadc1);
	HAL_ADC_Stop(&hadc1);
	
	return adc_value;
}