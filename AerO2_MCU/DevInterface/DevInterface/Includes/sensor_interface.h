/*
 * sensor_interface.h
 *
 * Created: 5/23/2016 4:50:42 PM
 *  Author: Saad
 */ 


#ifndef SENSOR_INTERFACE_H_
#define SENSOR_INTERFACE_H_

#include <stdint.h>
#include <avr/io.h>

#define SENSORS_ON			PORTC |= (1 << PORTC4)
#define SENSORS_OFF			PORTC &= ~(1 << PORTC4)

void Init_ADC();

void Enable_Sensors();

void Disable_Sensors();

int Get_Sensor_Data();

#endif /* SENSOR_INTERFACE_H_ */