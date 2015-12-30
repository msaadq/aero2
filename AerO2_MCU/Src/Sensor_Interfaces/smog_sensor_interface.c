#include "smog_sensor_interface.h"

/**
 * Enable Smog Sensor
 * Enables the smog sensor
 * arg: None
 * exception: None
 * return: None
 */

void enableSmogSensor(void) {
    HAL_GPIO_WritePin(SMOG_SWITCH_GPIO_Port, SMOG_SWITCH_Pin, 1);
}

/**
 * Disable Smog Sensor
 * Disables the smog sensor
 * arg: None
 * exception: None
 * return: None
 */

void disableSmogSensor(void) {
    HAL_GPIO_WritePin(SMOG_SWITCH_GPIO_Port, SMOG_SWITCH_Pin, 0);
}

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
