import googlemaps as gmaps
from googleplaces import GooglePlaces, types, lang as gplaces
import math as m


class Maps:
    """
    This class handles all activity related to Google Maps / Google Places api and provides relevant data for
    the Properties Table
    """

    DEFAULT_GOOGLE_API_KEY = "AIzaSyAxC9UsA68-zYS6aSsjCG5Mi8WDYP3Dxd4"
    INDUSTRY_DISTANCE_THRESHOLD = '1000'
    INDUSTRY_TYPE = 'establishment'

    table_name = 'smogTable'
    partition_key = 'smogValues'
    count = 0

    def __init__(self):
        pass

    # Helper Function
    def find_place(self, key, origin, p_type, radius=INDUSTRY_DISTANCE_THRESHOLD):
        """
        Calls Google Place API and returns the nearest place within
        the radius provided by user.
        Supported ptypes: https://developers.google.com/places/supported_types

        :param key:
        :param origin:
        :param p_type:
        :param radius:

        :return no. of places:
        """

        web_link = "https://maps.googleapis.com/maps/api/place/nearbysearch/xml?key=" + key
        web_link += '&location=' + origin[0] + ',' + origin[1] + '&radius=' + radius + '&type=' + p_type

        print(web_link)

        # index = 0

        source = urllib2.urlopen(web_link)
        tree = bs(source)

        results = tree.findAll('name')
        results_text = [i.getText() for i in results]

        # lat_results = tree.findAll('lat')
        # long_results = tree.findAll('lng')

        # for i in range(0, len(lat_results)):
        #    index += self._calc_distance_on_unit_sphere(float(lat_results[i].getText()),
        #                                                float(long_results[i].getText()), float(origin[0]), float(origin[1]))

        # index = len(lat_results)

        # if (index != 0):
        #    index /= (float(len(lat_results)) * 100)

        # return index

        return len(results_text)

    @staticmethod
    def cal_traffic(key, origin, destination, departure_time='now', traffic_model='best_guess'):
        """
        Calls Google Distance API and returns the duration/km.
        departure_time is number of seconds in int from December, 1970.
        traffic model can be 'best_guess', 'optimistic', or 'pessimistic'

        :param key:
        :param origin:
        :param destination:
        :param departure_time:
        :param traffic_model:
        """

        web_link = 'https://maps.googleapis.com/maps/api/distancematrix/xml?key' + key
        web_link += '&origins=' + origin[0] + ',' + origin[1] + '&destinations=' + destination[0] + ','
        web_link += destination[1]
        # web_link += '&departure_time='+departure_time+'&traffic_model='+traffic_model

        source = urllib2.urlopen(web_link)
        tree = bs(source)
        result = tree.findAll('distance')
        distance = [i.findChild('text').getText() for i in result]

        if len(distance) != 0:
            distance = float((distance[0].split())[0])
        else:
            distance = 1

        result = tree.findAll('duration')
        duration = [i.findChild('text').getText() for i in result]

        if len(duration) != 0:
            duration = float((duration[0].split())[0])
        else:
            duration = 0

        return duration / distance

    # Callable Function
    def cal_data(self, key, origin, latInterval=0.000179807875453, longInterval=0.000215901261691):
        """
        Calculates the number of general contractors and averaged traffic
        density in a given area.

        :param key: Google API Key
        :param origin: Latitude and longitude stored in a list
        :param latInterval: Equivalent latitude distance of a node in degrees
        :param longInterval: Equivalent longitude distance of a node in degrees

        :return General Contractors (int): Is the length of General Contractors within
        the area
        :return Traffic (float): Is an estimate of traffic density at corner points
        """

        corner1 = [(str(float(origin[0]) + latInterval), origin[1])]
        corner2 = [(str(float(origin[0]) - latInterval), origin[1])]
        corner3 = [(origin[0], str(float(origin[1]) + longInterval))]
        corner4 = [(origin[0], str(float(origin[1]) - longInterval))]
        corners = corner1 + corner2 + corner3 + corner4

        try:
            general_contractors = self.find_place(key, origin, 'general_contractor')

            # Calculate traffic at all corners
            traffics = [float(self.cal_traffic(key, origin, i)) for i in corners]
            # Average traffic density
            traffic = sum(traffics) / len(traffics)

        except Exception as e:
            general_contractors = 0
            traffic = 0
            print "Faced a problem with this link"

        Maps.count += 1
        print Maps.count

        return general_contractors, traffic


        # TO DO: Add timestamp here

    def get_corner_coordinates(self, city_name):
        """
        These functions need to be defined here

        :param city_name: (String)
        """

        try:
            geocoder = gmaps.Client(key=self._DEFAULT_GOOGLE_API_KEY)
            geocode_result = geocoder.geocode(city_name)

        except Exception as e:
            return [[]]

        bounds = geocode_result[0]['geometry']['bounds']
        north_east = [bounds['northeast']['lat'], bounds['northeast']['lng']]
        south_west = [bounds['southwest']['lat'], bounds['southwest']['lng']]

        y1, y2, x1, x2 = north_east[0], south_west[0], south_west[1], north_east[1]

        corner_coordinates = [[y1, x1], [y1, x2], [y2, x2], [y2, x1]]

        return corner_coordinates


    def get_road_index(self, node_coordinates):
        # return self.calTraffic('AIzaSyDRhcSUYbhG25wWSKRmvau1GuoXCnnjN8c', node_coordinates)
        return 0

    def get_industry_index(self, node_coordinates):
        # return self.find_place(self.DEFAULT_GOOGLE_KEY_1, node_coordinates, self.INDUSTRY_TYPE)
        return 0

    def get_altitude(self, node_coordinates):
        return 0

    @staticmethod
    def calc_distance_on_unit_sphere(lat1, long1, lat2, long2):

        earth_radius = 6373000
        degrees_to_radians = m.pi / 180.0

        phi1 = (90.0 - lat1) * degrees_to_radians
        phi2 = (90.0 - lat2) * degrees_to_radians

        theta1 = long1 * degrees_to_radians
        theta2 = long2 * degrees_to_radians

        cos = (m.sin(phi1) * m.sin(phi2) * m.cos(theta1 - theta2) + m.cos(phi1) * m.cos(phi2))
        arc = m.acos(cos)

        return arc * earth_radius
