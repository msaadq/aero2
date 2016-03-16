'''
lat = 0.000899039377268
long = 0.00107915131727


import Maps as mp

map_handler = mp.Maps()

map_handler.set_intervals(lat, long)

s = []

INDUSTRY_KEYWORDS = ['industry', 'factory', 'manufacturing', 'chemical', 'refinery', 'limited']

for key in INDUSTRY_KEYWORDS:
    result = map_handler._google_places.text_search(key + "in Islamabad")
    for place in result.places:
        s.append([float(place.geo_location['lat']), float(place.geo_location['lng'])])

for coordinates in s:
    print coordinates

'''

import DataTraining as dt

data_train = dt.DataTraining()

data_train.initialize("Islamabad")

