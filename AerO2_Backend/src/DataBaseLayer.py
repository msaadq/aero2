import AzureSQLHandler as sql


class DataBaseLayer:
    """
    Database Layer allows functions for accessing the SQL Database at a higher abstraction level and easy-access

    The structure of the SQL Tables are as follows:
        1) SampleDataTable
            0) time (number)
            1) lat (number)
            2) long (number)
            3) alt (number)
            4) smog (number)
            5) normalized (number)

        2) ResultsDataTable
            0) time (number)
            1) lat (number)
            2) long (number)
            3) air_index (number)

        3) PropertiesTable
            0) sampled (number)
            1) lat (number)
            2) long (number)
            3) r_index (number)
            4) i_index (number)
    """
    
    # Default Table names
    SAMPLE_TABLE_NAME = "aero2.SampleDataTable"
    RESULTS_TABLE_NAME = "aero2.ResultDataTable"
    PROP_TABLE_NAME = "aero2.PropertiesTable"

    # Default Columns for the tables
    SAMPLE_COLUMNS = "time, lat, long, alt, smog, normalized"
    RESULTS_COLUMNS = "time, lat, long, air_index"
    PROP_COLUMNS = "sampled, lat, long, r_index, i_index"

    # Threshold Values for Smog
    SMOG_MAX = 10000
    SMOG_MIN = -1

    _sql_handler = None

    def __init__(self):
        """
        Connects to the Azure SQL server using the AzureSQLHandler class

        :return None:
        """
        
        self._sql_handler = sql.AzureSQLHandler()

    def select_data(self, table_name, where_params=None):
        """
        Selects the table according to the where_params

        :param table_name: Table Name (String)
        :param where_params: Where Params (String)

        :return data_table: Table (String[])
        """

        if not where_params:
            where_params = ""

        data_table = []
        select_params = "*"

        rows_table = self._sql_handler.select_data(table_name, select_params, where_params)

        if table_name == self.SAMPLE_TABLE_NAME:
            for row in rows_table:
                data_table.append(
                    [str(row.time), str(row.lat), str(row.long), str(row.alt), str(row.smog), str(row.normalized)])
        elif table_name == self.RESULTS_TABLE_NAME:
            for row in rows_table:
                data_table.append([str(row.time), str(row.lat), str(row.long), str(row.air_index)])
        elif table_name == self.PROP_TABLE_NAME:
            for row in rows_table:
                data_table.append(
                    [str(row.time), str(row.lat), str(row.long), str(row.alt), str(row.r_index), str(row.i_index),
                     str(row.sampled)])

        return data_table

    def insert_row(self, table_name, data_list):
        """
        Inserts a new row into the DB table

        :param table_name: Table Name (String)
        :param data_list: Data List in formatted order (double[])

        :return int:
        """

        if table_name == self.SAMPLE_TABLE_NAME:
            columns_list = self.SAMPLE_COLUMNS
        elif table_name == self.RESULTS_TABLE_NAME:
            columns_list = self.RESULTS_COLUMNS
        elif table_name == self.PROP_TABLE_NAME:
            columns_list = self.PROP_COLUMNS

        columns_string = "(" + columns_list + ")"
        values_string = "(" + ",".join(data_list) + ")"

        if self._validate_data_values(data_list):
            return self._sql_handler.insert_data(table_name, columns_string, values_string)

        return 0

    def insert_multiple(self, table_name, data_table):
        """
        Inserts multiple rows into the DB table

        :param table_name: Table Name (String)
        :param data_table: Table of Data Values (String[][])

        :return Int:
        """

        i = 0
        for data_list in data_table:
            i += self.insert_row(table_name, data_list)

        return i

    def delete_data(self, table_name, where_params):
        """
        Deletes rows according to the specified condition(s)

        :param table_name: Table Name (String)
        :param where_params: Where Params (String)

        :return Int:
        """

        return self._sql_handler.delete_data(table_name, where_params)

    @staticmethod
    def key_value_string_gen(key, value):
        """
        where_param string generator for key value

        :param key: key name (String)
        :param value: the required value for key (String)

        :return String:
        """

        return key + " = " + str(float(value))

    @staticmethod
    def key_range_string_gen(key, min_value, max_value):
        """
        where_param string generator for key range

        :param key: key name (String)
        :param min_value: Max value
        :param max_value: Min value

        :return String:
        """

        return " " + key + " BETWEEN " + str(float(min_value)) + " AND " + str(
            float(max_value))

    @staticmethod
    def nearby_string_gen(latitude_position, longitude_position, x_y_range):
        """
        where_param string generator for nearby location

        :param latitude_position:
        :param longitude_position:
        :param x_y_range:

        :return String:
        """

        return "lat <= " + str(float(latitude_position + x_y_range)) + " AND  lat >= " + str(
            float(latitude_position - x_y_range)) + " AND  long <= " + str(
            float(longitude_position + x_y_range)) + " AND  lat >= " + str(
            float(longitude_position - x_y_range))

    @staticmethod
    def when_string_gen(date, start_time=None, end_time=None):
        """
        where_param string generator for different times

        :param date:
        :param start_time:
        :param end_time:

        :return String:
        """

        if not start_time or not end_time:
            return DataBaseLayer.key_range_string_gen('time', float(date), float(date) + 1.0)

        return DataBaseLayer.key_range_string_gen('time', float(date + "." + start_time), float(date + "." + end_time))

    def _validate_data_values(self, data_list):
        """
        Validates values provided in the insert function. If values do not comply, then they are discarded

        :param data_list: Data List in formatted order (double[])

        :return Int:
        """

        if len(data_list[0]) != 13 or data_list[0][6] != '.':
            return False

        try:
            int(data_list[0][0:6])
            int(data_list[0][7:13])
            float(data_list[1])
            float(data_list[2])
            float(data_list[3])
            float(data_list[4])

        except ValueError:
            return False

        if float(data_list[4]) not in range(self.SMOG_MIN, self.SMOG_MAX + 1):
            return False

        return True

    def close_connection(self):
        """
        Closes the SQL connection

        :return None:
        """

        self._sql_handler.close_connection()
