import Maps as map
import DataBaseLayer as dbl
import math


class DataTraining:
    VERIFICATION_THRESHOLD = 0.8

    DEFAULT_TIME_INDEX = 0
    DEFAULT_LAT_INDEX = 1
    DEFAULT_LONG_INDEX = 2
    DEFAULT_ALT_INDEX = 3
    DEFAULT_SMOG_INDEX = 4
    DEFAULT_NORMALIZED_INDEX = 5
    DEFAULT_AIR_INDEX_INDEX = 3
    DEFAULT_R_INDEX_INDEX = 3
    DEFAULT_I_INDEX_INDEX = 4
    DEFAULT_SAMPLED_INDEX = 0

    maps = map.Maps()

    def __init__(self):
        pass

    def initialize(self, city_name):
        return self._save_all_properties(city_name)

    def update_database(self):
        if self._normalize_all() > 0:
            if self._train_system() > self.VERIFICATION_THRESHOLD:
                return self._update_results()

        return -1

    '''
    Functions inside initialize
    '''

    def _save_all_properties(self, city_name):
        return self._get_all_coordinates(self.maps.get_corner_coordinates(city_name))

    def _get_all_coordinates(self, corner_coordinates):
        nodes_coordinates = []

        if corner_coordinates == [[]]:
            return [[]]

        y1, y2, x1, x2 = corner_coordinates[0][0], corner_coordinates[2][0], corner_coordinates[0][1], \
                         corner_coordinates[2][1]

        if x1 > x2:
            x1, x2 = x2, x1
        if y1 > y2:
            y1, y2 = y2, y1

        long_interval = (x2 - x1) / (self._calc_distance_on_unit_sphere(y1, x1, y1, x2) / 20.0)
        lat_interval = (y2 - y1) / (self._calc_distance_on_unit_sphere(y1, x1, y2, x1) / 20.0)

        print self._calc_distance_on_unit_sphere(y2, x1, y2, x1 + long_interval)

        latitude = y2

        while latitude > y1:
            longitude = x1

            while longitude < x2:
                nodes_coordinates.append([latitude, longitude])
                longitude += long_interval

            latitude -= lat_interval

        return nodes_coordinates

    def _map_properties(self, nodes_coordinates):
        output_table = []

        if nodes_coordinates == [[]]:
            return [[]]

        for single_coordinates in nodes_coordinates:
            output_table.append(
                [0, single_coordinates[0], single_coordinates[1], self.maps.get_road_index(single_coordinates),
                 self.maps.get_industry_index(single_coordinates)])

        return output_table

    def _save_nodes_properties(self, nodes_properties_table):
        if nodes_properties_table == [[]]:
            return 0

        return self._database.insert_multiple(self._database.PROP_TABLE_NAME, nodes_properties_table)

    '''
    Functions inside update_database
    '''

    def _normalize_all(self):
        not_normalized_data_table = self._database.select_data(self._database.SAMPLE_TABLE_NAME,
                                                               self._database.key_value_string_gen("normalized", 0))
        normalized_data_table = []
        temp = []
        average = 0.0

        for data_row in not_normalized_data_table:
            temp.append(self._database.select_data(self._database.SAMPLE_TABLE_NAME,
                                                   self._database.nearby_string_gen(data_row[0], data_row[1], 10)))

            for data in temp:
                average += data[self.DEFAULT_SMOG_INDEX]

            average /= len(temp)

            insert_row = [temp[0][DEFAULT_LAT_INDEX], temp[0][DEFAULT_LONG_INDEX], temp[0][DEFAULT_ALT_INDEX], average,
                          1]

            self._database.insert_row(self._database.SAMPLE_TABLE_NAME, insert_row)

    def _train_system(self):
        pass

    def _get_saved_samples(self):
        pass

    def _interpolate_data(self, samples_table_75):
        pass

    def _verify_interpolation(self, samples_table_25):
        pass

    def _get_single_output(self, list_without_output):
        pass

    def _update_results_table(self, samples_table):
        pass

    def _update_results(self):
        pass

    def _get_non_sampled_nodes(self):
        return self._database.select_data(self._database.SAMPLE_TABLE_NAME,
                                          self._database.key_value_string_gen("sampled", 0))

    def _get_table_output(self, table_without_outputs):
        pass

    def _calc_distance_from_lat(self, lat_min, lat_max, ref_long):
        return self._calc_distance_on_unit_sphere(lat_min, ref_long, lat_max, ref_long)

    def _calc_distance_from_long(self, long_min, long_max, ref_lat):
        return self._calc_distance_on_unit_sphere(ref_lat, long_min, ref_lat, long_max)

    @staticmethod
    def _calc_distance_on_unit_sphere(lat1, long1, lat2, long2):

        earth_radius = 6373000
        degrees_to_radians = math.pi / 180.0

        phi1 = (90.0 - lat1) * degrees_to_radians
        phi2 = (90.0 - lat2) * degrees_to_radians

        theta1 = long1 * degrees_to_radians
        theta2 = long2 * degrees_to_radians

        cos = (math.sin(phi1) * math.sin(phi2) * math.cos(theta1 - theta2) + math.cos(phi1) * math.cos(phi2))
        arc = math.acos(cos)

        return arc * earth_radius
