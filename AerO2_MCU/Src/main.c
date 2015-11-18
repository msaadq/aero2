/**
  ******************************************************************************
  * File Name          : main.c
  * Description        : Main program body
  ******************************************************************************
  *
  * COPYRIGHT(c) 2015 STMicroelectronics
  *
  * Redistribution and use in source and binary forms, with or without modification,
  * are permitted provided that the following conditions are met:
  *   1. Redistributions of source code must retain the above copyright notice,
  *      this list of conditions and the following disclaimer.
  *   2. Redistributions in binary form must reproduce the above copyright notice,
  *      this list of conditions and the following disclaimer in the documentation
  *      and/or other materials provided with the distribution.
  *   3. Neither the name of STMicroelectronics nor the names of its contributors
  *      may be used to endorse or promote products derived from this software
  *      without specific prior written permission.
  *
  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
  * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
  * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
  * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
  * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
  * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
  * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  *
  ******************************************************************************
  */
/* Includes ------------------------------------------------------------------*/
#include "stm32f1xx_hal.h"
#include "adc.h"
#include "usart.h"
#include "gpio.h"

/* USER CODE BEGIN Includes */

/* USER CODE END Includes */

/* Private variables ---------------------------------------------------------*/

/* USER CODE BEGIN PV */
/* Private variables ---------------------------------------------------------*/

/* USER CODE END PV */

/* Private function prototypes -----------------------------------------------*/
void SystemClock_Config(void);

/* USER CODE BEGIN PFP */
/* Private function prototypes -----------------------------------------------*/

/* USER CODE END PFP */

/* USER CODE BEGIN 0 */

/* USER CODE END 0 */

int main(void)
{

  /* USER CODE BEGIN 1 */
	

  /* USER CODE END 1 */

  /* MCU Configuration----------------------------------------------------------*/

  /* Reset of all peripherals, Initializes the Flash interface and the Systick. */
  HAL_Init();

  /* Configure the system clock */
  SystemClock_Config();

  /* Initialize all configured peripherals */
  MX_GPIO_Init();
  MX_ADC1_Init();
  MX_USART1_UART_Init();
	
	ADC_InitTypeDef ADC_InitStructure;
    Usart1Init();

    RCC_ADCCLKConfig(RCC_PCLK2_Div6); //ADCCLK = PCLK22/6 = 72/6=12MHz
    RCC_APB2PeriphClockCmd(RCC_APB2Periph_ADC1, ENABLE); //Enable ADC1 Clock

    /* ADC1 configuration */
    ADC_DeInit(ADC1); //Power-on default
    ADC_InitStructure.ADC_Mode = ADC_Mode_Independent; //Independent conversion mode (single)
    ADC_InitStructure.ADC_ScanConvMode = DISABLE; //Convert single channel only
    ADC_InitStructure.ADC_ContinuousConvMode = DISABLE; //Convert 1 time
    ADC_InitStructure.ADC_ExternalTrigConv = DISABLE; //No external triggering
    ADC_InitStructure.ADC_DataAlign = ADC_DataAlign_Right; //Right 12-bit data alignment
    ADC_InitStructure.ADC_NbrOfChannel = 1; //single channel conversion
    ADC_Init(ADC1, &ADC_InitStructure);
    ADC_TempSensorVrefintCmd(ENABLE); //wake up temperature sensor
    ADC_Cmd(ADC1, ENABLE); //Enable ADC1
    ADC_ResetCalibration(ADC1); //Enable ADC1 reset calibration register
    while(ADC_GetResetCalibrationStatus(ADC1)); //check the end of ADC1 reset calibration register  
    ADC_StartCalibration(ADC1); //Start ADC1 calibration
    while(ADC_GetCalibrationStatus(ADC1)); //Check the end of ADC1 calibration
    ADC_RegularChannelConfig(ADC1, ADC_Channel_16, 1, ADC_SampleTime_1Cycles5); //Select 1.5 cycles conversion for channel 16
    ADC_SoftwareStartConvCmd(ADC1, ENABLE); //Start ADC1 software conversion
    while(ADC_GetFlagStatus(ADC1, ADC_FLAG_EOC) == RESET); //Wail for conversion complete
    AD_value = ADC_GetConversionValue(ADC1); //Read ADC value
    ADC_ClearFlag(ADC1, ADC_FLAG_EOC); //Clear EOC flag

    printf("\r\n ADC value: %d \r\n", AD_value);
    TemperatureC = (uint16_t)((V25-AD_value)/Avg_Slope+25);
    printf("Temperature: %d%cC\r\n", TemperatureC, 176);
    while (1)
    {}  


  /* USER CODE BEGIN 2 */
	unsigned char message[2] = "AT";
	unsigned char endLine[1] = "\n";
	

  /* USER CODE END 2 */
	HAL_UART_Transmit(&huart1, message, 2, 2000);


  /* Infinite loop */
  /* USER CODE BEGIN WHILE */
  while (1)
  {
  /* USER CODE END WHILE */
			
	
/*			
			 unsigned char adc_char[1] = {(unsigned char) adc};
						HAL_UART_Transmit(&huart1, adc_char, 2, 2000);
			 
			 HAL_Delay(2000);
			
*/
		}

		

  /* USER CODE BEGIN 3 */

  }
  /* USER CODE END 3 */



