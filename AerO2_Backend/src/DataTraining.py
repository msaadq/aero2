import Maps as mp
import DataBaseLayer as dbl


class DataTraining:
    """
    Data Training class contains methods for Populating the Properties Table according to data provided by
    DataBaseLayer and Maps. It also has methods for Populating the Results Table based on the results provided
    by the ML algorithm
    """

    VERIFICATION_THRESHOLD = 0.8

    _city_name = None

    _maps = mp.Maps()
    _data_base = dbl.DataBaseLayer()

    def __init__(self):
        """
        Empty Constructor
        """

        pass

    def initialize(self, city_name):
        """
        Deletes any previous entries for a city and populates fresh data in the Properties Table with data for a
        particular city.

        :param city_name: City name (String)

        :return no_of_modifications: (Int)
        """

        self._city_name = city_name
        self._delete_city_data(city_name)

        return self._save_all_properties(city_name)

    '''
    Functions inside initialize
    '''

    def _save_all_properties(self, city_name):
        """
        Populates properties data in the Properties Table with data for a
        particular city after appending all the values.

        :param city_name: City name (String)

        :return no_of_modifications: (Int)
        """

        final_table = []
        n_saved = 0

        all_coordinates = self._get_all_coordinates(self._maps.get_corner_coordinates(city_name))

        # Debug Code
        print "Total nodes for this city: " + str(len(all_coordinates))
        all_coordinates = all_coordinates[:30]
        #

        for coordinates in all_coordinates:
            final_table.append([0, coordinates[0], coordinates[1], self._maps.get_altitude(coordinates), 0,
                                self._maps.get_road_index(coordinates), self._maps.get_industry_index(coordinates)])

        t_len = len(all_coordinates)
        for i in range(0, t_len, 1000):
            if (t_len - i) > 1000:
                max_index = 1000
            else:
                max_index = t_len - i

            n_saved += self._data_base.insert_multiple(self._data_base.PROP_TABLE_NAME, final_table[i:i + max_index],
                                                       self._city_name)

        return n_saved

    def _delete_city_data(self, city_name):
        """
        Deletes all associated data of a city in the Properties Table

        :param city_name:

        :return: no_of_deletions:
        """

        return self._data_base.delete_data(self._data_base.PROP_TABLE_NAME,
                                           self._data_base.key_value_string_gen('city', city_name))

    def _get_all_coordinates(self, corner_coordinates):
        """
        Calculates all the node coordinates of a city, separated by 20m using its corner coordinates

        :param corner_coordinates:

        :return: all_coordinates:
        """

        nodes_coordinates = []
        if corner_coordinates == [[]]:
            return [[]]

        y1, y2, x1, x2 = corner_coordinates[0][0], corner_coordinates[2][0], corner_coordinates[0][1], \
                         corner_coordinates[2][1]
        if x1 > x2:
            x1, x2 = x2, x1
        if y1 > y2:
            y1, y2 = y2, y1

        long_interval = (x2 - x1) / (self._maps.calc_distance_on_unit_sphere(y1, x1, y1, x2) / 20.0)
        lat_interval = (y2 - y1) / (self._maps.calc_distance_on_unit_sphere(y1, x1, y2, x1) / 20.0)

        latitude = y2
        while latitude > y1:
            longitude = x1

            while longitude < x2:
                nodes_coordinates.append([latitude, longitude])
                longitude += long_interval

            latitude -= lat_interval

        return nodes_coordinates

    '''
    Update Function
    '''

    def update_database(self):
        if self._normalize_all() > 0:
            if self._train_system() > self.VERIFICATION_THRESHOLD:
                return self._update_results()

        return -1

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

