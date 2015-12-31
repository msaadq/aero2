#include "battery_level_sensor.h"
#define MAX_LEVEL 4096.0

/**
 * Get Battery Percentage
 * Returns the value received from the Battery Level Indicator via ADC
 * arg: None
 * exception: None
 * return: % Level of Battery
 */

int getBatteryPercentage(void){
	
	uint32_t adcValue;		
	HAL_ADC_Start(&hadc2);
	adcValue = HAL_ADC_GetValue(&hadc2);
	HAL_ADC_Stop(&hadc2);	
	
  int percentage = ((int) adcValue / MAX_LEVEL) * 100;
	
	return percentage;
}
