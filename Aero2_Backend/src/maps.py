from bs4 import BeautifulSoup as bs
import numpy as np
import pandas as pd
import urllib2

from geopy.geocoders import Nominatim


# Helper Function
def find_place(key, origin, ptype, radius='1000'):
    '''
	Calls Google Place API and returns the nearest place within 
	the radius provided by user.
	Supported ptypes: https://developers.google.com/places/supported_types
	'''

    web_link = 'https://maps.googleapis.com/maps/api/place/nearbysearch/xml?key=' + key
    web_link += '&location=' + origin[0] + ',' + origin[1] + '&radius=' + radius + '&type=' + ptype
    source = urllib2.urlopen(web_link)
    tree = bs(source)

    results = tree.findAll('name')
    results_text = [i.getText() for i in results]

    print results_text

    return results_text


# Helper Function
def calDuration(key, origin, destination, departure_time='now', traffic_model='best_guess'):
    '''
	Calls Google Distance API and returns the duration/km.
	departure_time is number of seconds in int from December, 1970.
	traffic model can be 'best_guess', 'optimistic', or 'pessimistic'
	'''

    web_link = 'https://maps.googleapis.com/maps/api/distancematrix/xml?key' + key
    web_link += '&origins=' + origin[0] + ',' + origin[1] + '&destinations=' + destination[0] + ','
    web_link += destination[1]
    # web_link += '&departure_time='+departure_time+'&traffic_model='+traffic_model

    source = urllib2.urlopen(web_link)
    tree = bs(source)
    result = tree.findAll('distance')
    distance = [i.findChild('text').getText() for i in result]

    if (len(distance) != 0):
        distance = float((distance[0].split())[0])
    else:
        distance = 1

    result = tree.findAll('duration')
    duration = [i.findChild('text').getText() for i in result]

    if (len(duration) != 0):
        duration = float((duration[0].split())[0])
    else:
        duration = 0

    return duration / distance


# Helper Function
def calTraffic(key, origin, increment=0.01):
    '''
	Estimates traffic by averaging duration over 4 different 
	directions from origin.
	'''

    destination1 = [str(float(origin[0]) + increment), origin[1]]
    destination2 = [str(float(origin[0]) - increment), origin[1]]
    destination3 = [origin[0], str(float(origin[1]) + increment)]
    destination4 = [origin[0], str(float(origin[1]) - increment)]

    d1 = calDuration(key, origin, destination1)
    d2 = calDuration(key, origin, destination2)
    d3 = calDuration(key, origin, destination3)
    d4 = calDuration(key, origin, destination4)

    print (d1 + d2 + d3 + d4) / 4
    return (d1 + d2 + d3 + d4) / 4


# Callable Function
def calData(key, origin, delta=0.5, increment=0.02):
    '''
	Calculates traffic and industry information starting from 
	origin and up to delta.
	'''

    origins1 = [(str(float(origin[0]) + i), origin[1]) for i in np.linspace(0, delta, delta / increment)]
    origins2 = [(str(float(origin[0]) - i), origin[1]) for i in np.linspace(0, delta, delta / increment)]
    origins3 = [(origin[0], str(float(origin[1]) + i)) for i in np.linspace(0, delta, delta / increment)]
    origins4 = [(origin[0], str(float(origin[1]) + i)) for i in np.linspace(0, delta, delta / increment)]

    origins = origins1 + origins2 + origins3 + origins4

    general_contractors = [len(find_place(key, i, 'general_contractor')) for i in origins]
    traffics = [calTraffic(key, i) for i in origins]

    print general_contractors, traffics

    # TO DO: Add timestamp here

    df = pd.DataFrame({
        'coordinates': origins,
        'industries': general_contractors,
        'traffic': traffics
    })

    df.to_csv('Data\data.csv')

    return df


def main():
    google_key1 = 'AIzaSyDRhcSUYbhG25wWSKRmvau1GuoXCnnjN8c'
    google_key2 = 'AIzaSyDKQ-7d3pejj3BxgdGusj3djD4ppwWtn2s'
    origin = ['33.6499632', '72.9637728']
    destination = ['33.6499632', '72.9737728']
    radius = '2000'
    ptype = 'chemicals'
    table_name = 'smogTable'
    partition_key = 'smogValues'

    print find_place(google_key1, origin, ptype, radius)

if __name__ == '__main__':
    main()


'''
These functions need to be defined here
'''


def get_corner_coordinates(city_name):
    corner_coordinates = []

    geo_locator = Nominatim()

    try:
        location = geo_locator.geocode(city_name)
    except:
        return [[]]

    center_lat = round(location.latitude, 2)
    center_long = round(location.longitude, 2)

    y1, y2, x1, x2 = round(center_lat + 0.05, 2), round(center_lat - 0.08, 2), round(center_long - 0.08, 2), round(center_long + 0.07, 2)
    
    corner_coordinates = [[y1, x1], [y1, x2], [y2, x2], [y2, x1]]
    
    return corner_coordinates 
    

def get_road_index(node_coordinates):
    pass

def get_industry_index(node_coordinates):
    pass
