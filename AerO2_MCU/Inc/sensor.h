/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef __sensor_H
#define __sensor_H
#ifdef __cplusplus
	extern "C" {
#endif

/* Includes ------------------------------------------------------------------*/
#include "stm32f1xx_hal.h"

uint32_t Get_Sensor_Value(ADC_HandleTypeDef);
	 
#ifdef __cplusplus
}

#endif
#endif /*__ sensor_H */