/*
 * USART.h
 *
 * Created: 5/5/2016 6:04:38 PM
 *  Author: Saad
 */ 


#ifndef USART_H_
#define USART_H_

 #include <avr/io.h>
 #include <avr/interrupt.h>
 #include <stdio.h>

/* USART Buffer Defines */
#define USART_RX_BUFFER_SIZE 128     /* 2,4,8,16,32,64,128 or 256 bytes */
#define USART_TX_BUFFER_SIZE 128     /* 2,4,8,16,32,64,128 or 256 bytes */
#define USART_RX_BUFFER_MASK (USART_RX_BUFFER_SIZE - 1)
#define USART_TX_BUFFER_MASK (USART_TX_BUFFER_SIZE - 1)

/* Prototypes */
void USART0_Init(unsigned int ubrr_val);
unsigned char USART0_Receive(void);
void USART0_Transmit(unsigned char data);
int USART0_Transmit_IO(char data, FILE *stream);
void USART_putstring(char* StringPtr);
char* USART_getstring(char escapeSeq);

#endif /* USART_H_ */