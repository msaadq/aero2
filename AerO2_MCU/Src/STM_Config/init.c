/* Includes ------------------------------------------------------------------*/
#include "init.h"

/**
 * Initialize STM
 * Initializes the STM MCU and sets the default parameters
 * Also, sets Pin 10 and Pin 9 for UART functionality and sets IN0 as ADC
 * arg: None
 * exception: None
 * return: Integer Value of Smog Level
 */

void initSTM(void) {
	 /* Reset of all peripherals, Initializes the Flash interface and the Systick. */
  HAL_Init();

  /* Configure the system clock */
  configureSystemClock();

  /* Initialize all configured peripherals */
  MX_GPIO_Init();
  MX_ADC1_Init();
  MX_USART1_UART_Init();
	
}