/** System Clock Configuration
*/
void SystemClock_Config(void)
{

  RCC_OscInitTypeDef RCC_OscInitStruct;
  RCC_ClkInitTypeDef RCC_ClkInitStruct;
  RCC_PeriphCLKInitTypeDef PeriphClkInit;

  RCC_OscInitStruct.OscillatorType = RCC_OSCILLATORTYPE_HSI;
  RCC_OscInitStruct.HSIState = RCC_HSI_ON;
  RCC_OscInitStruct.HSICalibrationValue = 16;
  RCC_OscInitStruct.PLL.PLLState = RCC_PLL_ON;
  RCC_OscInitStruct.PLL.PLLSource = RCC_PLLSOURCE_HSI_DIV2;
  RCC_OscInitStruct.PLL.PLLMUL = RCC_PLL_MUL16;
  HAL_RCC_OscConfig(&RCC_OscInitStruct);

  RCC_ClkInitStruct.ClockType = RCC_CLOCKTYPE_SYSCLK|RCC_CLOCKTYPE_PCLK1;
  RCC_ClkInitStruct.SYSCLKSource = RCC_SYSCLKSOURCE_PLLCLK;
  RCC_ClkInitStruct.AHBCLKDivider = RCC_SYSCLK_DIV1;
  RCC_ClkInitStruct.APB1CLKDivider = RCC_HCLK_DIV2;
  RCC_ClkInitStruct.APB2CLKDivider = RCC_HCLK_DIV1;
  HAL_RCC_ClockConfig(&RCC_ClkInitStruct, FLASH_LATENCY_2);

  PeriphClkInit.PeriphClockSelection = RCC_PERIPHCLK_ADC;
  PeriphClkInit.AdcClockSelection = RCC_ADCPCLK2_DIV8;
  HAL_RCCEx_PeriphCLKConfig(&PeriphClkInit);

  HAL_SYSTICK_Config(HAL_RCC_GetHCLKFreq()/1000);

  HAL_SYSTICK_CLKSourceConfig(SYSTICK_CLKSOURCE_HCLK);

  /* SysTick_IRQn interrupt configuration */
  HAL_NVIC_SetPriority(SysTick_IRQn, 0, 0);
}

/* USER CODE BEGIN 4 */

/* USER CODE END 4 */

#ifdef USE_FULL_ASSERT

/**
   * @brief Reports the name of the source file and the source line number
   * where the assert_param error has occurred.
   * @param file: pointer to the source file name
   * @param line: assert_param error line source number
   * @retval None
   */
void assert_failed(uint8_t* file, uint32_t line)
{
  /* USER CODE BEGIN 6 */
  /* User can add his own implementation to report the file name and line number,
    ex: printf("Wrong parameters value: file %s on line %d\r\n", file, line) */
  /* USER CODE END 6 */

}

#endif

/**
  * @}
  */ 

/**
  * @}
*/ 

/************************ (C) COPYRIGHT STMicroelectronics *****END OF FILE****/
