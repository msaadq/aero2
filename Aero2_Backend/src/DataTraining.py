
import DataBaseLayer as dbl

class DataTraining:

    VERIFICATION_THRESHOLD = 0.8

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
        return self._save_nodes_properties(self._map_properties(self._get_all_coordinates(maps.get_corner_points(city_name))))

    def _get_all_coordinates(self, corner_coordinates):
        all_coordinates = []

        x1, x2, y1, y2 = corner_coordinates[0][1], corner_coordinates[1][1], corner_coordinates[0][0], corner_coordinates[3][0]

        long_interval = abs(x1 - x2) / (self._calc_distance_from_long(x1, x2) / 20.0)
        lat_interval = abs(y1 - y2) / (self._calc_distance_from_lat(y1, y2) / 20.0)

        latitude = y1
        longitude = x1
        
        while latitude < y2 and longitude < x2:
            all_coordinates.append([latitude, longitude])
            latitude += lat_interval
            longitude += long_interval

        return all_coordinates

    def _map_properties(self, nodes_coordinates):
        output_table = []
        i = 0
        for single_coordinates in nodes_coordinates:
            output_table[i][0:2] = single_coordinates
            output_table[i][3] = maps.get_road_index(single_coordinates)
            output_table[i][4] = maps.get_industry_index(single_coordinates)
            i += 1

        return output_table

    def _save_nodes_properties(self, nodes_properties_table):
        pass


    '''
    Functions inside update_database
    '''


    def _normalize_all(self):
        pass

    def _normalize_coordinates(self, coordinates):
        pass

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
        pass

    def _get_table_output(self, table_without_outputs):
        pass

    def _calc_distance_from_lat(self, lat_min, lat_max):
        distance = 400

        return distance

    def _calc_distance_from_long(self, long_min, long_max):
        distance = 400

        return distance