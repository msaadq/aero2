/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef __init_H
#define __init_H
#ifdef __cplusplus
 extern "C" {
#endif

/* Includes ------------------------------------------------------------------*/
#include "adc.h"
#include "usart.h"
#include "gpio.h"
#include "system_clock.h"
#include "smog_sensor_interface.h"

/**
 * Initialize STM
 * Initializes the STM MCU and sets the default parameters
 * Also, sets Pin 10 and Pin 9 for UART functionality and sets IN0 as ADC
 * arg: None
 * exception: None
 * return: Integer Value of Smog Level
 */

void initSTM(void);

#ifdef __cplusplus
}
#endif
#endif /*__init_H */
