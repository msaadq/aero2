import math
from bs4 import BeautifulSoup as bs
import numpy as np
import pandas as pd
import urllib2
from geopy.geocoders import Nominatim


class Maps:
    DEFAULT_GOOGLE_KEY_1 = 'AIzaSyDRhcSUYbhG25wWSKRmvau1GuoXCnnjN8c'
    DEFAULT_GOOGLE_KEY_2 = 'AIzaSyDKQ-7d3pejj3BxgdGusj3djD4ppwWtn2s'
    INDUSTRY_DISTANCE_THRESHOLD = '1000'
    INDUSTRY_TYPE = 'chemicals'
    table_name = 'smogTable'
    partition_key = 'smogValues'

    def __init__(self):
        pass

    # Helper Function
    def find_place(self, key, origin, ptype, radius=INDUSTRY_DISTANCE_THRESHOLD):
        """
        Calls Google Place API and returns the nearest place within
        the radius provided by user.
        Supported ptypes: https://developers.google.com/places/supported_types
        :param radius:
        :param ptype:
        :param key:
        :param origin:
        """

        web_link = 'https://maps.googleapis.com/maps/api/place/nearbysearch/xml?key=' + key
        web_link += '&location=' + origin[0] + ',' + origin[1] + '&radius=' + radius + '&type=' + ptype

        print(web_link)

        #index = 0

        source = urllib2.urlopen(web_link)
        tree = bs(source)

        results = tree.findAll('name')
        results_text = [i.getText() for i in results]

 
        #lat_results = tree.findAll('lat')
        #long_results = tree.findAll('lng')

        #for i in range(0, len(lat_results)):
        #    index += self._calc_distance_on_unit_sphere(float(lat_results[i].getText()),
        #                                                float(long_results[i].getText()), float(origin[0]), float(origin[1]))
        
        #index = len(lat_results)
        
        #if (index != 0):                                                
        #    index /= (float(len(lat_results)) * 100)

        #return index

        return len(results_text)

    # Helper Function
    def calDuration(self, key, origin, destination, departure_time='now', traffic_model='best_guess'):
        """
        Calls Google Distance API and returns the duration/km.
        departure_time is number of seconds in int from December, 1970.
        traffic model can be 'best_guess', 'optimistic', or 'pessimistic'
        """

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
    def call_traffic(self, key, origin, increment=0.01):
        '''
        Estimates traffic by averaging duration over 4 different
        directions from origin.
        '''

        destination1 = [str(float(origin[0]) + increment), origin[1]]
        destination2 = [str(float(origin[0]) - increment), origin[1]]
        destination3 = [origin[0], str(float(origin[1]) + increment)]
        destination4 = [origin[0], str(float(origin[1]) - increment)]

        d1 = self.calDuration(key, origin, destination1)
        d2 = self.calDuration(key, origin, destination2)
        d3 = self.calDuration(key, origin, destination3)
        d4 = self.calDuration(key, origin, destination4)
        
        return str((d1 + d2 + d3 + d4) / 4)

    # Callable Function
    def calData(self, key, origin, latInterval = 0.000179807875453, longInterval = 0.000215901261691):
        '''
        KEYWORDS:
        Area: Is the space consisting of points within latIntevral 
        & longInterval
        Corner: Is a point located at distance latIntveral or 
        longInterval from origin
        General Contractor: Is the length of General Contractors within
        the area
        Traffics: Is an estimate of traffic density at corner points 
        
        FUNCTION:
        Calculates the number of general contractors and averaged traffic
        density in a given area.
        
        INPUTS:
        Key: Google API Key
        Origin: Latitude and longitude stored in a list
        latInterval: Equivalent latitude distance of a node in degrees
        longInterval: Equivalent longitude distance of a node in degrees
        
        RETURNS:
        General Contractors: int
        Traffic: float 
        '''

        corner1 = [(str(float(origin[0]) + latInterval), origin[1])]
        corner2 = [(str(float(origin[0]) - latInterval), origin[1])]
        corner3 = [(origin[0], str(float(origin[1]) + longInterval))] 
        corner4 = [(origin[0], str(float(origin[1]) - longInterval))] 
        corners = corner1 + corner2 + corner3 + corner4

        general_contractors = self.find_place(key, origin, 'general_contractor')
        
        #Calculate traffic at all corners
        traffics = [float(self.call_traffic(key, i)) for i in corners]
        #Average traffic density
        traffic = sum(traffics)/len(traffics)
        print traffic
        
        return general_contractors, traffic

        # TO DO: Add timestamp here
        
    '''
    These functions need to be defined here
    '''

    @staticmethod
    def get_corner_coordinates(city_name):
        corner_coordinates = []

        geo_locator = Nominatim()

        try:
            location = geo_locator.geocode(city_name)
        except:
            return [[]]

        center_lat = round(location.latitude, 2)
        center_long = round(location.longitude, 2)

        y1, y2, x1, x2 = round(center_lat + 0.05, 2), round(center_lat - 0.08, 2), round(center_long - 0.08, 2), round(
            center_long + 0.07, 2)

        corner_coordinates = [[y1, x1], [y1, x2], [y2, x2], [y2, x1]]

        return corner_coordinates

    def get_nearest_roads(self, node_coordinates):
        pass

    def get_road_index(self, node_coordinates):
        return self.call_traffic('AIzaSyDRhcSUYbhG25wWSKRmvau1GuoXCnnjN8c', node_coordinates)

    def get_industry_index(self, node_coordinates):
        return self.find_place(self.DEFAULT_GOOGLE_KEY_1, node_coordinates, self.INDUSTRY_TYPE)

    def _calc_distance_on_unit_sphere(self, lat1, long1, lat2, long2):

        earth_radius = 6373.0
        degrees_to_radians = math.pi / 180.0

        phi1 = (90.0 - lat1) * degrees_to_radians
        phi2 = (90.0 - lat2) * degrees_to_radians

        theta1 = long1 * degrees_to_radians
        theta2 = long2 * degrees_to_radians

        cos = (math.sin(phi1) * math.sin(phi2) * math.cos(theta1 - theta2) + math.cos(phi1) * math.cos(phi2))
        arc = math.acos(cos)

        return arc * earth_radius
