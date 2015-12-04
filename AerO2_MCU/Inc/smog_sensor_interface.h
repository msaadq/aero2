/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef __smog_sensor_interface_H
#define __smog_sensor_interface_H
#ifdef __cplusplus
	extern "C" {
#endif

/* Includes ------------------------------------------------------------------*/
#include "stm32f1xx_hal.h"
#include "adc.h"

/**
 * Get Smog Sensor Value
 * Returns the value received from the Smog Sensor via ADC
 * arg: None
 * exception: None
 * return: Integer Value of Smog Level
 */

int getSmogSensorValue(void);
	 
#ifdef __cplusplus
}

#endif
#endif /*__smog_sensor_interface_H */
