/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef __battery_level_sensor_H
#define __battery_leve_sensor_H
#ifdef __cplusplus
	extern "C" {
#endif

/* Includes ------------------------------------------------------------------*/
#include "stm32f1xx_hal.h"
#include "adc.h"

/**
 * Get Battery Percentage
 * Returns the value received from the Battery Level Indicator via ADC
 * arg: None
 * exception: None
 * return: % Level of Battery
 */

int getBatteryPercentage(void);
	 
#ifdef __cplusplus
}

#endif
#endif /* __battery_leve_sensor_H */
