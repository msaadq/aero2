/**
  ******************************************************************************
  * File Name          : main.c
  * Description        : Main program body
  ******************************************************************************
  */
	
/* Includes ------------------------------------------------------------------*/
#include "init.h"
#include "bt_communication_protocols.h"

int main(void) {  

	initSTM(); // STM MCU initialization and configuation
	btRoutine(); // Bluetooth Communication Routine
	
  while (1);
}
