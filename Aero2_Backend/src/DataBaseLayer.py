import AzureSQLHandler as sql

'''
Database Layer allows functions for accessing the SQL Database at a higher abstraction level and easy-access
'''


class DataBaseLayer:

    # Default Table names
    SAMPLE_TABLE_NAME = "aero2.SampleDataTable"
    RESULTS_TABLE_NAME = "aero2.ResultDataTable"
    PROP_TABLE_NAME = "aero2.ResultDataTable"

    # Default Columns for the tables
    SAMPLE_COLUMNS = "time, lat, long, alt, smog, normalized"
    RESULTS_COLUMNS = "time, lat, long, alt, smog"
    PROP_COLUMNS = "lat, long, alt, r_index, i_index"

    # Threshold Values for Smog
    SMOG_MAX = 10000
    SMOG_MIN = -10

    _sql_handler = None

    '''
    Connects to the Azure SQL server using the AzureSQLHandler class
    Args: self
    Return: None
    '''

    def __init__(self):
        self._sql_handler = sql.AzureSQLHandler()

    '''
    Selects the table according to the where_params
    Args: self, Table Name (String), Where String (String)
	Return: Table (String[][])
    '''

    def select_data(self, table_name, where_params=None):
        if not where_params:
            where_params = ""

        data_table = []
        select_params = "*"

        rows_table = self._sql_handler.select_data(table_name, select_params, where_params)

        if table_name == self.SAMPLE_TABLE_NAME:
            for row in rows_table:
                data_table.append([str(row.time), str(row.lat), str(row.long), str(row.alt), str(row.smog), str(row.normalized)])
        elif table_name == self.RESULTS_TABLE_NAME:
            for row in rows_table:
                data_table.append([str(row.time), str(row.lat), str(row.long), str(row.alt), str(row.smog)])
        elif table_name == self.PROP_TABLE_NAME:
            for row in rows_table:
                data_table.append([str(row.lat), str(row.long), str(row.alt), str(row.r_index), str(row.i_index)])

        return data_table

    '''
    Inserts a new row into the DB table
    Args: self, Table Name (String), List of Data Values (String[])
	Return: Int
    '''

    def insert_row(self, table_name, data_list):
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

    '''
    Inserts multiple rows into the DB table
    Args: self, Table Name (String), Table of Data Values (String[][])
	Return: Int
    '''

    def insert_multiple(self, table_name, data_table):
        i = 0
        for data_list in data_table:
            i += self.insert_row(table_name, data_list)

        return i

    '''
    Deletes rows according to the specified condition(s)
    Args: self, Table Name (String), where_params
	Return: Int
    '''

    def delete_data(self, table_name, where_params):
        return self._sql_handler.delete_data(table_name, where_params)

    '''
    where_param string generator for key value
    Args: self, key name, value
	Return: String
    '''

    @staticmethod
    def key_value_string_gen(key, value):
        return key + " = " + str(float(value))


    '''
    where_param string generator for key range
    Args: self, key name, Max value, Min value
	Return: String
    '''

    @staticmethod
    def key_range_string_gen(key, min_value, max_value):
        return " " + key + " BETWEEN " + str(float(min_value)) + " AND " + str(
            float(max_value))

    '''
    where_param string generator for nearby location
    Args: self, Lat position, long position, X Y range
	Return: String
    '''

    @staticmethod
    def nearby_string_gen(latitude_position, longitude_position, x_y_range):
        return "lat <= " + str(float(latitude_position + x_y_range)) + " AND  lat >= " + str(
                float(latitude_position - x_y_range)) + " AND  long <= " + str(
                float(longitude_position + x_y_range)) + " AND  lat >= " + str(
                float(longitude_position - x_y_range))

    '''
    where_param string generator for different times
    Args: self, date, start_time, end_time
	Return: String
    '''

    @staticmethod
    def when_string_gen(date, start_time=None, end_time=None):
        if not start_time or not end_time:
            return DataBaseLayer.key_range_string_gen('time', float(date), float(date) + 1.0)

        return DataBaseLayer.key_range_string_gen('time', float(date + "." + start_time), float(date + "." + end_time))

    '''
    Validates values provided in the insert function
    Args: self, List of Data Values (String[])
	Return: Int
    '''

    def _validate_data_values(self, data_list):
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

    '''
    Closes the SQL connection
    Args: self
	Return: None
    '''

    def close_connection(self):
        self._sql_handler.close_connection()
