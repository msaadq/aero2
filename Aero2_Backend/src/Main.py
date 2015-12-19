'''

import DataBaseLayer as dbl

database_layer = dbl.DataBaseLayer()

RESULTS_TABLE_NAME = "aero2.SampleDataTable"

where_string = " smog BETWEEN 4.0 AND 10.0"

print database_layer.delete_data(RESULTS_TABLE_NAME, where_string)


'''

def calc(number):
    sum = 0

    while number / 10 != 0:
        sum += number % 10
        number /= 10

    if sum / 10 != 0:
        sum = calc(sum)

    return sum

print calc(12345)
        
        