#include "smog_sensor_interface.h"

/**
 * Get Smog Sensor Value
 * Returns the value received from the Smog Sensor via ADC
 * arg: None
 * exception: None
 * return: Integer Value of Smog Level
 */

int getSmogSensorValue(void){
	
	uint32_t adcValue;		
	HAL_ADC_Start(&hadc1);
	adcValue = HAL_ADC_GetValue(&hadc1);
	HAL_ADC_Stop(&hadc1);
	
	return (int) adcValue;
}
