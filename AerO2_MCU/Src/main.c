/**
  ******************************************************************************
  * File Name          : main.c
  * Description        : Main program body
  ******************************************************************************
  */
	
/* Includes ------------------------------------------------------------------*/
#include "init.h"
#include "bt_communication_protocols.h"

int main(void)
{
  /* STM MCU initialization and configuation */
	initSTM();
	
	/* Bluetooth Communication Routine */
	btRoutine();
	
  while (1);
}
