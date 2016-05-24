/*
 * sensor_interface.c
 *
 * Created: 5/23/2016 4:50:56 PM
 *  Author: Saad
 */ 

#include "sensor_interface.h"

void Init_ADC()
{
	ADCSRA |= ((1<<ADPS2)|(1<<ADPS1)|(1<<ADPS0)); // Prescaler at 128 so we have an 125Khz clock source
	ADMUX |= (1<<REFS0);
	ADMUX &= ~(1<<REFS1)|~(1<<MUX3)|~(1<<MUX2)|~(1<<MUX1)|~(1<<MUX0);                // A Vcc(+5v) as voltage reference
	ADCSRB &= ~((1<<ADTS2)|(1<<ADTS1)|(1<<ADTS0));    // ADC in free-running mode
	ADCSRA |= (1<<ADATE);                // Signal source, in this case is the free-running
	ADCSRA |= (1<<ADEN);                // Power up the ADC
	ADCSRA |= (1<<ADSC);                // Start converting
}


void Enable_Sensors()
{
	SENSORS_ON;
	Init_ADC();
}

void Disable_Sensors()
{
	SENSORS_OFF;
}

int Get_Sensor_Data()
{
	int adc_value = ADCW;
	return adc_value;
}