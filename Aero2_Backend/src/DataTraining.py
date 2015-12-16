import Maps.py as maps
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

        return 0

    '''
    Functions inside initialize
    '''

    def _save_all_properties(self, city_name):
        return self._save_nodes_properties(self._map_properties(self._get_all_coordinates(maps.get_corner_points(city_name))))

    def _get_all_coordinates(self, corner_coordinates):
        pass

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











