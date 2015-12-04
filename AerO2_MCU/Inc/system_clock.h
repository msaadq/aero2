/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef __system_clock_H
#define __systems_clock_H
#ifdef __cplusplus
 extern "C" {
#endif

/* Includes ------------------------------------------------------------------*/
#include "stm32f1xx_hal.h"

/**
 * Configure Systems Clock
 * Configures the System Clock to systems defaults
 * arg: None
 * exception: None
 * return: Integer Value of Smog Level
 */

void configureSystemClock(void);

#ifdef __cplusplus
}
#endif
#endif /*__system_clock_H */
