import googlemaps as gmaps
from googleplaces import GooglePlaces, types, lang as gplaces
import math as m


class Maps:
    """
    This class handles all activity related to Google Maps / Google Places api and provides relevant data for
    the Properties Table
    """

    GOOGLE_API_KEY = "AIzaSyB_th_xYmO9cXL74QGG0bb34D5vp15UxVc"
    DEFAULT_ROAD_DISTANCE_THRESHOLD = 500
    DEFAULT_INDUSTRY_DISTANCE_THRESHOLD = 1000
    DEFAULT_INDUSTRY_TYPE = 'establishment'

    INDUSTRY_KEYWORDS = ['industry', 'factory', 'manufacturing', 'chemical', 'refinery', 'bank', 'limited']

    _google_maps = None
    _google_places = None

    _is_connected = False

    table_name = 'smogTable'
    partition_key = 'smogValues'
    count = 0

    def __init__(self):
        try:
            self._google_maps = gmaps.Client(key=self.GOOGLE_API_KEY)
            self._google_places = GooglePlaces(self.GOOGLE_API_KEY)
            self._is_connected = True
        except:
            self._is_connected = False

    # Helper Function
    def get_industry_index(self, coordinates):
        """
        Calls Google Place API and returns the nearest place within
        the radius provided by user.
        Supported ptypes: https://developers.google.com/places/supported_types

        :param coordinates:

        :return industry_index:
        """

        distances = []
        intensities = []
        index = 0.0

        nearby_industry_query = self._google_places.nearby_search(
            lat_lng={'lat': coordinates[0], 'lng': coordinates[1]},
            radius=self.DEFAULT_INDUSTRY_DISTANCE_THRESHOLD, types=[types.TYPE_ESTABLISHMENT])

        for place in nearby_industry_query.places:
            # Returned places from a query are place summaries.
            lat = float(place.geo_location["lat"])
            lng = float(place.geo_location["lng"])
            name = str(place.name).lower()
            distance = self.calc_distance_on_unit_sphere(coordinates[0], coordinates[1], lat, lng)

            if distance > 20:
                distances.append(self.calc_distance_on_unit_sphere(coordinates[0], coordinates[1], lat, lng))
            else:
                distances.append(1)

            if any(substring in name for substring in self.INDUSTRY_KEYWORDS):
                intensities.append(100.0)
            else:
                intensities.append(1.0)

        for x in range(0, len(distances)):
            index += 10 * (intensities[x] / distances[x])

        return index

    def get_nearest_roads(self, coordinates):
        pass

    def cal_traffic(self, key, origin, destination, departure_time='now', traffic_model='best_guess'):
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

    def get_corner_coordinates(self, city_name):
        """
        These functions need to be defined here

        :param city_name: (String)

        :return corner_coordinates: (double[][])
        """

        try:
            geocode_result = self._google_maps.geocode(city_name)

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

    def get_altitude(self, node_coordinates):
        return self._google_maps.elevation((node_coordinates[0], node_coordinates[1]))[0]['elevation']

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
