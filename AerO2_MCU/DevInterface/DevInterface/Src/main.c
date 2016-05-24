/*
 * DevInterface.c
 *
 * Created: 4/27/2016 3:44:10 PM
 * Author : Saad Qureshi
 */
 
 #include "bt_communication.h"

int main(void)
{
	Init_BT();

	BT_Routine();
	while(1);
}

/*
int main(void)
{
	Init_ADC();
	USART0_Init(BAUD_PRESCALER);

	while(1)
	{
		char smogData[3];
		int iSmogData = 0;
		
		for(int i = 0; i < 1000; i++) {
			iSmogData += Get_Sensor_Data();
		}
		iSmogData /= 1000;
		sprintf(smogData, "%d", iSmogData);

		USART_putstring("The ADC Value is: ");
		USART_putstring(smogData);
		USART_putstring("       ");
		_delay_ms(500);
	}
} */

//char message[] = "Hello World!";
//
//static FILE mystdout = FDEV_SETUP_STREAM(USART0_Transmit_IO, NULL,
//_FDEV_SETUP_WRITE);
//
//int main(void)
//{
	///* Set the baud rate to the calculated value */
	//USART0_Init(BAUD_PRESCALER);   
//
	//uint16_t u16Data = 10;
//
	//while(1) 
	//{
		//for (uint16_t i = 0; i < 4; i++)
		//{
			//USART_putstring(message);
			////print unsigned integer
			//printf("\nunsigned int = %u and count = %u",u16Data, i);
			//_delay_ms(200);
		//}
		//_delay_ms(1000);
	//}
	//
	//return 0;
//}

/////////////////////////////////////

//char message[] = "Hello World!";
//
//void USART_Init(unsigned int ubrr)
//{
	///*Set baud rate */
	//UBRR0H = (unsigned char) (ubrr >> 8);
	//UBRR0L = (unsigned char) ubrr;
	///* Enable receiver and transmitter */
	//UCSR0B = (1 << RXEN0)|(1 << TXEN0);
	///* Set frame format: 8 data, 1 stop bit */
	//UCSR0C = (3 << UCSZ00);
//}
//
//unsigned char USART_Receive( void )
//{
	///* Wait for data to be received */
	//while (!(UCSR0A & (1<<RXC0)));
	///* Get and return received data from buffer */
	//return UDR0;
//}
//
//void USART_Transmit(unsigned char data)
//{  /* Wait for empty transmit buffer */
	//while (!(UCSR0A & (1<<UDRE0)));
	///* Put data into buffer, sends the data */
	//UDR0 = data;
//}
//
//
//void USART_putstring(char* StringPtr)
//{
	//while(*StringPtr != 0x00){
		//USART_Transmit(*StringPtr);
	//StringPtr++;}	
//}
//
//int main(void)
//{
	//USART_Init(BAUD_PRESCALER);
//
	//while(1)
	//{
		//USART_Transmit(USART_Receive());
		//USART_putstring(message);
	//}
//
//}

//////////////////////////////////////////

//
// GPIO / Interrupts / Low power mode
//
/*
#define F_CPU 16000000UL // 16 MHz

#include <avr/io.h>
#include <avr/delay.h>
#include <util/delay.h>
#include <avr/interrupt.h>
#include <avr/sleep.h>

#define LED_ON				PORTB |= (1 << PORTB5)
#define LED_OFF				PORTB &= ~(1 << PORTB5)
#define LED_TOGGLE			PINB |= (1 << PINB5)
#define SWITCH_PRESSED		!(PINB & (1 << PINB1))

ISR(PCINT0_vect)
{
	if(SWITCH_PRESSED)
	{
		LED_OFF;
	}
	else
	{
		LED_ON;
	}
}

void Interrupt_Init() 
{
	PCMSK0 |= (1 << PCINT1); // PCINT[7:0]: Pin Change Enable Mask 7...0
	PCICR |= (1 << PCIE0);

	sei();
}

void Pins_Init()
{
	DDRB |= (1 << DDB5);
	DDRB &= ~(1 << DDB1);

	PRR |= 0xFF;
}

int main(void)
{
	Pins_Init();
	Interrupt_Init();

	set_sleep_mode(SLEEP_MODE_PWR_DOWN);

    while (1)
	{
		sleep_mode();
	}
}
*/


